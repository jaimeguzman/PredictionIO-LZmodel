package org.template.classification



import scala.collection.JavaConversions._

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params
import io.prediction.data.storage.BiMap

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.tree.configuration.Strategy
import org.apache.spark.mllib.tree.configuration.Algo
import scala.collection.JavaConverters._
import org.apache.spark.mllib.util.MLUtils

import org.apache.spark.mllib.tree.impurity.{Variance, Entropy, Gini, Impurity}
import org.apache.spark.mllib.tree.configuration.Algo._



import grizzled.slf4j.Logger


case class LZModel(
        val valor: Int
        ) extends Serializable {

//  @transient lazy val itemIntStringMap = itemStringIntMap.inverse

  /*  override def toString = {
    s" productFeatures:"
  }*/
}


case class AlgorithmParams(
                            numAlphabet: Integer
                            ) extends Params

// extends P2LAlgorithm because the MLlib's NaiveBayesModel doesn't contain RDD.






class Algorithm(val ap: AlgorithmParams) extends P2LAlgorithm[PreparedData, LZModel, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext ,   data: PreparedData): Any = {


    // MLLib DecisionTree cannot handle empty training data.
    require(!data.labeledPoints.take(1).isEmpty,
      s"RDD[labeldPoints] in PreparedData cannot be empty." +
        " Please check if DataSource generates TrainingData" +
        " and Preprator generates PreparedData correctly.")

    var m=Map[Integer,Integer]()
    var categoricalFeaturesInfo: java.util.Map[Integer,Integer] = mapAsJavaMap[Integer, Integer](m)
    val impurity = "gini"

    //val stat= new Strategy(algo = Classification, impurity = Gini, ap.maxDepth, ap.numClasses,ap.maxBins, categoricalFeaturesInfo)
    //val tree=new DecisionTree(stat)
    //tree.run(data.labeledPoints)



  }







  def predict(model: DecisionTreeModel, query: Query): PredictedResult = {
    val label = model.predict(Vectors.dense(query.features))
    new PredictedResult(label)

  }



}