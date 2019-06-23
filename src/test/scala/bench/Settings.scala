package bench

object Settings {
  val recordsCount: Int = 100000
  val flushInterval: Int = 5000

  val benchRuns: Int = 15
  val minWarmupRuns: Int = 3
  val maxWarmupRuns: Int = 3
  val independentSamples: Int = 1
}
