package net.codjo.referential.gui.addon.tabbed.customiser;

import net.codjo.gui.toolkit.date.DateField;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.addon.tabbed.customiser.AbstractDynamicGuiCustomiser.ComponentFactory;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RelatedFieldsGuiCustomiserTest {
    private GuiFieldGroup guiFieldGroup;
    private Referential mockedReferential;
    private Field mockedDateField;
    private Field mockedStringField;
    private Field mockedComboField;
    private GuiField mockedStringGuiField;
    private GuiField mockedComboGuiField;
    private GuiReferential guiReferential;


    @Before
    public void before() {
        initMockedFields();
        initReferential();
        initMockedStringGuiFields();
        initMockedDateGuiFields();
        initMockedComboGuiFields();
    }


    private void initMockedFields() {
        mockedDateField = mock(Field.class);
        when(mockedDateField.getName()).thenReturn("mockedDateField");
        when(mockedDateField.getLabel()).thenReturn("mockedDateFieldLabel");
        when(mockedDateField.getType()).thenReturn("java.util.Date");
        when(mockedDateField.getHandlerId()).thenReturn(null);

        mockedStringField = mock(Field.class);
        when(mockedStringField.getName()).thenReturn("mockedStringField");
        when(mockedStringField.getLabel()).thenReturn("mockedStringFieldLabel");
        when(mockedStringField.getType()).thenReturn("java.lang.String");
        when(mockedStringField.getComponent()).thenReturn(new JTextField());
        when(mockedStringField.getHandlerId()).thenReturn(null);

        mockedComboField = new Field("mockedComboField", "mockedComboFieldLabel", "java.lang.String");
        mockedComboField.setHandlerId("comboHandlerId");
    }


    private void initReferential() {
        mockedReferential = mock(Referential.class);
        when(mockedReferential.getField("mockedDateField")).thenReturn(mockedDateField);
        when(mockedReferential.getField("mockedStringField")).thenReturn(mockedStringField);
        when(mockedReferential.getField("mockedComboField")).thenReturn(mockedComboField);
        guiFieldGroup = new GuiFieldGroup("mockedGuiFieldGroup");
        guiReferential = new GuiReferential();
        guiReferential.add(guiFieldGroup);
    }


    private void initMockedStringGuiFields() {
        mockedStringGuiField = mock(GuiField.class);
        when(mockedStringGuiField.getName()).thenReturn("mockedStringField");
        List<RelatedGuiField> relatedGuiFields = new ArrayList<RelatedGuiField>();
        relatedGuiFields.add(new RelatedGuiField("mockedDateField", "mockedDateFieldLabel"));
        when(mockedStringGuiField.getRelatedGuiFields()).thenReturn(relatedGuiFields);
        when(mockedStringGuiField.hasRelatedGuiFields()).thenReturn(true);
        when(mockedStringGuiField.getHandlerId()).thenReturn(null);
        guiFieldGroup.addGuiField(mockedStringGuiField);
    }


    private void initMockedDateGuiFields() {
        GuiField mockedDateGuiField = mock(GuiField.class);
        when(mockedDateGuiField.getName()).thenReturn("mockedDateField");
        when(mockedDateGuiField.getRelatedGuiFields()).thenReturn(new ArrayList<RelatedGuiField>());
        when(mockedDateGuiField.hasRelatedGuiFields()).thenReturn(false);
        guiFieldGroup.addGuiField(mockedDateGuiField);
    }


    private void initMockedComboGuiFields() {
        mockedComboGuiField = mock(GuiField.class);
        when(mockedComboGuiField.getName()).thenReturn("mockedComboField");
        List<RelatedGuiField> relatedGuiFields = new ArrayList<RelatedGuiField>();
        relatedGuiFields.add(new RelatedGuiField("mockedDateField", "mockedDateFieldLabel"));

        when(mockedComboGuiField.getRelatedGuiFields()).thenReturn(relatedGuiFields);
        when(mockedComboGuiField.hasRelatedGuiFields()).thenReturn(true);
        when(mockedComboGuiField.getRelatedGuiFields()).thenReturn(relatedGuiFields);
        when(mockedComboGuiField.getHandlerId()).thenReturn("comboHandlerId");

        guiFieldGroup.addGuiField(mockedComboGuiField);
    }


    @Test
    public void test_handleField_withSlaves() throws Exception {
        mockedStringGuiField.addRelatedGuiFields(new RelatedGuiField("slave", "slave"));
        RelatedFieldsGuiCustomiser customiser = new RelatedFieldsGuiCustomiser(guiFieldGroup,
                                                                               mockedReferential,
                                                                               guiReferential);
        assertThat(customiser.handle(mockedStringField), equalTo(true));
    }


    @Test
    public void test_handleField_ko() throws Exception {
        when(mockedStringGuiField.getRelatedGuiFields()).thenReturn(new ArrayList<RelatedGuiField>());
        when(mockedStringGuiField.hasRelatedGuiFields()).thenReturn(false);
        RelatedFieldsGuiCustomiser customiser = new RelatedFieldsGuiCustomiser(guiFieldGroup, mockedReferential, guiReferential);
        assertThat(customiser.handle(mockedStringField), equalTo(false));
    }


    @Test
    public void test_handleField_seperateGuiGroup() throws Exception {
        guiReferential.getFieldsGroup().clear();
        GuiFieldGroup group1 = new GuiFieldGroup("group1");
        group1.addGuiField(new GuiField("mockedComboField"));
        group1.addGuiField(new GuiField("mockedStringField"));
        group1.addGuiField(new GuiField("mockedDateField"));
        guiReferential.add(group1);
        GuiFieldGroup group2 = new GuiFieldGroup("group2");
        group2.addGuiField(mockedComboGuiField);
        guiReferential.add(group2);
        RelatedFieldsGuiCustomiser customiser = new RelatedFieldsGuiCustomiser(group1,
                                                                               mockedReferential,
                                                                               guiReferential);
        assertThat(customiser.handle(mockedComboField), equalTo(true));
    }


    @Test
    public void test_createDateField_withNoRelatedFields() throws Exception {
        assertThat(guiFieldGroup.getGuiField(mockedDateField.getName()), not(nullValue()));
        RelatedFieldsGuiCustomiser customizer = new RelatedFieldsGuiCustomiser(guiFieldGroup,
                                                                               mockedReferential,
                                                                               guiReferential);
        JComponent component = customizer.createGui(mockedDateField);
        assertThat(component, not(nullValue()));
        assertThat(component instanceof Box, equalTo(true));
        assertThat(component.getComponentCount(), equalTo(0));
    }


    @Test
    public void test_addRelatedGuiFields() throws Exception {
        RelatedFieldsGuiCustomiser customizer = new RelatedFieldsGuiCustomiser(guiFieldGroup,
                                                                               mockedReferential,
                                                                               guiReferential);
        Box container = new Box(1);
        customizer.addRelatedGuiFields(mockedStringField, mockedStringGuiField, container);
        assertThat(container.getComponentCount(), equalTo(2));
        assertThat(container.getComponent(0) instanceof JLabel, equalTo(true));
        assertThat(container.getComponent(1) instanceof DateField, equalTo(true));
        assertThat(container.getComponent(1).getName(), equalTo("mockedDateField"));
    }


    @Test
    public void test_addRelatedGuiFields_withNoRootDisplayed() throws Exception {
        when(mockedStringField.getHandlerId()).thenReturn(null);
        RelatedFieldsGuiCustomiser customizer = new RelatedFieldsGuiCustomiser(guiFieldGroup,
                                                                               mockedReferential,
                                                                               guiReferential);
        JComponent component = customizer.createGui(mockedStringField);
        assertThat(component, not(nullValue()));
        assertThat(component.getComponentCount(), equalTo(2));
        assertThat(component.getComponent(0) instanceof JLabel, equalTo(true));
        assertThat(component.getComponent(1) instanceof DateField, equalTo(true));
    }


    @Test
    public void test_addRelatedGuiFields_withRootDisplayed() throws Exception {
        when(mockedStringField.getHandlerId()).thenReturn(null);
        when(mockedStringGuiField.isRootDisplayed()).thenReturn(true);
        RelatedFieldsGuiCustomiser customizer = new RelatedFieldsGuiCustomiser(guiFieldGroup,
                                                                               mockedReferential,
                                                                               guiReferential);
        JComponent component = customizer.createGui(mockedStringField);
        assertThat(component, not(nullValue()));
        assertThat(component.getComponentCount(), equalTo(4));
        Component rootComponent = component.getComponent(0);

        assertThat(rootComponent instanceof JTextField, equalTo(true));
        assertThat(rootComponent.getName(), equalTo("mockedStringField"));
        assertThat(rootComponent.isEnabled(), equalTo(true));
        //noinspection ConstantConditions
        assertThat(((JTextField)rootComponent).isEditable(), equalTo(true));

        assertThat(component.getComponent(1) instanceof Container, equalTo(true));
        assertThat(component.getComponent(2) instanceof JLabel, equalTo(true));
        assertThat(component.getComponent(3) instanceof DateField, equalTo(true));
    }


    @Test
    public void test_addRelatedGuiFields_withDisplayedComboBox() throws Exception {
        when(mockedComboGuiField.isRootDisplayed()).thenReturn(true);
        when(mockedComboGuiField.getComponentClassName()).thenReturn("addItemComboBox");
        when(mockedComboGuiField.getHandlerId()).thenReturn("comboHandlerId");
        RelatedFieldsGuiCustomiser relatedFieldsGuiCustomizer = new RelatedFieldsGuiCustomiser(
              guiFieldGroup,
              mockedReferential, guiReferential);
        relatedFieldsGuiCustomizer.registerComponentFactory("addItemComboBox",
                                                            new ComponentFactory<RequestComboBox>() {
                                                                public RequestComboBox create(GuiField guiField,
                                                                                              Field field)
                                                                      throws Exception {
                                                                    return new RequestComboBox();
                                                                }
                                                            });

        JComponent component = relatedFieldsGuiCustomizer.createGui(mockedComboField);
        assertThat(component, not(nullValue()));
        assertThat(component instanceof Box, equalTo(true));
        assertThat(component.getComponentCount(), equalTo(4));
        Component rootComponent = component.getComponent(0);
        assertThat(rootComponent.getName(), equalTo("mockedComboField"));
        assertThat(rootComponent instanceof RequestComboBox, equalTo(true));
        assertThat(component.getComponent(2) instanceof JLabel, equalTo(true));
        assertThat(component.getComponent(3) instanceof DateField, equalTo(true));
    }
}
