package net.codjo.referential.gui.addon.tabbed;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import java.util.ArrayList;
import java.util.List;

public class TabbedPaneConfiguration {
    private final List<GuiReferential> referentials = new ArrayList<GuiReferential>();


    public List<GuiReferential> getReferentials() {
        return referentials;
    }


    public GuiReferential getReferential(String name) {
        for (GuiReferential referential : referentials) {
            if (referential.getName().equalsIgnoreCase(name)) {
                return referential;
            }
        }
        return null;
    }
}
