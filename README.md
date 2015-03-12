# rocnlp.datalog
A simple datalogger using MongoDB, Casbah and Salat to store results of evolving classifier projects.

This is very personalized: help generalizing would be appreciated.

## Basic Usage:

```scala
/* 
Initialize your DAO.  new ExperimentDAO(mongoDBName, collectionName)
I use the dbname as the project and the collection name for the method
*/
val e = new ExperimentDAO("test", "experiment")

val p1 = Param("a", 1)
val p2 = Param("b", 2)

/*
Version() commits the code in the current repo (warning this could cause you problems if you're not prepared)
The version is set as the commit id of the head of the current branch.  You could always use your own version number instead (a string)

Version stops the same results being logged as different results.  A new result is only inserted if there isn't an exact match (probabilistic algorithms beware or rejoice as necessary)

*/
val exp = Experiment(Version(), "json2", Vector("someFeat"),Vector(p1, p2), 1,1,1)
val exp2 = Experiment(Version(), "semcor-json", Vector(),Vector(p1), 1,1,1)

e.insert(exp)

/*
There are some nice search features.  I'll be working on a scala play frontend for viewing and visualizing results
*/

println(e.byFeatures(Vector("someFeat")))
```

