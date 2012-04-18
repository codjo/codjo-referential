package net.codjo.referential.releasetest;
import java.io.File;
import net.codjo.mad.gui.base.GuiPlugin;
import net.codjo.plugin.server.ServerPlugin;
import net.codjo.referential.gui.plugin.ReferentialGuiPlugin;
import org.uispec4j.Window;
import server.plugin.ReferentialServerPlugin;
/**
 *
 */
public abstract class ReferentialGuiTestCase extends GuiPluginTestCase {
    @Override
    protected Class<? extends GuiPlugin>[] getGuiPlugins() {
        //noinspection unchecked
        return new Class[]{ReferentialGuiPlugin.class};
    }


    @Override
    protected Class<? extends ServerPlugin>[] getServerPlugins() {
        //noinspection unchecked
        return new Class[]{ReferentialServerPlugin.class};
    }


    protected Window getWindow(String title) {
        return mainWindow.getDesktop().getWindow(title);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        jdbc.advanced()
              .executeCreateTableScriptFile(new File(getClass().getResource("/sql/grant.sql").getFile()));
    }
}
