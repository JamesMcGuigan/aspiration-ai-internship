package module_1

object CubeCalculator extends App {

  //// NOTE: App automatically apply this wrapper
  //override def main(args: Array[String]): Unit = {
    for(n <- 1 to 10) {
      println(s"$n^3 = " + this.cube(n))
    }
  //}

  def cube(x: Int) = {
    x * x * x
  }
}
