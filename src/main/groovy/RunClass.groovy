class HashCode2018 {
    static void main(String[] args) {
        def files = [
                "a_example",
                "b_should_be_easy",
                "c_no_hurry",
                "d_metropolis",
                "e_high_bonus"
        ]

        files.each { filename ->
            // Read input file
            def lines = new File("${filename}.in").readLines()

            def params = lines[0].split()
            def city = new City(rows: params[0].toInteger(), cols: params[1].toInteger())
            lines.drop(1).eachWithIndex { line, index ->
                city.rides.add(new Ride(line.split(), index))
            }
            def fleet = new Fleet(numCars: params[2].toInteger(), numFreeDrivers: params[2].toInteger())
            fleet.numCars.times { fleet.cars.add(new Car()) }
            def numRides = params[3].toInteger()
            def perRideBonus = params[4].toInteger()
            def stepsMax = params[5].toInteger()

            // verify input
            printState(city, fleet)

            println "simulation starting: " + filename
            for (int step = 0; step < stepsMax; ++step) {
                // first solution: assign drivers in the order in which they are available

                // assign any free drivers
                // expensive, so only look if there are ACTUALLY free drivers
                if (fleet.numFreeDrivers > 0) {
                    fleet.cars.each { car ->
                        if (!car.current) {
                            assignFreeDriver(city, fleet, car)
                        }
                    }
                }

                // step the simulation forward: each car moves once per round
                fleet.cars.each { car ->
                    def ride = car.current
                    if (ride) {
                        if (!ride.isPickedUp) {
                            car.moveTowards(ride.a, ride.b)
                            if (car.a == ride.a && car.b == ride.b)
                                ride.isPickedUp = true
                        } else {
                            car.moveTowards(ride.x, ride.y)
                            if (car.a == ride.x && car.b == ride.y) {
                                car.current = null // dropped off
                                car.listOfRides << ride.id // added to list of completed rides

                                // immediately look for a new ride
                                fleet.numFreeDrivers += 1
                                assignFreeDriver(city, fleet, car)
                            }
                        }
                    }
                }

//                printState(city, fleet, step)
//                println()
            }

            // Output results
            new File("${filename}.out").withWriter { writer ->
                fleet.cars.each { car ->
                    def line = car.listOfRides.size()
                    car.listOfRides.each {
                        line += " $it"
                    }
                    writer.println line
                }
            }
        }
    }

    static void assignFreeDriver(City city, Fleet fleet, Car car) {
        city.rides.find { ride ->
            if (!ride.isServed) {
//                println "Trying to assign $ride"
                car.current = ride
                fleet.numFreeDrivers -= 1
//                println "    Car free, now assigned $car"
                ride.isServed = true
                return true // found a ride to assign to this car
            } else {
                return false // keep looking for a ride that needs to be served
            }
        }
    }

    static void printState(City city, Fleet fleet, int step = -1) {
        println "On step $step"
//        println "City of size $city.rows, $city.cols"
        city.rides.eachWithIndex { it, i -> println "$i: $it" }
        fleet.cars.eachWithIndex { it, i -> println "$i: $it" }
    }
}

class Car {
    int a = 0, b = 0
    def listOfRides = []
    Ride current = null

    String toString() { "Car{ at ($a, $b) ${current == null ? 'is free' : "is busy with ride #${current?.id}"}" }

    // move one space, unless a,b already is x,y (then return true)
    void moveTowards(int x, int y) {
        // doesn't matter how it moves, just move closer in either row or col
//        if (a != x) {
//            a += 1 * ((x - a) > 0 ? 1 : -1)  // don't be overly clever
//        }
        if (a < x)
            ++a
        else if (a > x)
            --a
        else if (b < y)
            ++b
        else if (b > y)
            --b
    }
}

class Fleet {
    int numCars
    List<Car> cars = []
    int numFreeDrivers
}

class Ride {
    int a, b // start intersection
    int x, y // end intersection
    int start // earliest start time
    int fin // latest finish
    int id // the numeric identifier of this ride
    Ride(String[] line, int id) {
        def ints = line.collect { it.toInteger() }
        a = ints[0]
        b = ints[1]
        x = ints[2]
        y = ints[3]
        start = ints[4]
        fin = ints[5]
        this.id = id
    }
    boolean isServed = false
    boolean isPickedUp = false

    String toString() { "Ride{ #$id from ($a, $b) to ($x, $y), start=$start, fin=$fin, ${isServed ? '' : 'has NOT been assigned to a car'}" }
}

class City {
    int rows
    int cols
    List<Ride> rides = []
}
