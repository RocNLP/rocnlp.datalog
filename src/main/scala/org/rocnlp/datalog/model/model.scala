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

  //Only includes features for now
  def byFeatures(features : Vector[String], only : Boolean = false) : List[Experiment] = {

    val onlyQ = ("features" $size features.size)
    val query = ("features" $all features)
    if (only) {
      dao.find(query ++ onlyQ).toList
    }
    else dao.find(query).toList
  }
  def byParams(params : Vector[Param], only : Boolean = false) : List[Experiment] = {

    val onlyQ = ("params" $size params.size)
    val query = ("parameters" $all params.map(grater[Param].asDBObject(_)))

    if (only) {
      dao.find(query ++ onlyQ).toList
    } else {
      dao.find(query).toList
    }
  }
  def byDataset(dataset : String) : List[Experiment] = dao.find(MongoDBObject("dataset" -> dataset)).toList
  def byVersion(version : String) : List[Experiment] = dao.find(MongoDBObject("version" -> version)).toList

  private def best(experiments : List[Experiment]) : Experiment = {
    experiments.maxBy(e => (e.correct*1.0)/e.tagged)
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
  def git() : String = {
    //You should commit and comment yourself so that you can invalidate experiments at will
    val add = "git add ." !
    val commit = "git commit -m \"testing\"" !
    val head = ("git rev-parse head" !!).stripLineEnd
    apply(head).id
  }

  def apply(head : String) : Version = {

    dao.findOne(MongoDBObject("head" -> head)) match {
      case Some(version) => version
      case None => {
        val version = Version(head, true, System.currentTimeMillis())
        dao.insert(version)
        version
      }
    }
  }

  def get(head : String) : String = apply(head).id
}

object Test{
  val e = new ExperimentDAO("test", "experiment")

  val p1 = Param("a", 1)
  val p2 = Param("b", 2)

  val exp = Experiment(Version.git(), "json2", Vector("someFeat"),Vector(p1, p2), 1,1,1)
  val exp2 = Experiment(Version.git(), "semcor-json", Vector(),Vector(p1), 1,1,1)

  e.insert(exp)

  println(e.byFeatures(Vector("someFeat")))
}