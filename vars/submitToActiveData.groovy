/** Publish a structured log to S3 for processing by ActiveData
 *
 * @param path path to the structured log file
 * @param bucket bucket and destination for structured log
 * @param region region bucket belongs to
*/
def call(String path,
         String bucket = "net-mozaws-stage-fx-test-activedata/${BUILD_TAG}",
         String region = 'us-east-1') {
    step([$class: 'S3BucketPublisher',
      consoleLogLevel: 'INFO',
      dontWaitForConcurrentBuildCompletion: false,
      entries: [[
        bucket: bucket,
        excludedFile: '',
        flatten: true,
        gzipFiles: true,
        keepForever: false,
        managedArtifacts: false,
        noUploadOnFailure: false,
        selectedRegion: region,
        showDirectlyInBrowser: false,
        sourceFile: path,
        storageClass: 'STANDARD',
        uploadFromSlave: false,
        useServerSideEncryption: false]],
      pluginFailureResultConstraint: 'FAILURE',
      profileName: 'ActiveData',
      userMetadata: []])
}
