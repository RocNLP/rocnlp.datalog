package org.rocnlp.datalog.model

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection

/**
 * Created by mechko on 3/12/15.
 */

case class Param(name : String, value : Double)

case class Experiment(
                       version : String,
                       dataset : String,
                       features : Vector[String],
                       parameters : Vector[Param],
                       correct : Int,
                       tagged : Int,
                       total : Int,
                       time : Long = System.currentTimeMillis(),
                       valid : Boolean = true)

//Use DB as the project and Collection as the classifier
object Experiment {
  def apply(db : String, coll : String) =  new SalatDAO[Experiment, Int](collection = MongoConnection()(db)(coll) ) {}
}

/**
 * Assumes the existence of a complete and good gitignore
 */
object Versioner {
  import sys.process._
  def apply() : String = {
    val add = "git add ." !
    val commit = "git commit -m \"testing\"" !
    val head = "git rev-parse head" !!

    head
  }
}

object Test extends App {
  val e = Experiment("test", "experiment")

  val p1 = Param("a", 1)
  val p2 = Param("b", 2)

  val exp = Experiment("0.01", "semcor-json", Vector(),Vector(p1, p2), 1,1,1)

  e.insert(exp)

  println(Versioner())
}