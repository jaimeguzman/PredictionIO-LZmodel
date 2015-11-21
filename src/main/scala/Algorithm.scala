package cl.jguzman.piocompressapp



import scala.collection.JavaConversions._



import io.prediction.controller.{Params, P2LAlgorithm}
import org.apache.spark.SparkContext

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.tree.configuration.Strategy



import org.apache.spark.mllib.tree.impurity.{Variance, Entropy, Gini, Impurity}
import org.apache.spark.mllib.tree.configuration.Algo._

import scala.util.Random

import grizzled.slf4j.Logger

import io.prediction.controller.PersistentModel



/***
 * Parametros que le estoy pasando al algoritmo
 * Estos se pueden ir declarando en el engine.json adicionalmente
 **/
case class AlgorithmParams(
  numAlphabet: Integer

) extends Params



 class LZModel(
     val sc: SparkContext
     ) extends PersistentModel[AlgorithmParams] with Serializable {
  @transient lazy val logger = Logger[this.type]
  def save(id: String, params: AlgorithmParams, sc: SparkContext): Boolean = {
    false
    }
  }



 class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm [PreparedData,
                        DecisionTreeModel,
                        Query,
                        PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext ,   data: PreparedData): DecisionTreeModel = {


    System.out.print( "Exsiten muchos puntos que quiero ivnestigar con los RDD" )

    println("\n\n\n")
    val rows  = data.labeledPoints

    println( "la clase de data es.::::" +   data.getClass )



    println(data.labeledPoints.take(0)  )
    println(data.labeledPoints.take(1)  )
    println(data.labeledPoints.take(2)  )



    println(data.labeledPoints.take(0).isEmpty  )
    println(data.labeledPoints.take(1).isEmpty )
    println(data.labeledPoints.take(2).isEmpty  )



    //rows.collect().foreach(a => println(a.toString() ))




/**

    require(!data.labeledPoints.take(1).isEmpty,
      s"RDD[labeldPoints] in PreparedData cannot be empty." +
        " Please check if DataSource generates TrainingData" +
        " and Preprator generates PreparedData correctly.") **/




      var m=Map[Integer,Integer]()
      var categoricalFeaturesInfo: java.util.Map[Integer,Integer] = mapAsJavaMap[Integer, Integer](m)
      val impurity = "gini"

      val stat=   new Strategy(algo = Classification, impurity = Gini, 5, 3,100, categoricalFeaturesInfo)
      val tree=   new DecisionTree(stat)
      tree.run(data.labeledPoints)








  }






  /**El predictor es la lectura del TRIE LZ ***/
  def predict(model: DecisionTreeModel, query: Query): PredictedResult = {








    new PredictedResult( 2.0 )
  }







}