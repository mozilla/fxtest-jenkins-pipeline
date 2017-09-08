package tests

import org.junit.Before
import org.junit.Test

import com.lesfurets.jenkins.unit.BasePipelineTest

class ServiceBookTests extends BasePipelineTest {


    @Override
    @Before
    void setUp() throws Exception {
        super.setUp()
        String fileContents = new File('tests/projects.json').text
        def resp = [content: fileContents]
        helper.registerAllowedMethod('httpRequest', [String.class], {url -> resp})
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

    @Test
    void projectWithTests() {
        def script = loadScript('src/org/mozilla/fxtest/ServiceBook.groovy')

        // kinto has 1 jenkins enabled project
        def tests = script.getProjectTests('kinto')
        assert tests != null
        assert tests.size() == 1

        // this project does not exists
        assert script.getProjectTests('IDONTEXIST') == null

        // Balrog has zero jenkins tests
        def balrog = script.getProjectTests('Balrog')
        assert balrog.size() == 0
    }
}
