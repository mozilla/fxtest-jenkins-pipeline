/** Submit structured logs to ActiveData
 *
 * @param logPath path to the structured log(s)
*/
def call(String logPath) {
  publishToS3(logPath, 'net-mozaws-stage-fx-test-activedata')
}
