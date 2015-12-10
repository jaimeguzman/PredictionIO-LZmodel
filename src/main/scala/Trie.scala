package cl.jguzman.piocompressapp



import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack
import scala.util.Random


object Trie {
  def apply() : Trie = new TrieNode()
}

sealed trait Trie extends Traversable[String] {

  def append(key : String)
  def findByPrefix(prefix: String): scala.collection.Seq[String]
  def contains(word: String): Boolean
  def remove(word : String) : Boolean

};

class TrieNode(val char: Option[Char] = None,
               var word: Option[String] = None,
               var counter:Int= 0) extends Trie with Serializable{

  var trieHeigth = 0
  val children: mutable.Map[Char, TrieNode] = new java.util.TreeMap[Char, TrieNode]().asScala

  override def  append(key: String) = {


    @tailrec def appendHelper(node: TrieNode, currentIndex: Int): Unit = {

      if (currentIndex == key.length) {
        //System.out.println( "IF "+currentIndex+" == "+key.length+" - "+node.word+"\t "+node.counter )
        node.counter  += 1
        node.word     = Some(key)
      }else{

        val char      = key.charAt(currentIndex).toLower
        val result    = node.children.getOrElseUpdate(char, { new TrieNode(Some(char)) })

        node.counter  += 1

        //node.counter  += 1
        //System.out.println("char "+char+"\t"+result+"\t "+node.counter )
        //System.out.println( "EL "+currentIndex+" == "+key.length+" - "+node.word+"\t "+node.counter )
        appendHelper(result, currentIndex + 1)
      }


    }
    appendHelper(this, 0)
  }

  override def  foreach[U](f: String => U): Unit = {

    @tailrec def foreachHelper(nodes: TrieNode*): Unit = {
      if (nodes.size != 0) {
        nodes.foreach(node => node.word.foreach(f))
        foreachHelper(nodes.flatMap(node => node.children.values): _*)
      }
    }

    foreachHelper(this)
  }

  override def  findByPrefix(prefix: String): scala.collection.Seq[String] = {

    @tailrec def helper(currentIndex: Int,
                        node: TrieNode,
                        items: ListBuffer[String]): ListBuffer[String] = {
      if (currentIndex == prefix.length) {
        items ++ node
      } else {
        node.children.get(prefix.charAt(currentIndex).toLower) match {
          case Some(child) => helper(currentIndex + 1, child, items)
          case None => items
        }
      }
    }

    helper(0, this, new ListBuffer[String]())
  }

  override def  contains(word: String): Boolean = {

    @tailrec def helper(currentIndex: Int, node: TrieNode): Boolean = {
      if (currentIndex == word.length) {
        node.word.isDefined
      } else {
        node.children.get(word.charAt(currentIndex).toLower) match {
          case Some(child) => helper(currentIndex + 1, child)
          case None => false
        }
      }
    }

    helper(0, this)
  }

  override def  remove(word : String) : Boolean = {

    pathTo(word) match {
      case Some(path) => {
        var index = path.length - 1
        var continue = true

        path(index).word = None

        while ( index > 0 && continue ) {
          val current = path(index)

          if (current.word.isDefined) {
            continue = false
          } else {
            val parent = path(index - 1)

            if (current.children.isEmpty) {
              parent.children.remove(word.charAt(index - 1).toLower)
            }

            index -= 1
          }
        }

        true
      }
      case None => false
    }

  }


  def pathTo( word : String ) : Option[ListBuffer[TrieNode]] = {

    def helper(buffer : ListBuffer[TrieNode], currentIndex : Int, node : TrieNode) : Option[ListBuffer[TrieNode]] = {
      if ( currentIndex == word.length) {
        node.word.map( word => buffer += node )
      } else {
        node.children.get(word.charAt(currentIndex).toLower) match {
          case Some(found) => {
            buffer += node
            helper(buffer, currentIndex + 1, found)
          }
          case None => None
        }
      }
    }

    helper(new ListBuffer[TrieNode](), 0, this)
  }

  override def  toString() : String = s"Trie(char=${char},   \tpage= ${word},\tcounter= ${counter}}) chidls= ${this.children.size}} "

  def printTree[U](f: String => U): Unit = {
    println("epsilon");
    @tailrec def foreachHelper(nodes: TrieNode*): Unit = {
      if (nodes.size != 0) {
        //println("\tSZ"+nodes.size )
        println()
        println("--|")

        nodes.foreach(
          node =>{
            for(i <- 0 to node.children.size ){print("    ")}
          print(" "+node.children.size+"_[" )
          node.word.foreach(f)
          print("]#"+node.counter )
        }  )
        foreachHelper(nodes.flatMap(node => node.children.values): _*)

      }
    }

    foreachHelper(this)
  }


  /*
  *@TODO:
  *  Need to work here, the primary idea is get the frecuency
  *  of ocurrency for caculate the probability
  * */

  def updateCounters[U](f: String => U): Unit = {
    @tailrec def foreachHelper(nodes: TrieNode*): Unit = {
      if (nodes.size != 0) {
        //println("\tSZ"+nodes.size )
        println()


        nodes.foreach(
          node =>{

            val aux = node.word
            if( aux!=None ) {
              node.children.foreach {
                case (key, value) =>
                  if( value.word != None){
                    print("\t" + key)
                    println("\t aux\t"
                      + aux.get.toLowerCase
                      + "\tvalue\t" + value.word.get.toLowerCase)



                    val chars = value.word.get.toCharArray
                    for (c <- chars) {
                      val check = c.toLower

                      if (check == key){
                        print("-" + c + "-")
                        node.counter +=1
                        value.counter = 1
                      }

                    }

                    //value.counter += 1
                  }
              }
              println()
            }
            /*
            // node.children.foreach(p => println(">>> key=" + p._1 + ", value=" + p._2))
            //println("********" )
          **/

          }  )
        foreachHelper(nodes.flatMap(node => node.children.values): _*)

      }
    }

    foreachHelper(this)
  }

  /*
  * De Parametro le paso la secuencia a predecir o una letra
  *
  * Idea: Itero todo el arbol
  * me coloco en el que tenga mas hijos
  * busco el que tenga mayor counter
  * lo devuelvo con su valor
  *
  * */


  def predictNextPage[U]( param: String ): String = {

    var currentIndex:Int    = 0
    var nextSymbol:String   = ""
    val resultFindBP        = findByPrefix(param)
    val random              = new Random
    var countPosibility:Int = 0
    val stack               = Stack[String]()
    val alphabet            = Stack[String]()
    var maxProbability:Int      = 0


    //,"R","S","T", "U","V","W","X","Y","Z")
    alphabet.push("A","B","C","D","E","F","G","H","I","J",
      "K","L","M","N","O","P","Q");


    // CUANDO PREGUNTAN POR EPSILON DEBO ENTREGAR DEVOLVERME A LA RAIZ
    // Y PREGUNTAR POR EL SIGUIENTE SIMBOLO CON MEJOR FRECUENCIA
    if( param.length == 0 || param =="" ){
      nextSymbol = alphabet(random.nextInt(alphabet.length))
      var maxProbabilityChildsAfterEpsilon: Int = 0
      var afterEpsilonNodeWithMoreFreq:String = ""

      def helperEpsilon( nodes: TrieNode) = {
         println("HELPER EPSILON CHILD"+ nodes.children.toString() )
        for(  ch <- nodes.children  ){
            if ( ch._2.counter >  maxProbabilityChildsAfterEpsilon && ch._2.word.isDefined ){
              maxProbabilityChildsAfterEpsilon = ch._2.counter
              afterEpsilonNodeWithMoreFreq = ch._2.word.get
            }
        }
      }
      helperEpsilon(this )
      nextSymbol = afterEpsilonNodeWithMoreFreq
    }



    for( t <- 0 until  resultFindBP.length ){

      if( t > 0  ) {
        if( param.length > 1 ) { // SI LA SECUENCIA DE PREGUNTA ES MAYOR QUE 1 SIMBOLO

          countPosibility+=1
          println( "t \t\t"+ resultFindBP(t) )
          //println(  "t.stripPrefix(param) "+ resultFindBP(t).stripPrefix(param) )
          stack.push(resultFindBP(t).stripPrefix(param))
          //nextSymbol = resultFindBP(t).stripPrefix(param)
          nextSymbol = stack(random.nextInt(stack.length))

          println(">>>> NODO INTERMEDIO CON MAS DE UN HIJO query: "+param+"  predict: "+nextSymbol)



        }else {

          println(">>>> EL NODO TIENE MAS DE DOS HIJO, ES UN NODO INTERMEDIO, Y LA SECUENCIA ES DE LARGO 1 - query: " + param + "  predict: " + nextSymbol)
          stack.push(resultFindBP(t).stripPrefix(param))

          /** cuando los leo todos
          if (param.length + 1 == resultFindBP(t).length) {
            // println(">>>"+ nextSymbol  + "\t "+ resultFindBP(t)  )
            //8787888nextSymbol = stack(random.nextInt(stack.length) )

            //println(">>>> PATH TO I READ ALL THE EVENT BEFORE  query: " + param + "  predict: " + nextSymbol)
          }*/

            if( param.length > 0 ){

            pathTo(param) match {
              case Some(param) => {
                var index = 0
                var continue = true
                param(index).word = None

                while (continue) {
                  val current = param(index)

                  if (current.counter > maxProbability && current.word.isDefined) {
                    nextSymbol = current.word.get  //<--- ver si la piso

                    var maxProbabilityChilds: Int = 0
                    val stackEquiprobable = Stack[String]()
                    stackEquiprobable.clear() // LIMPIO ANTES PUSHEAR


                    for (ch <- current.children) {


                      if (ch._2.counter > maxProbabilityChilds) {
                        maxProbabilityChilds = ch._2.counter
                        println(ch._2.word.get)
                      }

                      // IDEALMENTE UN MODELO DE NAVEGACION BASADO EN LZ DEBERIA EVITAR LOS EVENTOS EQUIPROBABLES
                      if (ch._2.counter == maxProbabilityChilds) {
                        maxProbabilityChilds = ch._2.counter
                        println(" Symbolo equiprobables " + ch._2.word.get + "cuando se consulta: " + current.word + "\t" + ch._2.word.get.stripPrefix(current.word.get))
                        stackEquiprobable.push(ch._2.word.get.stripPrefix(current.word.get))
                      }


                      // Si tengo eventos  equiprobables devuelvo solo 1 y vacio
                      if (stackEquiprobable.size > 0) nextSymbol = stackEquiprobable(random.nextInt(stackEquiprobable.length))

                    }
                    continue = false
                  }
                  index += 1
                }
              }
            }
          }
        }
      }
    }

    // Caso de nodo hoja sin hijo y sin simbolo siguiente
    // lo que sucede aca es que epsilon se come el siguiente
    // simbolo y despues todos los eventos son equiporbables
    // por lo cual es un random de 1/17 o 1/alphabet
    if( resultFindBP==""   ){
      nextSymbol = alphabet(random.nextInt(alphabet.length))
      // Aqui hay que poner un some por que en caso de tener dos nodos equiprobables se cae

      println(">>>> NODO HOJA SIN HIJO Y SIN SIMBOLO SIG  query: "+param+"  predict: "+nextSymbol)
    }



    stack.clear()






    @tailrec def predictHelper(nodes: TrieNode*): Unit = {
      if (nodes.size != 0) {

        var aux:Int = 0

        if(param.length > 0 ){
        nodes.foreach(
          node =>{

            var chMatch = param.charAt(currentIndex).toLower

            node.children.get( chMatch ) match {
              case Some(child) =>{
                //println(child +"nodesize "+nodes.size )

                if (child.counter > aux  ){
                  aux = child.counter
                  //nextSymbol = child.word.get
                }

              }
              case None => None
            }
          }
        )
        }

        predictHelper(nodes.flatMap(node => node.children.values): _*)

      }
    }


    //Se me va fuera de rango
    //currentIndex += 1
    predictHelper(this)
    //println(  ">>> predictTo:\t\t what's the next?   "+ param+ "\t ResultPredict: "+ nextSymbol.last.toString +"\t length:  "+ nextSymbol.length+ " of "+nextSymbol )
    //println(  "TRIE \t\t>>>  what's the nextsymbol ?: "+ param+ "\t ResultPredict= "+ nextSymbol+ "\t" )

    nextSymbol
  }


  // Retornar el siguiente simbolo con mayor probabilidad

  def getNodeBySymbol( word : String )  = {
    println("--------------------------- getNodeBySymbo: "+word+" -------------------" )

    var aux:Int = 0
    var nextNode:TrieNode = null
    val random            = new Random


    pathTo(word) match {

      case Some(path) => {
        var index        =  path.length - 1
        var continue     = true
        path(index).word = None

        while ( index > 0 && continue ) {
          val current = path(index) // current node


          //Necesito verificar si el nodo contiene un simbolo ono
          if (current.word.isDefined) {
             println(".>>>> current word IF  para iteracion en ??  "+ current  )
            continue = false

          } else {

            val parent = path(index - 1) // El Padre del nodo con mayo probabilidad


              //actualizo al que tenga el mejor counter
              if (current.counter > aux  ){
                //aux = current.counter // actualizo al actual
                nextNode = current
                //println("Siguiente simbolo con mayor probabilidad dado "+word + " es: \t"+ current.toString() )


                // Cuando tengo mas de un hijo con probabilidades equiprobables o
                if( current.children.size > 0 ){
                    //recorro todos los nodos
                    for( i <- 0 until current.children.size ){
                      if( current.children.toList(i)._2.counter > aux   ){

                        aux = current.children.toList(i)._2.counter
                        nextNode = current.children.toList(i)._2
                        println ("urrent.children(0)    >>< "+  current.children.toList(i)  )
                      }

                    }
                }


              }


            index -= 1
          }
        }

        true
      }
      case None => false
    }

    println( "\n\n\nEl siguiente nodo acorde a la función es: "+ nextNode )

  }


  // Tendria que hacer un metodo que dado Epsilon me retorne el siguiente simbolo con mayor probabilidad.



  /*
  *@TODO:
  */
  def clearTree()  ={

  }




}

