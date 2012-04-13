package server.plugin;
import net.codjo.mad.server.plugin.MadServerPlugin;
import net.codjo.plugin.server.AbstractServerPlugin;
import server.handler.DeletePmReferentialFamilyCommand;
/**
 *
 */
public class ReferentialServerPlugin extends AbstractServerPlugin {

    public ReferentialServerPlugin(MadServerPlugin madServerPlugin) {
        madServerPlugin.getConfiguration().addHandlerCommand(DeletePmReferentialFamilyCommand.class);
    }
}
