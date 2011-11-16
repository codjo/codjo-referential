package net.codjo.referential.gui.addon.tabbed.customiser;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.api.Field;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;

public class DynamicComponentGuiCustomiser extends AbstractDynamicGuiCustomiser {

    public DynamicComponentGuiCustomiser(GuiFieldGroup guiFieldGroup) {
        super(guiFieldGroup);
    }


    public boolean handle(Field field) {
        GuiField guiField = guiFieldGroup.getGuiField(field.getName());
        if (guiField != null && guiField.getHandlerId() == null) {
            String key = guiField.getComponentClassName();
            String className = (classNameTranslator.get(key) == null ? key : classNameTranslator.get(key));
            if (className != null && !("".equals(guiField.getComponentClassName()))) {
                try {
                    //noinspection unchecked
                    return (((Class)Class.forName(className)).asSubclass(JComponent.class) != null);
                }
                catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }


    public JComponent createGui(Field field) {
        GuiField guiField = guiFieldGroup.getGuiField(field.getName());
        String key = guiField.getComponentClassName();
        ComponentFactory componentFactory = comboFactoryMap.get((classNameTranslator.get(key) == null ?
                                                              key :
                                                              classNameTranslator.get(key)));

        JComponent component1 = null;
        try {
            if (componentFactory != null) {
                component1 = componentFactory.create(guiField, field);
            }
            else {
                component1 = new DynamicClassComponentFactory(classNameTranslator).create(guiField, field);
            }
        }
        catch (Exception exception) {
            LOG.error("Erreur lors de la creation du composant ", exception);
        }
        JComponent component = component1;

        field.setComponent(component);
        return component;
    }


    protected static class DynamicClassComponentFactory implements ComponentFactory {
        private Map<String, String> classNameTranslator = new HashMap<String, String>();


        protected DynamicClassComponentFactory(Map<String, String> translator) {
            classNameTranslator = translator;
        }


        public JComponent create(GuiField guiField, Field field) throws Exception {
            @SuppressWarnings({"unchecked"})
            Class<? extends JComponent> clazz
                  = (Class<? extends JComponent>)Class.forName(translate(guiField.getComponentClassName()));
            return clazz.newInstance();
        }


        private String translate(String key) {
            return (classNameTranslator.get(key) == null ? key : classNameTranslator.get(key));
        }
    }
}
