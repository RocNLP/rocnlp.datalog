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
                       total : Int
                       )
//Use DB as the project and Collection as the classifier
class ExperimentDAO(db : String, coll : String) {
  var dao = new SalatDAO[Experiment, Int](collection = MongoConnection()(db)(coll) ) {}

  def insert(experiment : Experiment) = {
    //Don't insert if exact match exists
    val exists = dao.findOne(grater[Experiment].asDBObject(experiment))

    exists match {
      case Some(_) => //do nothing since this is a duplicate
      case None => dao.insert(experiment)
    }
  }
}

/**
 * Assumes the existence of a complete and good gitignore
 */

case class Version(@Key("_id") id : String, valid : Boolean = true, time : Long = System.currentTimeMillis())

/**
 * Sets and gets a unique version of this code using git
 */
object Version {
  val dao = new SalatDAO[Version, String](collection = MongoConnection()("meta")("versions")) {}
  import sys.process._
  def apply() : String = {
    val add = "git add ." !
    val commit = "git commit -m \"testing\"" !
    val head = "git rev-parse head" !!

    dao.findOne(MongoDBObject("head" -> head)) match {
      case Some(version) => head
      case None => {
        val version = Version(head)
        dao.insert(version)
        head
      }
    }
  }
}

object Test extends App {
  val e = new ExperimentDAO("test", "experiment")

  val p1 = Param("a", 1)
  val p2 = Param("b", 2)

  val exp = Experiment(Version(), "semcor-json", Vector(),Vector(p1, p2), 1,1,1)

  e.insert(exp)
}