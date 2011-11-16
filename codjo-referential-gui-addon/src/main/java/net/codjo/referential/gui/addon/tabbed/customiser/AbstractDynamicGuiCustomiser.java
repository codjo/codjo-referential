package net.codjo.referential.gui.addon.tabbed.customiser;
import net.codjo.mad.gui.request.AutoCompleteRequestComboBox;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import net.codjo.referential.gui.api.AbstractGuiCustomizer;
import net.codjo.referential.gui.api.CompositeGuiCustomiser;
import net.codjo.referential.gui.api.DefaultGuiCustomizer;
import net.codjo.referential.gui.api.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JComponent;
import org.apache.log4j.Logger;

public abstract class AbstractDynamicGuiCustomiser extends AbstractGuiCustomizer {
    protected static final Logger LOG = Logger.getLogger(AbstractDynamicGuiCustomiser.class);
    protected Map<String, String> classNameTranslator = new HashMap<String, String>();
    protected final Map<String, ComponentFactory<? extends RequestComboBox>> comboFactoryMap
          = new HashMap<String, ComponentFactory<? extends RequestComboBox>>();
    protected CompositeGuiCustomiser defaultGuiCustomiser = new DefaultGuiCustomizer();
    protected GuiFieldGroup guiFieldGroup;


    protected AbstractDynamicGuiCustomiser(GuiFieldGroup guiFieldGroup) {
        this.guiFieldGroup = guiFieldGroup;
        registerComponent("comboBox",
                          "net.codjo.mad.gui.request.RequestComboBox",
                          new RequestComboBoxFactory());
        registerComponent("autoCompleteComboBox",
                          "net.codjo.mad.gui.request.AutoCompleteRequestComboBox",
                          new AutoCompleteComboBoxFactory());
    }


    public void registerComponent(String componentType,
                                  String componentClassName,
                                  ComponentFactory<? extends RequestComboBox> componentFactory) {
        registerComponentClass(componentType, componentClassName);
        registerComponentFactory(componentClassName, componentFactory);
    }


    public void registerComponentClass(String componentType, String componentClass) {
        classNameTranslator.put(componentType, componentClass);
    }


    public void registerComponentFactory(String factoryKey, ComponentFactory<? extends RequestComboBox> factory) {
        comboFactoryMap.put(factoryKey, factory);
    }


    public void initDefaultFieldValue(Field field) {
        GuiField guiField = guiFieldGroup.getGuiField(field.getName());
        if (guiField == null) {
            return;
        }
        if (guiField.getHandlerId() == null) {
            for (RelatedGuiField relatedGuiField : guiField.getRelatedGuiFields()) {
                defaultGuiCustomiser.initDefaultFieldValue(relatedGuiField);
            }
        }
    }


    protected ComponentFactory<? extends RequestComboBox> getComboBoxFactory(final String key) {
        return comboFactoryMap.get((classNameTranslator.get(key) == null ?
                                    key :
                                    classNameTranslator.get(key)));
    }


    protected static interface ComponentFactory<T extends JComponent> {
        public T create(GuiField guiField, Field field) throws Exception;
    }

    public static class RequestComboBoxFactory implements ComponentFactory<RequestComboBox> {

        public RequestComboBox create(GuiField guiField, Field field) {
            RequestComboBox comboBox = new RequestComboBox();
            comboBox.setEditable(guiField.isEditable());
            initComboBox(guiField, comboBox);
            return comboBox;
        }


        protected String[] builColumns(GuiField guiField) {
            Set<String> columns = new TreeSet<String>();
            columns.add("refCode");
            columns.add("refLabel");
            if (guiField.hasRelatedGuiFields()) {
                for (RelatedGuiField relatedGuiField : guiField.getRelatedGuiFields()) {
                    columns.add(relatedGuiField.getName());
                }
            }
            return columns.toArray(new String[columns.size()]);
        }


        protected void initComboBox(GuiField guiField, RequestComboBox requestComboBox) {
            requestComboBox.getDataSource().setLoadFactoryId(guiField.getHandlerId());
            requestComboBox.initRequestComboBox("refCode", "refLabel", false);
            requestComboBox.setColumns(builColumns(guiField));
            requestComboBox.setEnabled(true);
            requestComboBox.setEditable(guiField.isEditable());
        }
    }

    public static class AutoCompleteComboBoxFactory extends RequestComboBoxFactory {

        @Override
        public RequestComboBox create(GuiField guiField, Field field) {
            AutoCompleteRequestComboBox requestComboBox
                  = new AutoCompleteRequestComboBox(guiField.isEditable());
            initComboBox(guiField, requestComboBox);
            return requestComboBox;
        }
    }
}
