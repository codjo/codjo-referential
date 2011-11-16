package net.codjo.referential.gui.plugin;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.i18n.AbstractInternationalizableGuiPlugin;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.referential.gui.ActionData;
import net.codjo.referential.gui.ReferentialAction;
import net.codjo.referential.gui.admin.FamilyAction;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

public final class ReferentialGuiPlugin extends AbstractInternationalizableGuiPlugin {
    public static final String MAPPING_FILE_PATH = "/conf/referential-mapping.xml";
    private static final String PREFERENCES_FILE_PATH = "/conf/referential-preference.xml";
    private static final String PLUGIN_PREFERENCES_FILE_PATH
          = "/net/codjo/referential/gui/plugin/preferences.xml";
    private ReferentialGuiConfiguration configuration = new ReferentialGuiConfiguration();
    private Logger logger = Logger.getLogger(ReferentialGuiPlugin.class.getName());
    private final ApplicationCore core;


    public ReferentialGuiPlugin(ApplicationCore core) {
        this.core = core;
    }


    public ReferentialGuiConfiguration getConfiguration() {
        return configuration;
    }


    @Override
    protected void registerLanguageBundles(TranslationManager translationManager) {
        translationManager.addBundle("net.codjo.referential.gui.i18n", Language.FR);
        translationManager.addBundle("net.codjo.referential.gui.i18n", Language.EN);
    }


    @Override
    public void initGui(GuiConfiguration guiConfiguration) throws Exception {
        super.initGui(guiConfiguration);
        core.addGlobalComponent(new ActionData(configuration.getReferentialMapping(),
                                               configuration.getListGuiCustomizer(),
                                               configuration.getTreeGuiCustomizer(),
                                               configuration.getReferentialFrameCustomizer(),
                                               configuration.isWaitingPanel(),
                                               configuration.getPreferredSize()));

        guiConfiguration.registerAction(this, "referential", ReferentialAction.class);
        guiConfiguration.registerAction(this, "referentialFamily", FamilyAction.class);

        PreferenceFactory.initFactory();
        loadPreferenceFile(PREFERENCES_FILE_PATH);
        loadPreferenceFile(PLUGIN_PREFERENCES_FILE_PATH);
    }


    private void loadPreferenceFile(String preferenceFile) throws IOException {
        InputStream inputStream = toResource(preferenceFile);
        if (inputStream == null) {
            logger.warn("Impossible d'accéder au fichier de préférences '" + preferenceFile + "'.");
        }
        else {
            PreferenceFactory.addMapping(new InputSource(inputStream));
            inputStream.close();
        }
    }


    private InputStream toResource(String resourcePath) {
        return ReferentialGuiPlugin.class.getResourceAsStream(resourcePath);
    }
}
