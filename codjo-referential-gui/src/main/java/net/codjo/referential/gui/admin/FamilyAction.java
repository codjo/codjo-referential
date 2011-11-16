package net.codjo.referential.gui.admin;
import net.codjo.mad.gui.framework.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.referential.gui.ActionData;
import net.codjo.referential.gui.ReferentialMapping;
import javax.swing.JInternalFrame;

public class FamilyAction extends AbstractAction {
    private final ReferentialMapping referentialMapping;


    public FamilyAction(GuiContext guiContext, ActionData data) {
        super(guiContext, "Gestion des familles de référentiels", "Gestion des familles de référentiels");
        this.referentialMapping = data.getReferentialMapping();
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext guiContext) throws Exception {
        return new ReferentialAssoLogic(guiContext, referentialMapping).getGui();
    }
}
