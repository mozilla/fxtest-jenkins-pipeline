package org.mozilla.fxtest
import groovy.json.JsonSlurperClassic;

// Servicebook iterator to get operational tests for a given project
Object getProjectTests(String name) {
    def projectName = name.toLowerCase();
    def resp = httpRequest "https://servicebook-api.stage.mozaws.net/api/project";
    def jsonSlurper = new JsonSlurperClassic();
    def projects = jsonSlurper.parseText(resp.content);

    for (project in projects.data) {
        if (project.name.toLowerCase() == projectName) {
            echo "The project " + name + " was found!"

            def jenkin_tests = [];
                for (test in project.tests) {
                    if (test.jenkins_pipeline) {
                        echo "Adding one test " + test.name
                        jenkin_tests << test;
                    }
                }
            return jenkin_tests;
        }
    }
    echo "The project " + name + " was not found"
    return null;
}



def validURL(url) {
    allowedOrgs = ['Kinto', 'mozilla', 'mozilla-services']

    for (allowedOrg in allowedOrgs) {
        if (url.startsWith('https://github.com/' + allowedOrg + '/')) {
            return true
        }
    }
    echo url + " is not a valid url"
    return false
}


def runStage(test) {
    stage(test.name) {
        if (validURL(test.url)) {
            echo "checking out " + test.url + ".git"
            node {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [[$class: 'CleanCheckout']],
                    submoduleCfg: [],
                    userRemoteConfigs: [[url: test.url + '.git']]
                ])
            }
            echo "checked out"
            node {
                sh "chmod +x run"
                sh "${WORKSPACE}/run"
            }
        } else {
            throw new IOException(test.url + " is not allowed")
        }
    }
}

def testProject(String name) {
    echo "Testing project " + name
    def failures = []
    def tests = getProjectTests(name)

    for (test in tests) {
        try {
            echo "Running " + test
            echo "URL is " + test.url
            runStage(test)
        } catch (exc) {
            echo test.name + " failed"
            echo "Caught: ${exc}"
            failures.add(test.name)
        }
    }
    stage('Ship it!') {
        node {
            if (failures.size == 0) {
                sh 'exit 0'
            } else {
                sh 'exit 1'
            }
        }
    }
}


return this;
