package net.codjo.referential.gui.addon.tabbed.customiser;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import net.codjo.referential.gui.api.DefaultGuiCustomizer;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

public class RelatedFieldsGuiCustomiser extends AbstractDynamicGuiCustomiser {
    protected final Referential referential;
    protected final GuiReferential guiReferential;
    private MasterFieldGuiCustomizer masterFieldGuiCustomizer;


    public RelatedFieldsGuiCustomiser(GuiFieldGroup guiFieldGroup,
                                      Referential referential,
                                      GuiReferential guiReferential) {
        super(guiFieldGroup);
        this.referential = referential;
        this.guiReferential = guiReferential;
        this.defaultGuiCustomiser = new DefaultGuiCustomizer();
        masterFieldGuiCustomizer = new MasterFieldGuiCustomizer(
              this.guiReferential,
              comboFactoryMap,
              classNameTranslator
        );
        this.defaultGuiCustomiser.addFirstCustomizer(masterFieldGuiCustomizer);
    }


    public boolean handle(final Field field) {
        boolean handle = false;
        for (GuiField guiField : guiReferential.findGuiFields(field.getName())) {
            if (guiField.hasRelatedGuiFields()) {
                handle = true;
            }
        }
        return handle;
    }


    public JComponent createGui(Field field) {
        GuiField guiField = guiFieldGroup.getGuiField(field.getName());
        Box container = Box.createVerticalBox();
        JComponent fieldComponent = field.getComponent();
        if (fieldComponent == null) {
            fieldComponent = defaultGuiCustomiser.createGui(field);
        }
        fieldComponent.setName(field.getName());
        if (guiField.isRootDisplayed()) {
            fieldComponent.setAlignmentX(0);
            container.add(fieldComponent);
        }
        if (guiField.hasRelatedGuiFields()) {
            addRelatedGuiFields(field, guiField, container);
        }
        return container;
    }


    protected JComponent buildRelatedFieldComponent(RelatedGuiField relatedGuiField) {
        JComponent gui;
        if (relatedGuiField.getType() == null) {
            TextField textField = new TextField();
            textField.setMaxTextLength(relatedGuiField.getLength());
            gui = textField;
        }
        else {
            gui = defaultGuiCustomiser.createGui(relatedGuiField);
        }
        if (gui instanceof JTextComponent) {
            GuiUtil.disableTextEdition((JTextComponent)gui);
        }
        gui.setAlignmentX(0);
        relatedGuiField.setComponent(gui);
        return gui;
    }


    protected void addRelatedGuiFields(Field field, GuiField guiField, Box container) {
        for (int i = 0; i < guiField.getRelatedGuiFields().size(); i++) {
            RelatedGuiField relatedGuiField = guiField.getRelatedGuiFields().get(i);
            relatedGuiField.initValuesFromReferential(referential);
            if (i != 0 || guiField.isRootDisplayed()) {
                container.add(Box.createRigidArea(new Dimension(1, 8)));
            }

            JLabel label = new JLabel(relatedGuiField.getLabel());
            label.setAlignmentX(0);
            label.setForeground(Color.GRAY);
            container.add(label);
            container.add(buildRelatedFieldComponent(relatedGuiField));

            if (guiField.getHandlerId() != null) {
                RelatedFieldValueUpdater.activate(field, relatedGuiField);
            }
        }
    }


    @Override
    public void declareField(DetailDataSource dataSource, Field field) {
        super.declareField(dataSource, field);
        GuiField guiField = guiFieldGroup.getGuiField(field.getName());
        List<RelatedGuiField> relatedFields = guiField.getRelatedGuiFields();
        if (relatedFields != null) {
            for (RelatedGuiField relatedGuiField : relatedFields) {
                if (guiField.getHandlerId() == null) {
                    super.declareField(dataSource, relatedGuiField);
                }
            }
        }
    }
}
