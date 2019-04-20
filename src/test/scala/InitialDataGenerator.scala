import project.DataUtils

object InitialDataGenerator {
  def main(args: Array[String]): Unit = {
    DataUtils.toCsvFile("50000.csv", 50000)
  }
}
