package net.codjo.referential.gui.addon.tabbed.customiser;
import net.codjo.mad.gui.request.AutoCompleteRequestComboBox;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.addon.tabbed.customiser.AbstractDynamicGuiCustomiser.ComponentFactory;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import net.codjo.referential.gui.api.Field;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import org.junit.Before;
import org.junit.Test;

public class MasterFieldGuiCustomizerTest {
    private Field field;
    private GuiField guiField;
    private MasterFieldGuiCustomizer customizer;
    private Map<String, ComponentFactory<? extends RequestComboBox>> comboFactoryMap;
    private GuiReferential guiReferential;
    private GuiFieldGroup guiFieldGroup;


    @Before
    public void setUp() {
        field = new Field("field", "field", "varchar");
        guiField = new GuiField("field");
        guiFieldGroup = new GuiFieldGroup("guiFieldGroup");
        guiFieldGroup.addGuiField(guiField);
        guiReferential = new GuiReferential();
        guiReferential.add(guiFieldGroup);
        comboFactoryMap = new HashMap<String, ComponentFactory<? extends RequestComboBox>>();
        Map<String, String> classNameTranslator = new HashMap<String, String>();
        customizer = new MasterFieldGuiCustomizer(guiReferential, comboFactoryMap, classNameTranslator);
    }


    @Test
    public void test_handleField() throws Exception {
        assertThat(customizer.handle(field), equalTo(false));
        guiField.setHandlerId("monHandlerId");
        assertThat(customizer.handle(field), equalTo(true));
    }


    @Test
    public void test_handleField_seperateGuiGroup() throws Exception {
        assertThat(customizer.handle(field), equalTo(false));
        GuiFieldGroup newGuiFieldGroup = new GuiFieldGroup("newGroup");
        GuiField guiField2 = new GuiField("field");
        guiField2.setHandlerId("handlerId");
        newGuiFieldGroup.addGuiField(guiField2);
        newGuiFieldGroup.addGuiField(new GuiField("field"));
        guiReferential.add(newGuiFieldGroup);
        assertThat(customizer.handle(field), equalTo(true));
    }


    @Test
    public void test_createColumns() throws Exception {
        String[] columns = customizer.createColumns(guiField);
        assertThat(columns.length, equalTo(2));
        assertThat(columns[0], equalTo("refCode"));
        assertThat(columns[1], equalTo("refLabel"));
        guiField.addRelatedGuiFields(new RelatedGuiField("relatedGuiField1", "related1"));
        guiField.addRelatedGuiFields(new RelatedGuiField("relatedGuiField2", "related2"));
        columns = customizer.createColumns(guiField);
        assertThat(columns.length, equalTo(4));
        assertThat(columns[0], equalTo("refCode"));
        assertThat(columns[1], equalTo("refLabel"));
        assertThat(columns[2], equalTo("relatedGuiField1"));
        assertThat(columns[3], equalTo("relatedGuiField2"));
    }


    @Test
    public void test_createComboBox_nominal() throws Exception {
        guiField.setHandlerId("monHandleId");
        assertThat(guiField.getComponentClassName(), is(nullValue()));
        final JComponent component = customizer.createGui(field);
        assertThat(component, not(nullValue()));
        assertThat(component instanceof AutoCompleteRequestComboBox, equalTo(true));
        RequestComboBox comboBox = (RequestComboBox)component;
        assertThat(comboBox.getDataSource().getLoadFactory().getId(), equalTo("monHandleId"));
        assertThat(comboBox.getColumns().length, equalTo(2));
    }


    @Test
    public void test_createComboBox_separatedGuiFieldGroup() throws Exception {
        guiReferential.getFieldsGroup().clear();
        guiFieldGroup.getGuiFields().clear();
        guiFieldGroup.addGuiField(new GuiField("field"));
        guiFieldGroup.addGuiField(new GuiField("field2"));
        guiFieldGroup.addGuiField(new GuiField("field3"));
        guiReferential.add(guiFieldGroup);
        GuiFieldGroup guiFieldGroup2 = new GuiFieldGroup("guiFieldGroup2");
        guiField.setHandlerId("monHandlerId");
        guiField.addRelatedGuiFields(new RelatedGuiField("relatedGuiField", "relatedGuiField"));
        guiFieldGroup2.addGuiField(guiField);
        guiReferential.add(guiFieldGroup2);

        JComponent component = customizer.createGui(field);
        assertThat(component, not(nullValue()));
        assertThat(component instanceof RequestComboBox, equalTo(true));
        final List<String> columnList = Arrays.asList(((RequestComboBox)component).getColumns());
        assertThat(columnList.size(), equalTo(3));
        assertThat(columnList.contains("refLabel"), equalTo(true));
        assertThat(columnList.contains("refCode"), equalTo(true));
        assertThat(columnList.contains("relatedGuiField"), equalTo(true));
    }


    @Test
    public void test_createComboBox_withFactory() throws Exception {
        comboFactoryMap.put(TestRequestComboBoxFactory.class.getName(), new TestRequestComboBoxFactory());
        guiField.setComponentClassName(TestRequestComboBoxFactory.class.getName());
        guiField.setHandlerId("handlerId");
        assertThat(customizer.handle(field), equalTo(true));
        final JComponent component = customizer.createGui(field);
        assertThat(component, not(nullValue()));
        assertThat(component instanceof TestRequestComboBox, equalTo(true));
    }


    private static class TestRequestComboBoxFactory implements ComponentFactory<RequestComboBox> {

        public RequestComboBox create(GuiField guiField, Field field) throws Exception {
            return new TestRequestComboBox();
        }
    }

    private static class TestRequestComboBox extends RequestComboBox {
        // Just for test !
    }
}
