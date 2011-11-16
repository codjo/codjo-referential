package net.codjo.referential.gui;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.action.DetailWindowBuilder;
import net.codjo.mad.gui.request.action.EditAction;
/**
 *
 */
public class EditReferentialAction extends EditAction {
    EditReferentialAction(GuiContext ctxt, RequestTable table, DetailWindowBuilder builder) {
        super(ctxt, table, builder);
    }
}
