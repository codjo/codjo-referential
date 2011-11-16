package net.codjo.referential.gui.plugin;
import net.codjo.referential.gui.ReferentialMapping;
import net.codjo.referential.gui.XmlReferentialMapping;
import net.codjo.referential.gui.api.EmptyListGuiCustomizer;
import net.codjo.referential.gui.api.EmptyReferentialFrameCustomizer;
import net.codjo.referential.gui.api.EmptyTreeGuiCustomizer;
import net.codjo.referential.gui.api.ListGuiCustomizer;
import net.codjo.referential.gui.api.ReferentialFrameCustomizer;
import net.codjo.referential.gui.api.TreeGuiCustomizer;
import java.awt.Dimension;
/**
 *
 */
public class ReferentialGuiConfiguration {
    private Class<? extends ListGuiCustomizer> listGuiCustomizer = EmptyListGuiCustomizer.class;
    private Class<? extends TreeGuiCustomizer> treeGuiCustomizer = EmptyTreeGuiCustomizer.class;
    private Class<? extends ReferentialFrameCustomizer> referentialFrameCustomizer
          = EmptyReferentialFrameCustomizer.class;
    
    private boolean waitingPanel = false;
    private ReferentialMapping referentialMapping;
    private Dimension preferredSize;


    public Class<? extends ListGuiCustomizer> getListGuiCustomizer() {
        return listGuiCustomizer;
    }


    public void setListGuiCustomizer(Class<? extends ListGuiCustomizer> listGuiCustomizer) {
        this.listGuiCustomizer = listGuiCustomizer;
    }


    public Class<? extends TreeGuiCustomizer> getTreeGuiCustomizer() {
        return treeGuiCustomizer;
    }


    public void setTreeGuiCustomizer(Class<? extends TreeGuiCustomizer> treeGuiCustomizer) {
        this.treeGuiCustomizer = treeGuiCustomizer;
    }


    public void setReferentialFrameCustomizer(Class<? extends ReferentialFrameCustomizer> referentialFrameCustomizer) {
        this.referentialFrameCustomizer = referentialFrameCustomizer;
    }


    public Class<? extends ReferentialFrameCustomizer> getReferentialFrameCustomizer() {
        return referentialFrameCustomizer;
    }


    public boolean isWaitingPanel() {
        return waitingPanel;
    }


    public void setWaitingPanel(boolean waitingPanel) {
        this.waitingPanel = waitingPanel;
    }


    public ReferentialMapping getReferentialMapping() {
        if (referentialMapping == null) {
            try {
                referentialMapping = XmlReferentialMapping.loadDefault();
            }
            catch (Exception cause) {
                throw new IllegalStateException(ReferentialGuiPlugin.MAPPING_FILE_PATH + " cannot be loaded. "
                                                + "Do you have a dependency on the datagen module (classifier 'client') ?",
                                                cause);
            }
        }
        return referentialMapping;
    }


    public void setReferentialMapping(ReferentialMapping referentialMapping) {
        this.referentialMapping = referentialMapping;
    }


    public void setPreferredSize(int width, int height) {
        this.preferredSize = new Dimension(width, height);
    }


    public Dimension getPreferredSize() {
        return preferredSize;
    }
}
