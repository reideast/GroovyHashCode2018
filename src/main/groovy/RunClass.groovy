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
            city.rides = []
            lines.drop(1).each { line ->
                city.rides.add(new Ride(line.split()))
            }
            def fleet = new Fleet(numCars: params[2].toInteger())
            fleet.cars = []
            fleet.numCars.times { fleet.cars.add(new Car(x: 0, y: 0)) }
            def numRides = params[3].toInteger()
            def perRideBonus = params[4].toInteger()
            def stepsMax = params[5].toInteger()

            // verify input
            printState(city, fleet)

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

    def static void printState(City city, Fleet fleet) {
        println "City of size $city.rows, $city.cols"
        city.rides.eachWithIndex { it, i -> println "$i: $it" }
        fleet.cars.eachWithIndex { it, i -> println "$i: $it" }
    }

    class Car {
        int x, y

        String toString() { "Car{ at ($x, $y) }" }
    }

    class Fleet {
        int numCars
        List<Car> cars
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

        String toString() { "Ride{ from ($a, $b) to ($x, $y), start=$start, fin=$fin}" }
    }

    class City {
        int rows
        int cols
        List<Ride> rides
    }
}
