package bench

import project.{DataUtils, MixedData, NarrowMixedData, NarrowOnlyLongs, NarrowOnlyStrings, OnlyLongs, OnlyStrings}
import project.Implicits._

object InitialDataGenerator {
  def main(args: Array[String]): Unit = {
    DataUtils.toCsvFile[MixedData](Settings.pathString(Settings.InputCsv.mixedData), Settings.recordsCount)
    DataUtils.toCsvFile[OnlyStrings](Settings.pathString(Settings.InputCsv.onlyStrings), Settings.recordsCount)
    DataUtils.toCsvFile[OnlyLongs](Settings.pathString(Settings.InputCsv.onlyLongs), Settings.recordsCount)
    DataUtils.toCsvFile[NarrowMixedData](Settings.pathString(Settings.InputCsv.narrowMixedData), Settings.recordsCount)
    DataUtils.toCsvFile[NarrowOnlyStrings](Settings.pathString(Settings.InputCsv.narrowOnlyStrings), Settings.recordsCount)
    DataUtils.toCsvFile[NarrowOnlyLongs](Settings.pathString(Settings.InputCsv.narrowOnlyLongs), Settings.recordsCount)
  }
}
