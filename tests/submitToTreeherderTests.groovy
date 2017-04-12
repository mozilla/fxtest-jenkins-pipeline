package tests

import org.junit.Before
import org.junit.Test

import com.lesfurets.jenkins.unit.BasePipelineTest

class SubmitToTreeherderTests extends BasePipelineTest {


    @Override
    @Before
    void setUp() throws Exception {
        super.setUp()
    }

    @Test
    void defaultGroup() {
        def script = loadScript('vars/submitToTreeherder.groovy')
        def display = script.getDisplay('myJobSymbol', 'myJobName')
        assert display.groupSymbol == 'j'
        assert display.groupName == 'Executed by Jenkins'
    }

    @Test
    void customGroup() {
        def script = loadScript('vars/submitToTreeherder.groovy')
        def display = script.getDisplay('myJobSymbol', 'myJobName', 'myGroupSymbol', 'myGroupName')
        assert display.groupSymbol == 'myGroupSymbol'
        assert display.groupName == 'myGroupName'
    }

    @Test
    void noGroup() {
        def script = loadScript('vars/submitToTreeherder.groovy')
        def display = script.getDisplay('myJobSymbol', 'myJobName', '?', null)
        assert display.groupSymbol == '?'
        assert !display.containsKey('groupName')
    }

    @Test
    void getLogsReturnsListOfLinkMaps() {
        helper.registerAllowedMethod('publishToS3', [String.class, String.class], {path, bucket ->
            [[url:'linkURL', name:'linkName']]})
        def script = loadScript('vars/submitToTreeherder.groovy')
        def links = script.getLogs('foo')
        assert links.size() == 1
        assert links[0].url == 'linkURL'
        assert links[0].name == 'linkName'
    }

    @Test
    void tbplLogNameIsModified() {
        helper.registerAllowedMethod('publishToS3', [String.class, String.class], {path, bucket ->
            [[url:'linkURL', name:'my-tbpl-log']]})
        def script = loadScript('vars/submitToTreeherder.groovy')
        def links = script.getLogs('foo')
        assert links.size() == 1
        assert links[0].url == 'linkURL'
        assert links[0].name == 'buildbot_text'
    }

}
