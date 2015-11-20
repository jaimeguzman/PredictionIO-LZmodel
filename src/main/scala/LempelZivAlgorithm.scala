package org.template.classification



import scala.collection.JavaConversions._

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params
import io.prediction.data.storage.BiMap

import org.apache.spark.SparkContext

import org.apache.spark.mllib.linalg.Vectors

import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.tree.configuration.Strategy
import org.apache.spark.mllib.tree.configuration.Algo
import scala.collection.JavaConverters._
import org.apache.spark.mllib.util.MLUtils

import org.apache.spark.mllib.tree.impurity.{Variance, Entropy, Gini, Impurity}
import org.apache.spark.mllib.tree.configuration.Algo._



import grizzled.slf4j.Logger


class LZModel(
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







abstract class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm[ PreparedData,
                        Any,
                        Query,
                        PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc: SparkContext ,   data: PreparedData): Any = {


    logger.debug( "::::::  en TRAIN  ::::" )



    System.out.println("Hola ")





  }






  /**El predictor es la lectura del TRIE LZ ***/
  def predict(model: Any, query: Query): PredictedResult = {

    System.out.println("Entro al predictor" )





    val label =  6.9 //"esto es un etiqueta " //model.predict(Vectors.dense(query.features))
    new PredictedResult(label)



  }



}