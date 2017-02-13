import groovy.json.JsonOutput

/** Write capabilities to JSON file
 *
 * @param desiredCapabilities capabilities to include in the file
 * @param path destination for capabilities file
*/
def call(Map desiredCapabilities, String path = 'capabilities.json') {
    defaultCapabilities = [
        build: env.BUILD_TAG,
        public: 'public restricted'
    ]
    capabilities = defaultCapabilities.clone()
    capabilities.putAll(desiredCapabilities)
    json = JsonOutput.toJson([capabilities: capabilities])
    writeFile file: path, text: json
}
