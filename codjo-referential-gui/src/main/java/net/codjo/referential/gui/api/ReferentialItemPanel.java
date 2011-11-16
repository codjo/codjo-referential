package net.codjo.referential.gui.api;
import net.codjo.gui.toolkit.LabelledItemPanel;
import net.codjo.gui.toolkit.util.FeedbackPanel;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.gui.framework.AuditPanel;
import net.codjo.mad.gui.request.DetailDataSource;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public class ReferentialItemPanel extends LabelledItemPanel {
    private static final GuiCustomizer GUICUSTOMIZER = new DefaultGuiCustomizer();
    protected AuditPanel auditPanel;
    protected FeedbackPanel feedbackPanel;


    public ReferentialItemPanel() {
        feedbackPanel = new FeedbackPanel(this);
    }


    public void switchToUpdateMode(Collection<Field> fields) {
        for (Field field : fields) {
            if (field.isPrimaryKey()) {
                if (field.getComponent() instanceof JTextComponent) {
                    GuiUtil.disableTextEdition((JTextComponent)field.getComponent());
                }
                else if (!field.isGenerated()) {
                    field.getComponent().setEnabled(false);
                }
            }
        }
    }


    private void addItem(Field field, JComponent fieldComponent) {
        if (fieldComponent == null) {
            throw new IllegalStateException("Le champs " + field.getName() + " n'a pas de composant associé");
        }
        addItem(field.getLabel(), fieldComponent);
    }


    public void declareFields(DetailDataSource dataSource, Collection<Field> fields) {
        declareFields(dataSource, fields, GUICUSTOMIZER);
    }


    public void declareFields(DetailDataSource dataSource,
                              Collection<Field> fields,
                              GuiCustomizer customizer) {
        for (Field field : fields) {
            if (customizer != null && customizer.handle(field)) {
                customizer.declareField(dataSource, field);
            }
            else if (!field.isGenerated()) {
                dataSource.declare(field.getName(), field.getComponent());
            }
        }
    }


    public void buildPanel(Collection<Field> fields) {
        buildPanel(fields, GUICUSTOMIZER);
    }


    public void buildPanel(Collection<Field> fields, GuiCustomizer customizer) {
        for (Field field : fields) {
            if (customizer != null && customizer.handle(field)) {
                buildItemFromCustomiser(customizer, field);
            }
            else {
                GuiCustomizer defaultGuiCustomizer = new DefaultGuiCustomizer();
                if (!field.isGenerated()) {
                    addItem(field, defaultGuiCustomizer.createGui(field));
                }
                else {
                    continue;
                }
            }

            if (field.isRequired()) {
                feedbackPanel.addIcon(field.getComponent());
            }
            if (field.getComponent() == null) {
                throw new IllegalStateException("Le composant GUI associé au champ '"
                                                + field.getName() + "' n'a pas été renseigné. "
                                                + "Il faut faire field.setComponent(gui) dans le customizer");
            }
        }

        if (auditPanel != null) {
            addItem(auditPanel);
        }
    }


    protected void buildItemFromCustomiser(GuiCustomizer customizer, Field field) {
        JComponent component = customizer.createGui(field);
        if (component instanceof AuditPanel) {
            auditPanel = (AuditPanel)component;
        }
        else {
            addItem(field, component);
        }
    }


    public static interface GuiCustomizer {
        boolean handle(Field field);


        JComponent createGui(Field field);


        void declareField(DetailDataSource dataSource, Field field);


        void initDefaultFieldValue(Field field);
    }

    public static abstract class GuiCustomizerAdapter implements GuiCustomizer {
        public boolean handle(Field field) {
            return false;
        }


        public JComponent createGui(Field field) {
            return null;
        }


        public void declareField(DetailDataSource dataSource, Field field) {
        }


        public void initDefaultFieldValue(Field field) {
        }
    }
}
