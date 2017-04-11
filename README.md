# Shared libraries for Jenkins Pipeline
This repository holds
[shared libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/) for
[Jenkins pipelines](https://jenkins.io/doc/book/pipeline/) used by
[Firefox Test Engineering](https://wiki.mozilla.org/TestEngineering).

## ircNotification
Sends a notification to IRC with the specified `channel`, `nick`, and `server`.
By default it will connect to `irc.mozilla.org:6697` as `fxtest` and join the
`#fx-test-alerts` channel.

### Examples
```groovy
// use defaults
ircNotification()

// specify a channel
ircNotification('#fx-test-alerts')

// specify all values
ircNotification(
  channel: '#fx-test-alerts',
  nick: 'fxtest',
  server: 'irc.mozilla.org:6697'
)
```

## publishToPulse
Publishes a `message`, to [Pulse] with the specified `exchange` and
`routingKey`. If a schema is provided then it will be used to check that the
message is valid. If the message fails to pass validation, details will be
output to the console log and `ProcessingException` will be thrown.

### Requirements
* Pulse credentials to be configured in Jenkins.
* [Pipeline Model Definition Plugin] v1.1.2 or later.
* [Pipeline Utility Steps Plugin].

### Examples
```groovy
// configure environment variables from credentials
environment {
  PULSE = credentials('PULSE')
}

// send message without schema validation
publishToPulse(
  exchange: "exchange/${PULSE_USR}",
  routingKey: "${PULSE_USR}.foo",
  message: 'foo'
)

// send message with schema validation from resources
schema = libraryResource 'org/mozilla/fxtest/pulse/schemas/treeherder.json'
publishToPulse(
  exchange: "exchange/${PULSE_USR}",
  routingKey: "${PULSE_USR}.foo",
  message: 'foo',
  schema: schema
)
```

## publishToS3
Publishes the files at `path` to the specified Amazon S3 `bucket` and `region`.
Defaults to region `us-east-1`.

### Requirements
* [Amazon S3] bucket with appropriate permissions.
* [S3 Plugin] with profile configured in Jenkins.

### Examples
```groovy
// single file with default bucket and region
publishToS3('results.html')

// multiple files with specified bucket and region
publishToS3(
  path: 'results/*',
  bucket: 'foo',
  region: 'bar'
)
```

## submitToActiveData
Publishes the structured log(s) at `logPath` to ActiveData.

### Requirements
See [publishToS3](#publishtos3).

### Examples
```groovy
submitToActiveData('results/raw.txt')
```

## submitToTreeherder
Submits the build result for `project` to [Treeherder] using the specified
`jobSymbol` and `jobName`. If provided, files located by `artifactPath` and
`logPath` will be published to Amazon S3 and linked from the build results. By
default the job will have a `groupSymbol` of 'j', and a `groupName` of
'Executed by Jenkins'. To leave the job ungrouped pass a `groupSymbol` of '?'.

### Requirements
See [publishToS3](#publishtos3) and [publishToPulse](#publishtopulse).

### Examples
```groovy
// submit default grouped build results without artifacts or logs
submitToTreeherder(
  project: 'foo',
  jobSymbol: 'T',
  jobName: 'Tests'
)

// submit ungrouped build results without artifacts or logs
submitToTreeherder(
  project: 'foo',
  jobSymbol: 'T',
  jobName: 'Tests',
  groupSymbol: '?'
)

// submit custom grouped build results with artifacts and log
submitToTreeherder(
  project: 'foo',
  jobSymbol: 'I',
  jobName: 'Integration tests',
  artifactPath: 'results/*',
  logPath: 'results/tbpl.txt',
  groupSymbol: 'T',
  groupName: 'Tests'
)
```

## writeCapabilities
Writes a JSON file containing the items from the `capabilities` map to the
specified `path` (for use by [pytest-selenium]). If omitted, the `path`
defaults to `capabilities.json` in the working directory.

### Examples
```groovy
capabilities = [
  browserName: 'Firefox',
  version: '51.0',
  platform: 'Windows 10'
]

// write capabilities to default path
writeCapabilities(capabilities)

// write capabilities to specified path
writeCapabilities(
  desiredCapabilities: capabilities,
  path: 'fx51win10.json'
)
```

## Version History

### 1.6 (unreleased)
Provide a default group for Treeherder jobs.

### 1.5 (2017-03-31)
Changed S3 profile `fx-test-jenkins-s3-publisher`.

### 1.4 (2017-02-31)
Introduce `publishToS3`, `publishToPulse`, and `submitToTreeherder` steps.

### 1.3 (2017-02-23)
Don't mark jobs as unstable if `submitToActiveData` fails.

### 1.2 (2017-02-22)
Added `submitToActiveData` step for publishing structured logs to S3 for processing by ActiveData.

### 1.1 (2017-02-13)
Changed order of arguments for `ircNotification` for ease of specifying alternate channel.

### 1.0 (2017-02-13)
Initial release with `ircNotification` and `writeCapabilities` steps.

[Pulse]: https://wiki.mozilla.org/Auto-tools/Projects/Pulse
[Pipeline Utility Steps Plugin]: https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Utility+Steps+Plugin
[S3 Plugin]: https://wiki.jenkins-ci.org/display/JENKINS/S3+Plugin
[Amazon S3]: https://aws.amazon.com/s3/
[Treeherder]: https://wiki.mozilla.org/Auto-tools/Projects/Treeherder
[pytest-selenium]: http://pytest-selenium.readthedocs.io/en/latest/user_guide.html#capabilities-files
[Pipeline Model Definition Plugin]: https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Model+Definition+Plugin
