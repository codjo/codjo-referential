package net.codjo.referential.releasetest;
import net.codjo.mad.gui.base.GuiPlugin;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.plugin.common.AbstractApplicationPlugin;
import net.codjo.referential.gui.api.ListGuiCustomizer;
import net.codjo.referential.gui.api.Referential;
import net.codjo.referential.gui.api.ReferentialListGui;
import net.codjo.referential.gui.plugin.ReferentialGuiPlugin;
import org.uispec4j.Table;
import org.uispec4j.Tree;
import org.uispec4j.Window;

public class ReferentialCustomizationTest extends ReferentialGuiTestCase {
    @Override
    protected Class<? extends GuiPlugin>[] getGuiPlugins() {
        //noinspection unchecked
        return new Class[]{ReferentialGuiPlugin.class, AddCustomizerPlugin.class};
    }


    private Window getReferentialWindow() {
        mainWindow.getMenuBar().getMenu("Référentiel").getSubMenu("Gestion des référentiels").click();
        return mainWindow.getDesktop().getWindow("Gestion des référentiels");
    }


    public void test_displayReferenceNoFamilyList() throws Exception {
        tokio.insertInputInDb("displayReferenceNoFamilyList");

        Window window = getReferentialWindow();
        final Tree tree = window.getTree();
        tree.select("Devise");
        Table table = window.getTable();
        assertTrue(table.getHeader().contentEquals(new String[]{"Libellé", "Date de clôture",
                                                                "Est valide", "Valeur", "Population",
                                                                "Produit Intérieur Brut", "Code"}));
    }

/* *** POUR VISUALISER L'IHM ***
    public static void main(String[] args) throws Exception {
        Class clazz = ReferentialCustomizationTest.class;
        //from GuiPluginTestCase.setUp
        java.util.Properties properties = new java.util.Properties();
        properties.load(clazz.getResourceAsStream("/conf/application.properties"));
        String login = (String)properties.get("login.default.name");
        String passwd = (String)properties.get("login.default.pwd");
        net.codjo.agent.UserId userId = net.codjo.agent.UserId.createId(login, passwd);

        net.codjo.tokio.TokioFixture tokio = new net.codjo.tokio.TokioFixture(clazz);
        net.codjo.agent.test.AgentContainerFixture container = new net.codjo.agent.test.AgentContainerFixture();
        net.codjo.test.common.fixture.CompositeFixture fixture = new net.codjo.test.common.fixture.CompositeFixture(tokio, container);
        fixture.doSetUp();
        tokio.getJdbcFixture().getConnection().setAutoCommit(true);

        tokio.insertInputInDb("displayReferenceNoFamilyList");

        //from GuiPluginTestCase.initServer
        net.codjo.security.common.api.UserMock mock = new net.codjo.security.common.api.UserMock().mockIsAllowedTo(true);
        net.codjo.plugin.server.ServerCore serverCore = new net.codjo.plugin.server.ServerCore();
        serverCore.addGlobalComponent(userId);
        serverCore.addGlobalComponent(net.codjo.security.common.api.User.class, mock);
        serverCore.addPlugin(net.codjo.sql.server.plugin.JdbcServerPlugin.class);
        serverCore.addPlugin(net.codjo.security.server.plugin.SecurityServerPlugin.class);
        serverCore.addPlugin(net.codjo.mad.server.plugin.MadServerPlugin.class);
        serverCore.addPlugin(net.codjo.referential.releasetest.GuiPluginTestCase.ServerTestPlugin.class);
        String[] arguments = new String[]{"-configuration", clazz.getResource("/server-config.properties").getFile()};
        serverCore.start(new net.codjo.plugin.common.CommandLineArguments(arguments));

        //from GuiPluginTestCase.initClient
        net.codjo.mad.gui.base.MadGuiCore guiCore = new net.codjo.mad.gui.base.MadGuiCore(clazz.getResource("/conf/menu.xml"), null);
        guiCore.getConfiguration().setMainWindowSize(new java.awt.Dimension(1100, 900));
        guiCore.addPlugin(net.codjo.security.client.plugin.SecurityClientPlugin.class);
        guiCore.addPlugin(net.codjo.mad.client.plugin.MadConnectionPlugin.class);
        guiCore.addPlugin(net.codjo.security.gui.plugin.SecurityGuiPlugin.class);
        guiCore.addPlugin(net.codjo.i18n.gui.plugin.InternationalizationGuiPlugin.class);
        guiCore.addPlugin(net.codjo.mad.gui.plugin.MadGuiPlugin.class);
        guiCore.addPlugin(ReferentialGuiPlugin.class);
        guiCore.addPlugin(MyGuiPlugin.class);
        net.codjo.mad.gui.util.ApplicationData applicationData = new net.codjo.mad.gui.util.ApplicationData(clazz.getResourceAsStream("/conf/application.properties"));
        guiCore.show(new String[]{login, passwd, "localhost", "35714"}, applicationData);
    }
/**/

    public static class AddCustomizerPlugin extends AbstractApplicationPlugin {

        public AddCustomizerPlugin(ReferentialGuiPlugin referentialGuiPlugin) {
            referentialGuiPlugin.getConfiguration()
                  .setListGuiCustomizer(MoveFirstColumnToTheEndCustomizer.class);
        }
    }
    public static class MoveFirstColumnToTheEndCustomizer implements ListGuiCustomizer {

        public void handleListOpen(Referential referential, ReferentialListGui listGui) {
            Preference newPreference = new Preference(listGui.getTable().getPreference());

            Column firstColumn = newPreference.getColumns().remove(0);
            newPreference.getColumns().add(firstColumn);

            listGui.getTable().setPreference(newPreference);
        }


        public void handleListClose(ReferentialListGui listGui) {
        }
    }
}
