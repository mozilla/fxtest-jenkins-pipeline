import groovy.json.JsonOutput
import java.text.SimpleDateFormat

/** Submit build results to Treeherder
 *
 * @param project project to submit results for
 * @param jobSymbol symbol for the job
 * @param jobName name for the job
 * @param artifactPath path for artifact(s) to publish
 * @param logPath path for log(s) to publish
 * @param groupSymbol symbol for the job group
 * @param groupName name for the job group
*/
def call(String project,
         String jobSymbol,
         String jobName,
         String artifactPath = null,
         String logPath = null,
         String groupSymbol = '?',
         String groupName = null) {
  machine = getMachine()
  payload = [
    taskId: UUID.randomUUID().toString(),
    buildSystem: machine['name'],
    origin: [kind: 'github.com', project: project, revision: getRevision()],
    display: [
      jobSymbol: jobSymbol,
      jobName: jobName,
      groupSymbol: groupSymbol],
    state: 'completed',
    result: getResult(),
    jobKind: 'test',
    timeScheduled: getDateTime(currentBuild.timeInMillis),
    timeStarted: getDateTime(currentBuild.startTimeInMillis),
    timeCompleted: getDateTime(currentBuild.startTimeInMillis + currentBuild.duration),
    reason: 'scheduled', // TODO build cause: currentBuild.rawBuild.getCause().getShortDescription()
    productName: project,
    buildMachine: machine,
    runMachine: machine,
    jobInfo: [summary: 'myJobInfoSummary', links: getJobLinks(artifactPath)],
    logs: getLogs(logPath),
    version: 1
  ]

  if ( groupName != null) {
    payload.display.groupName = groupName
  }

  // TODO include ec2-metadata output in payload
  exchange = "exchange/${PULSE_USR}/jobs"
  routingKey = "${PULSE_USR}.${payload.productName}"
  schema = libraryResource 'org/mozilla/fxtest/pulse/schemas/treeherder.json'
  publishToPulse(exchange, routingKey, JsonOutput.toJson(payload), schema)
}

def getMachine() {
  os = System.getProperty("os.name").toLowerCase().replaceAll('\\W', '-')
  version = System.getProperty("os.version").toLowerCase().replaceAll('\\W', '-')
  architecture = System.getProperty("os.arch")
  return [
    name: new URI(env.JENKINS_URL).getHost(),
    platform: [os, version, architecture].join('-'),
    os: os,
    architecture: architecture
  ]
}

def getRevision() {
  return sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
}

def getResult() {
  switch(currentBuild.result) {
    case 'FAILURE':
    case 'UNSTABLE':
      return 'fail'
    case 'SUCCESS':
    case null:
      return 'success'
    default:
      return 'unknown'
  }
}

def getDateTime(timeInMillis) {
  time = new Date(timeInMillis)
  return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(time)
}

def getJobLinks(artifactPath) {
  links = [[url: env.BUILD_URL, linkText: env.BUILD_TAG, label: 'build']]
  if ( artifactPath != null ) {
    artifactLinks = publishToS3(artifactPath, 'net-mozaws-stage-fx-test-treeherder')
    for (link in artifactLinks) {
      links.add([url: link.url, linkText: link.name, label: 'artifact uploaded'])
    }
  }
  return links
}

def getLogs(logPath) {
  links = []
  if ( logPath != null ) {
    logLinks = publishToS3(logPath, 'net-mozaws-stage-fx-test-treeherder')
    for (link in logLinks) {
      links.add([url: link.url, name: link.name])
    }
  }
  return links
}
