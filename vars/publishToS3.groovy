/** Publish files to an Amazon S3 bucket
 *
 * @param path path to the file(s) to publish
 * @param bucket bucket and destination for file(s)
 * @param region region bucket belongs to
 * @return list of [name, url] maps for published file(s)
*/
def call(String path, String bucket, String region = 'us-east-1') {
  step([$class: 'S3BucketPublisher',
    consoleLogLevel: 'INFO',
    dontWaitForConcurrentBuildCompletion: false,
    entries: [[
      bucket: "$bucket/${BUILD_TAG}",
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
    pluginFailureResultConstraint: 'SUCCESS',
    profileName: 'fx-test-jenkins-s3-publisher'])
  return getLinks(bucket, path)
}

def getLinks(bucket, path) {
  def links = []
  def files = findFiles(glob: path)
  for ( f in files ) {
    links.add([
        name: f.name,
        url: "https://s3.amazonaws.com/$bucket/${BUILD_TAG}/$f.name"])
  }
  return links
}
