package server.plugin;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.mad.server.plugin.MadServerPluginMock;
import net.codjo.test.common.LogString;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class ReferentialServerPluginTest {
    private LogString log = new LogString();
    private AgentContainerFixture fixture = new AgentContainerFixture();


    @Before
    public void setUp() throws Exception {
        fixture.doSetUp();
    }


    @Test
    public void test_handlerIsDeclared() throws Exception {
        new ReferentialServerPlugin(new MadServerPluginMock(new LogString("madServerPlugin", log)));
        log.assertAndClear("madServerPluginConfiguration.addHandlerCommand(DeletePmReferentialFamilyCommand)");
    }


    @After
    public void tearDown() throws Exception {
        fixture.doTearDown();
    }
}
