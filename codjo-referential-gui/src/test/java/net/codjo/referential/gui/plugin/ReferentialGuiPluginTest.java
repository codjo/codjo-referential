package net.codjo.referential.gui.plugin;
import net.codjo.mad.gui.base.GuiConfigurationMock;
import net.codjo.mad.gui.base.MadGuiCore;
import net.codjo.referential.gui.ActionData;
import net.codjo.referential.gui.ReferentialMapping;
import net.codjo.referential.gui.api.Referential;
import net.codjo.test.common.LogString;
import java.util.SortedMap;
import junit.framework.TestCase;
/**
 *
 */
public class ReferentialGuiPluginTest extends TestCase {
    private LogString log = new LogString();
    private MadGuiCore core;
    private ReferentialGuiPlugin guiPlugin;


    public void test_mappingFileNotFound() throws Exception {

        try {
            guiPlugin.initGui(new GuiConfigurationMock());
            fail();
        }
        catch (IllegalStateException ex) {
            assertEquals("/conf/referential-mapping.xml cannot be loaded. "
                         + "Do you have a dependency on the datagen module (classifier 'client') ?",
                         ex.getMessage());
        }
    }


    public void test_customizeReferentialMappin() throws Exception {
        ReferentialMappingMock mapping = new ReferentialMappingMock();
        guiPlugin.getConfiguration().setReferentialMapping(mapping);

        guiPlugin.initGui(new GuiConfigurationMock());

        assertSame(mapping, core.getGlobalComponent(ActionData.class).getReferentialMapping());
    }


    public void test_registerAction() throws Exception {
        guiPlugin.getConfiguration().setReferentialMapping(new ReferentialMappingMock());

        guiPlugin.initGui(new GuiConfigurationMock(log));

        log.assertContent("registerAction(ReferentialGuiPlugin, referential, ReferentialAction), "
                          + "registerAction(ReferentialGuiPlugin, referentialFamily, FamilyAction)");
    }


    @Override
    protected void setUp() throws Exception {
        core = new MadGuiCore();
        guiPlugin = new ReferentialGuiPlugin(core);
    }


    private static class ReferentialMappingMock implements ReferentialMapping {
        public SortedMap<String, Referential> getReferentialsByPreferenceId() throws Exception {
            return null;
        }
    }
}
