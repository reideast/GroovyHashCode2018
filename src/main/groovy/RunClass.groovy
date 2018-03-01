class RunClass {
    static void main(String[] args) {
        def files = [
                "a_example"
//                "b_should_be_easy"
        ]

        files.each { filename ->
            // Read input file
            def lines = new File("${filename}.in").readLines()

            def params = lines[0].split()
            def city = new City(rows: params[0].toInteger(), cols: params[1].toInteger())
            lines.drop(1).each { line ->
                city.rides.add(new Ride(line.split()))
            }
            def fleet = new Fleet(numCars: params[2].toInteger())
            fleet.numCars.times { fleet.cars.add(new Car()) }
            def numRides = params[3].toInteger()
            def perRideBonus = params[4].toInteger()
            def stepsMax = params[5].toInteger()

            // verify input
            printState(city, fleet)

            println "simulation starting"
            for (int step = 0; step < stepsMax; ++step) {
                // first solution: assign drivers in the order in which they are available
                city.rides.findAll({!it.isServed}).each { ride ->
                    fleet.cars.find {
                        if (it.current) {
                            false // keep looking
                        } else {
                            it.current = ride
                            ride.isServed = true
                            true // found a free car, stop looping
                        }
                    }
                }


                printState(city, fleet, step)
            }

            // Output results
            new File("${filename}.out").withWriter { writer ->
                lines.each { line ->
                    writer.println line
                }
            }
//            new File("${filename}.out").eachLine {
//                println it
//            }
        }
    }

    static void printState(City city, Fleet fleet, int step = -1) {
        println "On step $step"
        println "City of size $city.rows, $city.cols"
        city.rides.eachWithIndex { it, i -> println "$i: $it" }
        fleet.cars.eachWithIndex { it, i -> println "$i: $it" }
    }
}

class Car {
    int a = 0, b = 0
    def listOfRides = []
    Ride current = null

    String toString() { "Car{ at ($a, $b) ${current == null ? 'is free' : 'is busy'}" }

    // move one space, unless a,b already is x,y
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
}

class Ride {
    int a, b // start intersection
    int x, y // end intersection
    int start // earliest start time
    int fin // latest finish
    Ride(String[] line) {
        def ints = line.collect { it.toInteger() }
        a = ints[0]
        b = ints[1]
        x = ints[2]
        y = ints[3]
        start = ints[4]
        fin = ints[5]
    }
    boolean isServed = false

    String toString() { "Ride{ from ($a, $b) to ($x, $y), start=$start, fin=$fin, ${isServed ? '' : 'has NOT been assigned to a car'}" }
}

class City {
    int rows
    int cols
    List<Ride> rides = []
}
