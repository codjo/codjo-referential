package net.codjo.referential.gui;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.action.AddAction;
import net.codjo.mad.gui.request.action.DetailWindowBuilder;
/**
 *
 */
public class AddReferentialAction extends AddAction {
    AddReferentialAction(GuiContext ctxt, RequestTable table, DetailWindowBuilder builder) {
        super(ctxt, table, builder);
    }
}
