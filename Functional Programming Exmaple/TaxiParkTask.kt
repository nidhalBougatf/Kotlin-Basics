package taxipark

import jdk.nashorn.internal.objects.NativeArray


/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver>
        {
           return allDrivers.filter { !trips.map { it.driver }.toSet().contains(it) }.toSet()
        // OR allDrivers.filter { c -> trips.none{it.driver==c}}.toSet()

        }


/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger>
    {
        if (minTrips==0)
            return allPassengers

        val listPassengers= trips.map{ it.passengers}.flatten()
        fun getNumberTrips (p:Passenger)= listPassengers.filter{e -> e==p}.count()
        val PassengersWithNumberOfTrips = listPassengers.associate{ it to getNumberTrips(it) }
        val PassengersWithMinTrips =   PassengersWithNumberOfTrips.filter { it.value >= minTrips }.map {it.key}.toSet()

        return PassengersWithMinTrips

    }

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger>
{
    val DriverListOfPassengers = trips.filter { it.driver == driver }.map{it.passengers}.flatten()
    fun getNumberTrips (p:Passenger)= DriverListOfPassengers.filter{e -> e==p}.count()
    return  DriverListOfPassengers.associate { it to getNumberTrips(it) }.filter { it.value > 1 }.map {it.key}.toSet()
}


/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger>
{
    fun TripsWithDiscount(p:Passenger)=trips.filter{it.discount!=null}
                                        .map{ it.passengers}.flatten().filter{e -> e==p}.count()
    fun TripsWithoutDiscount(p:Passenger)=trips.filter{it.discount==null}
            .map{ it.passengers}.flatten().filter{e -> e==p}.count()

    return trips.map{ it.passengers}.flatten().filter {TripsWithDiscount(it) > TripsWithoutDiscount(it) }.toSet()
}


/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    if (trips.isEmpty())
        return null
    // 1- Creating a list of ranges ( ranges from 0 to trips MaxDuration value )
    val MaxDuration = trips.sortedByDescending { it.duration }.first().duration
    var DurationList = mutableListOf<IntRange>()
    for( i in  0..MaxDuration step 10 )
        DurationList.add(i..i+9)

    //2- Creeating a map of  (duraionRange,NumberOfTrips)
    fun getNumberOfTripsPerDuration(duration:IntRange) = trips.filter { it.duration in duration}.count()
    val MapTripsDuration = DurationList.associate {it to getNumberOfTripsPerDuration(it) }

        return MapTripsDuration.maxBy { it.value }?.key
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if (trips.isEmpty() || allDrivers.isEmpty())
        return false

    val listOfDriversSortedByIncome = trips
                                    .groupBy { it.driver }
                                    .map { e -> Pair(e.key, e.value.sumByDouble { it.cost }) }
                                    .sortedByDescending { (_, value) -> value }
                                    .toList()

    val twentyPercentDrivers = (allDrivers.count() * 20) / 100

    val BestDrivers = listOfDriversSortedByIncome
                    .take(twentyPercentDrivers)
                    .map { it.first }

    val totalIncome: Double = this.trips.sumByDouble { it.cost }

    val eightyPercentIncome = (totalIncome * 80) / 100

    val incomeByTopDriver = this.trips
            .filter { it.driver in BestDrivers }
            .map { it.cost }
            .sum()

    return incomeByTopDriver >= eightyPercentIncome


}