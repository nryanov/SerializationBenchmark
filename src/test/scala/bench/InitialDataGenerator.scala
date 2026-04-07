package bench

import project.{DataUtils, MixedData, OnlyLongs, OnlyStrings}
import project.Implicits._

object InitialDataGenerator {
  def main(args: Array[String]): Unit = {
    DataUtils.toCsvFile[MixedData](Settings.pathString(Settings.InputCsv.mixedData), Settings.recordsCount)
    DataUtils.toCsvFile[OnlyStrings](Settings.pathString(Settings.InputCsv.onlyStrings), Settings.recordsCount)
    DataUtils.toCsvFile[OnlyLongs](Settings.pathString(Settings.InputCsv.onlyLongs), Settings.recordsCount)
  }
}
