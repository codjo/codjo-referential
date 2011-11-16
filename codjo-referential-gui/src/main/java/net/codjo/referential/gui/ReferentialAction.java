package net.codjo.referential.gui;

import net.codjo.mad.gui.framework.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.GuiLogic;
import net.codjo.referential.gui.api.ListGuiCustomizer;
import net.codjo.referential.gui.api.ReferentialFrameCustomizer;
import net.codjo.referential.gui.api.TreeGuiCustomizer;
import java.awt.Dimension;
import javax.swing.JInternalFrame;

/**
 * Affichage d'une liste avec détail pour une table de référence.
 */
public class ReferentialAction extends AbstractAction {
    private ReferentialMapping referentialMapping;
    private Class<? extends ListGuiCustomizer> listGuiCustomizer;
    private Class<? extends ReferentialFrameCustomizer> referentialFrameCustomizer;
    private Class<? extends TreeGuiCustomizer> treeGuiCustomizer;
    private boolean waitingPanel = false;
    private Dimension preferredSize;


    public ReferentialAction(GuiContext guiContext, ActionData data) {
        super(guiContext, "Gestion des référentiels", "Gestion des référentiels");
        this.referentialMapping = data.getReferentialMapping();
        this.listGuiCustomizer = data.getListGuiCustomizer();
        this.treeGuiCustomizer = data.getTreeGuiCustomizer();
        this.referentialFrameCustomizer = data.getReferentialFrameCustomizer();
        this.waitingPanel = data.isWaitingPanel();
        this.preferredSize = data.getPreferredSize();
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext guiContext) throws Exception {
        return getReferentialLogic(guiContext).getGui();
    }


    protected GuiLogic<ReferentialGui> getReferentialLogic(GuiContext guiContext) throws Exception {
        return new ReferentialLogic(guiContext,
                                    referentialMapping,
                                    listGuiCustomizer.newInstance(),
                                    treeGuiCustomizer.newInstance(),
                                    referentialFrameCustomizer.newInstance(),
                                    waitingPanel,
                                    preferredSize);
    }
}
