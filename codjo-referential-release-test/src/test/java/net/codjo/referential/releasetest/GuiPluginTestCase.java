package net.codjo.referential.releasetest;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import net.codjo.agent.UserId;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.plugin.InternationalizationGuiPlugin;
import net.codjo.mad.client.plugin.MadConnectionPlugin;
import net.codjo.mad.gui.base.AbstractGuiPlugin;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.base.GuiPlugin;
import net.codjo.mad.gui.base.MadGuiCore;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.plugin.MadGuiPlugin;
import net.codjo.mad.gui.util.ApplicationData;
import net.codjo.mad.server.plugin.MadServerPlugin;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.plugin.common.ApplicationPlugin;
import net.codjo.plugin.common.CommandLineArguments;
import net.codjo.plugin.server.AbstractServerPlugin;
import net.codjo.plugin.server.ServerCore;
import net.codjo.plugin.server.ServerPlugin;
import net.codjo.security.client.plugin.SecurityClientPlugin;
import net.codjo.security.common.api.User;
import net.codjo.security.common.api.UserMock;
import net.codjo.security.gui.plugin.SecurityGuiPlugin;
import net.codjo.security.server.plugin.SecurityServerPlugin;
import net.codjo.sql.server.plugin.JdbcServerPlugin;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;

public abstract class GuiPluginTestCase extends UISpecTestCase {
    protected TokioFixture tokio = new TokioFixture(getClass());
    protected JdbcFixture jdbc;
    protected AgentContainerFixture container = new AgentContainerFixture();
    protected final UserMock userMock = new UserMock();
    protected UserId userId;
    private CompositeFixture fixture = new CompositeFixture(tokio, container);
    private ServerCore serverCore;
    private MadGuiCore guiCore;
    protected Window mainWindow;
    private String login;
    private String passwd;


    @Override
    protected void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/conf/application.properties"));
        login = (String)properties.get("login.default.name");
        passwd = (String)properties.get("login.default.pwd");
        userId = UserId.createId(login, passwd);
        super.setUp();
        fixture.doSetUp();
        jdbc = tokio.getJdbcFixture();
        jdbc.getConnection().setAutoCommit(true);
        initServer();
        initClient();
    }


    @Override
    protected void tearDown() throws Exception {
        // TODO : BG mise en place des thread sleep de manière temporaire...
        Thread.sleep(1000);
        guiCore.stop();
        Thread.sleep(1000);
        serverCore.stop();
        Thread.sleep(1000);
        fixture.doTearDown();
        super.tearDown();
    }


    public void initServer() throws Exception {
        serverCore = new ServerCore();
        serverCore.addGlobalComponent(userId);
        serverCore.addGlobalComponent(User.class, userMock.mockIsAllowedTo(true));
        serverCore.addPlugin(JdbcServerPlugin.class);
        serverCore.addPlugin(SecurityServerPlugin.class);
        serverCore.addPlugin(MadServerPlugin.class);
        serverCore.addPlugin(ServerTestPlugin.class);
        addPlugins(serverCore, getServerPlugins());
        String configFile =
              new File(PathUtil.findTargetDirectory(getClass()), "/config/server-config.properties").getCanonicalPath();
        String[] arguments = new String[]{"-configuration", configFile};
        serverCore.start(new CommandLineArguments(arguments));
    }


    private void initClient() throws IOException {
        guiCore = new MadGuiCore(getClass().getResource("/conf/menu.xml"), null);
        guiCore.getConfiguration().setMainWindowSize(new Dimension(1100, 900));
        guiCore.addPlugin(SecurityClientPlugin.class);
        guiCore.addPlugin(MadConnectionPlugin.class);
        guiCore.addPlugin(SecurityGuiPlugin.class);
        guiCore.addPlugin(InternationalizationGuiPlugin.class);
        guiCore.addPlugin(MadGuiPlugin.class);
        guiCore.addPlugin(MyGuiPlugin.class);
        addPlugins(guiCore, getGuiPlugins());

        final ApplicationData applicationData = new ApplicationData(
              getClass().getResourceAsStream("/conf/application.properties"));
        mainWindow = WindowInterceptor.run(new Trigger() {
            public void run() throws Exception {
                guiCore.show(new String[]{login, passwd, "localhost", "35714"}, applicationData);
            }
        });
    }


    public MadGuiCore getGuiCore() {
        return guiCore;
    }


    private void addPlugins(ApplicationCore manager, Class<? extends ApplicationPlugin>[] plugins) {
        if (plugins != null) {
            for (Class<? extends ApplicationPlugin> pluginClass : plugins) {
                manager.addPlugin(pluginClass);
            }
        }
    }


    protected abstract Class<? extends GuiPlugin>[] getGuiPlugins();


    protected abstract Class<? extends ServerPlugin>[] getServerPlugins();


    protected <T extends ApplicationPlugin> T getPlugin(Class<T> aClass) {
        return serverCore.getGlobalComponent(aClass);
    }


    public static class ServerTestPlugin extends AbstractServerPlugin {

        public ServerTestPlugin(SecurityServerPlugin security, User user) {
            security.getConfiguration().setUserFactory(new UserFactoryMock(user));
        }
    }


    protected URL toUrl(String name) {
        return getClass().getResource(name);
    }


    public static class MyGuiPlugin extends AbstractGuiPlugin {

        public void initGui(GuiConfiguration guiConfiguration) throws Exception {
            TranslationManager translationManager = InternationalizationUtil.retrieveTranslationManager(
                  guiConfiguration.getGuiContext());
            translationManager.addBundle("net.codjo.referential.gui.i18n", Language.FR);
            translationManager.addBundle("net.codjo.referential.gui.i18n", Language.EN);
        }
    }
}
