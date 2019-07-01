package module_1

class CubeCalculatorTest extends org.scalatest.FunSuite {
  test("CubeCalculator.cube") {
    assert(CubeCalculator.cube(3) === 27)
  }
}
