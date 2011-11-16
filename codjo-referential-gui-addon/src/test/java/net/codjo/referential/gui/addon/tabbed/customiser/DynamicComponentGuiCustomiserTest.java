package net.codjo.referential.gui.addon.tabbed.customiser;

import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.addon.tabbed.customiser.AbstractDynamicGuiCustomiser.ComponentFactory;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.api.Field;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.junit.Before;
import org.junit.Test;

public class DynamicComponentGuiCustomiserTest {
    protected GuiFieldGroup guiFieldGroup;
    protected AbstractDynamicGuiCustomiser customizer;


    private GuiFieldGroup mockGuiFieldGroup() {
        GuiFieldGroup mockedGuiFieldGroup = new GuiFieldGroup("group");
        GuiField field1 = new GuiField("champ1");
        field1.setComponentClassName("javax.swing.JTextField");
        mockedGuiFieldGroup.addGuiField(field1);
        mockedGuiFieldGroup.addGuiField(new GuiField("champ2"));
        return mockedGuiFieldGroup;
    }


    @Before
    public void before() {
        guiFieldGroup = mockGuiFieldGroup();
        customizer = new DynamicComponentGuiCustomiser(guiFieldGroup);
    }


    @Test
    public void test_handle_field() throws Exception {
        Field field1 = new Field("champ1", "testText", "java.lang.String");
        assertThat(customizer.handle(field1), equalTo(true));
        guiFieldGroup.getGuiField("champ1").setComponentClassName("java.lang.String");
        assertThat(customizer.handle(field1), equalTo(false));
        assertThat(customizer.handle(new Field("champ2", "testText", "java.lang.String")), equalTo(false));
    }


    @Test
    public void test_handle_fieldWithHandlerId() throws Exception {
        Field field = new Field("champ1", "testText", "java.lang.String");
        field.setHandlerId("comboHandlerId");
        GuiField guiField = guiFieldGroup.getGuiField("champ1");
        guiField.setComponentClassName("addItemComboBox");
        guiField.setHandlerId("comboHandlerId");
        assertThat(customizer.handle(field), equalTo(false));
    }


    @Test
    public void test_createGui() throws Exception {
        JComponent component = customizer.createGui(new Field("champ1", "fredField", "java.lang.String"));
        assertThat(component, notNullValue());
        assertThat(component instanceof JTextField, equalTo(true));
    }


    @Test
    public void test_createGui_requestComboBox() throws Exception {
        GuiField field1 = guiFieldGroup.getGuiField("champ1");
        field1.setComponentClassName("net.codjo.mad.gui.request.RequestComboBox");
        field1.setHandlerId("monLoloHandler");
        field1.setEditable(false);
        JComponent component = customizer.createGui(new Field("champ1", "fredField", "java.lang.String"));
        assertThat(component, notNullValue());
        assertThat(component instanceof RequestComboBox, equalTo(true));
        RequestComboBox requestComboBox = (RequestComboBox)component;
        assertThat(requestComboBox.getDataSource().getLoadFactory(), not(nullValue()));
        assertThat(requestComboBox.getDataSource().getLoadFactory().getId(), equalTo("monLoloHandler"));
        assertThat(requestComboBox.isEditable(), equalTo(false));
        field1.setEditable(true);
        component = customizer.createGui(new Field("champ1", "fredField", "java.lang.String"));
        assertThat(((RequestComboBox)component).isEditable(), equalTo(true));
    }


    @Test
    public void test_createGui_withKeyWord() throws Exception {
        ComponentFactory componentFactory = new ComponentFactory() {
            public JComponent create(GuiField guiField, Field field) throws Exception {
                return new JLabel();
            }
        };
        customizer.registerComponent(
              "addItemComboBox",
              "net.codjo.smart.gui.tabbed.component.MutableMaxSizeAutoCompleteRequestComboBox",
              componentFactory);

        GuiField field1 = guiFieldGroup.getGuiField("champ1");
        field1.setComponentClassName("addItemComboBox");
        field1.setHandlerId("monLoloHandler");
        field1.setEditable(false);
        JComponent component = customizer.createGui(new Field("champ1", "fredField", "java.lang.String"));
        assertThat(component, notNullValue());
        assertThat(component instanceof JLabel, equalTo(true));
    }
}
