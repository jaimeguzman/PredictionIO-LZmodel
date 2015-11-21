package cl.jguzman.piocompressapp



import scala.collection.JavaConversions._



import io.prediction.controller.{Params, P2LAlgorithm,PersistentModel}
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
                        LZModel,
                        Query,
                        PredictedResult] {

  @transient lazy val logger = Logger[this.type]



  def train(sc: SparkContext ,   data: PreparedData): LZModel = {


    System.out.print( "Exsiten muchos puntos que quiero ivnestigar con los RDD" )

    println("\n\n\n")
    val rows  = data.labeledPoints

    println( "la clase de data es.::::" +   data.getClass )


    println(data.labeledPoints.take(0).isEmpty  )
    println(data.labeledPoints.take(0)  )
    println(data.labeledPoints.count()  )
    println(data.labeledPoints.first()  )
    println(data.labeledPoints.toDebugString)
    println(data.labeledPoints.toString() )



    //rows.collect().foreach(a => println(a.toString() ))


    /**

    require(!data.labeledPoints.take(1).isEmpty,
      s"RDD[labeldPoints] in PreparedData cannot be empty." +
        " Please check if DataSource generates TrainingData" +
        " and Preprator generates PreparedData correctly.") **/


    val m = Map[Integer, Integer]()
      var categoricalFeaturesInfo: java.util.Map[Integer,Integer] = mapAsJavaMap[Integer, Integer](m)
      val impurity = "gini"

      val stat=   new Strategy(algo = Classification, impurity = Gini, 5, 3,100, categoricalFeaturesInfo)
      val tree=   new DecisionTree(stat)
//      tree.run()


       new LZModel(sc)





  }






  /**El predictor es la lectura del TRIE LZ ***/

  def predict(model: LZModel, query: Query): PredictedResult = {








    new PredictedResult( 2.0 )

  }



}