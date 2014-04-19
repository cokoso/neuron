package tutorials

//import breeze.plot._
import breeze.linalg._
import breeze.stats.distributions._
import neuralnetwork._


// create custom Image AutoEncoder from SparseSingleLayerAE
class ImageAutoEncoder (val rowsMultCols:Int, override val hiddenDimension: Int) 
	extends SparseAutoEncoder (3.0, .0001) (rowsMultCols, hiddenDimension)(){
  type Instance <: InstanceOfImageAutoEncoder
  override def create() = new InstanceOfImageAutoEncoder(this)
}
class InstanceOfImageAutoEncoder (override val NN: ImageAutoEncoder) 
	extends InstanceOfAutoEncoder(NN) //There is no InstanceOfSparseSingleLayerAE
{ 
  type Structure <: ImageAutoEncoder
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    	val p = new java.io.PrintWriter(f)
    	try { op(p) } finally { p.close() }
  }
  
  def displayHiddenNetwork (filename: String) : Unit = { 
    val weightsVector = new WeightVector((NN.rowsMultCols)*NN.hiddenDimension)
    val raw = NN.inputLayer.W.vec() // getRandomWeights((System.currentTimeMillis()%100000).toString) // load in optimized weights
    weightsVector := raw.asWeight(NN.hiddenDimension, NN.rowsMultCols).transpose.vec(false)
    
    import java.io._
    printToFile(new File(filename))(p =>    
    for (i<- 0 until NN.hiddenDimension) { // display by hidden nodes
      val imgNull = new Weight(0,0)
      val img = new NeuronVector(NN.rowsMultCols)//
      weightsVector(imgNull, img)
      //println(img.vec.data)
      p.println((img.data/norm(img.data)).data.mkString("\t")) // Just print
    })
  }
}

object ImageAutoEncoderTest extends Optimizable {
	def main(args: Array[String]): Unit = {
	  val rows = 8
	  val cols = 8
	  val hidden = 25
	  
	  val dataSource = scala.io.Source.fromFile("data/UFLDL/sparseae/patches64x10000.txt").getLines.toArray
	  val numOfSamples = dataSource.length
	  xData = new Array(numOfSamples)
	  for (i<- 0 until numOfSamples) {
	    xData(i) = new NeuronVector(
	        new DenseVector(dataSource(i).split("\\s+").map(_.toDouble), 0, 1, rows*cols))
	  }
	  yData = xData
	  
	  nn = new ImageAutoEncoder(rows*cols, hidden).create() // the same

	  
	  val w = getRandomWeightVector()
	  var time:Long = 0
	  
	  time = System.currentTimeMillis();
	  val (obj, w2) = train(w)
	  println(System.currentTimeMillis() - time, obj)
	  
	  nn.asInstanceOf[InstanceOfImageAutoEncoder]
	  	.displayHiddenNetwork("data/UFLDL/sparseae/results25.txt")

	}
}