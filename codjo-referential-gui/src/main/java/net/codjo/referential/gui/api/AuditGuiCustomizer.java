package net.codjo.referential.gui.api;
import net.codjo.mad.gui.framework.AuditPanel;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;
import javax.swing.JComponent;

public class AuditGuiCustomizer implements GuiCustomizer {

    public boolean handle(Field field) {
        return field.isAuditField();
    }


    public JComponent createGui(Field field) {
        AuditPanel auditPanel = new AuditPanel();
        field.setComponent(auditPanel);
        return auditPanel;
    }


    public void declareField(DetailDataSource dataSource, Field field) {
        AuditPanel auditPanel = (AuditPanel)field.getComponent();
        auditPanel.declareFields(dataSource);
    }


    public void initDefaultFieldValue(Field field) {
        // Todo
    }
}
