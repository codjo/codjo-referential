package net.codjo.referential.gui;
import net.codjo.referential.gui.api.EmptyReferentialFrameCustomizer;
import net.codjo.referential.gui.api.ListGuiCustomizer;
import net.codjo.referential.gui.api.ReferentialFrameCustomizer;
import net.codjo.referential.gui.api.TreeGuiCustomizer;
import java.awt.Dimension;
/**
 *
 */
public class ActionData {
    private ReferentialMapping referentialMapping;
    private Class<? extends ListGuiCustomizer> listGuiCustomizer;
    private Class<? extends TreeGuiCustomizer> treeGuiCustomizer;
    private Class<? extends ReferentialFrameCustomizer> referentialFrameCustomizer;
    private final boolean waitingPanel;
    private Dimension preferredSize;


    public ActionData(ReferentialMapping referentialMapping,
                      Class<? extends ListGuiCustomizer> listGuiCustomizer,
                      Class<? extends TreeGuiCustomizer> treeGuiCustomizer,
                      Class<? extends ReferentialFrameCustomizer> referentialFrameCustomizer,
                      boolean hasWaitingPanel) {
        this.referentialMapping = referentialMapping;
        this.listGuiCustomizer = listGuiCustomizer;
        this.treeGuiCustomizer = treeGuiCustomizer;
        this.referentialFrameCustomizer = referentialFrameCustomizer;
        this.waitingPanel = hasWaitingPanel;
    }


    public ActionData(ReferentialMapping referentialMapping,
                      Class<? extends ListGuiCustomizer> listGuiCustomizer,
                      Class<? extends TreeGuiCustomizer> treeGuiCustomizer,
                      boolean hasWaitingPanel) {
        this(referentialMapping,
             listGuiCustomizer,
             treeGuiCustomizer,
             EmptyReferentialFrameCustomizer.class,
             hasWaitingPanel);
    }

    public ActionData(ReferentialMapping referentialMapping,
                      Class<? extends ListGuiCustomizer> listGuiCustomizer,
                      Class<? extends TreeGuiCustomizer> treeGuiCustomizer,
                      Class<? extends ReferentialFrameCustomizer> referentialFrameCustomizer,
                      boolean hasWaitingPanel,
                      Dimension preferredSize) {
        this.referentialMapping = referentialMapping;
        this.listGuiCustomizer = listGuiCustomizer;
        this.treeGuiCustomizer = treeGuiCustomizer;
        this.referentialFrameCustomizer = referentialFrameCustomizer;
        this.waitingPanel = hasWaitingPanel;
        this.preferredSize = preferredSize;
    }


    public ActionData(ReferentialMapping referentialMapping,
                      Class<? extends ListGuiCustomizer> listGuiCustomizer,
                      Class<? extends TreeGuiCustomizer> treeGuiCustomizer,
                      boolean hasWaitingPanel,
                      Dimension preferredSize) {
        this(referentialMapping,
             listGuiCustomizer,
             treeGuiCustomizer,
             EmptyReferentialFrameCustomizer.class,
             hasWaitingPanel,
             preferredSize);
    }


    public ReferentialMapping getReferentialMapping() {
        return referentialMapping;
    }


    public Class<? extends ListGuiCustomizer> getListGuiCustomizer() {
        return listGuiCustomizer;
    }


    public Class<? extends TreeGuiCustomizer> getTreeGuiCustomizer() {
        return treeGuiCustomizer;
    }


    public Class<? extends ReferentialFrameCustomizer> getReferentialFrameCustomizer() {
        return referentialFrameCustomizer;
    }


    public boolean isWaitingPanel() {
        return waitingPanel;
    }


    public Dimension getPreferredSize() {
        return preferredSize;
    }
}
