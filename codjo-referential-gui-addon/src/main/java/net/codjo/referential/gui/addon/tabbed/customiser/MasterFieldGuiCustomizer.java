package net.codjo.referential.gui.addon.tabbed.customiser;
import net.codjo.mad.gui.request.AutoCompleteRequestComboBox;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.addon.tabbed.customiser.AbstractDynamicGuiCustomiser.ComponentFactory;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import net.codjo.referential.gui.api.AbstractGuiCustomizer;
import net.codjo.referential.gui.api.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JComponent;
import org.apache.log4j.Logger;

class MasterFieldGuiCustomizer extends AbstractGuiCustomizer {
    private static final Logger LOG = Logger.getLogger(MasterFieldGuiCustomizer.class);
    private static final String REF_CODE = "refCode";
    private static final String REF_LABEL = "refLabel";
    private final GuiReferential guiReferential;
    private final Map<String, ComponentFactory<? extends RequestComboBox>> factoriesMap;
    private final Map<String, String> classNameTranslator;


    MasterFieldGuiCustomizer(final GuiReferential guiReferential,
                                 final Map<String, ComponentFactory<? extends RequestComboBox>> factoriesMap,
                                 final Map<String, String> classNameTranslator) {
        this.guiReferential = guiReferential;
        this.factoriesMap = factoriesMap;
        this.classNameTranslator = classNameTranslator;
    }


    public boolean handle(final Field field) {
        final List<GuiField> guiFields = guiReferential.findGuiFields(field.getName());
        boolean handle = false;
        for (GuiField guiField : guiFields) {
            handle = handle || (guiField != null && guiField.getHandlerId() != null);
        }
        return handle;
    }


    public void initDefaultFieldValue(Field field) {
        // Todo
    }


    public JComponent createGui(final Field field) {
        final GuiField guiField = findGuiField(field);
        try {
            RequestComboBox comboBox;
            ComponentFactory<? extends RequestComboBox> factory
                  = getComboBoxFactory(guiField.getComponentClassName());
            if (factory == null) {
                comboBox = new AutoCompleteRequestComboBox();
                comboBox.initRequestComboBox(REF_CODE, REF_LABEL, !field.isRequired());
                comboBox.getDataSource().setColumns(createColumns(guiField));
                comboBox.getDataSource().setLoadFactoryId(guiField.getHandlerId());
            }
            else {
                comboBox = factory.create(guiField, field);
            }
            comboBox.setName(field.getName());
            field.setComponent(comboBox);
            return comboBox;
        }
        catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new RuntimeException(exception);
        }
    }


    private GuiField findGuiField(Field field) {
        if (field == null) {
            return null;
        }
        for (GuiField guiField : guiReferential.findGuiFields(field.getName())) {
            if (guiField.getHandlerId() != null) {
                return guiField;
            }
        }
        return null;
    }


    private ComponentFactory<? extends RequestComboBox> getComboBoxFactory(final String key) {
        if (factoriesMap == null) {
            return null;
        }
        final String newKey;
        if (classNameTranslator == null || classNameTranslator.get(key) == null) {
            newKey = key;
        }
        else {
            newKey = classNameTranslator.get(key);
        }
        return factoriesMap.get(newKey);
    }


    String[] createColumns(GuiField guiField) {
        final Set<String> columns = new TreeSet<String>();
        columns.add(REF_CODE);
        columns.add(REF_LABEL);
        if (guiField.hasRelatedGuiFields()) {
            for (RelatedGuiField relatedGuiField : guiField.getRelatedGuiFields()) {
                columns.add(relatedGuiField.getName());
            }
        }
        return columns.toArray(new String[columns.size()]);
    }
}
