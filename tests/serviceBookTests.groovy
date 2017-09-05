package tests

import org.junit.Before
import org.junit.Test

import com.lesfurets.jenkins.unit.BasePipelineTest

class ServiceBookTests extends BasePipelineTest {


    @Override
    @Before
    void setUp() throws Exception {
        super.setUp()
    }

    @Test
    void allowedOrgs() {
        def script = loadScript('src/org/mozilla/fxtest/ServiceBook.groovy')
        for ( i in ['Kinto', 'mozilla', 'mozilla-services'] ) {
          assert script.validURL("https://github.com/${i}/foo") == true
        }
    }

    @Test
    void disallowedOrgs() {
        def script = loadScript('src/org/mozilla/fxtest/ServiceBook.groovy')
        for ( i in ['kinto', 'Mozilla', 'davehunt'] ) {
          assert script.validURL("https://github.com/${i}/foo") == false
        }
    }

    // @Test
    // void projectWithTests() {
    //     def resp = [body:'{"data": [{"foo": {"tests": [{"name": "bar", "jenkins_pipeline": true}]}}]}']
    //     def script = loadScript('src/org/mozilla/fxtest/ServiceBook.groovy')
    //     script.doGetHttpRequest = { String url -> [resp] }
    //     def tests = script.getProjectTests('foo')
    //     assert tests != null
    // }
}
