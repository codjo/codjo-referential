package net.codjo.referential.gui.addon.tabbed.field;
import net.codjo.referential.gui.addon.tabbed.util.filter.Filter;
import net.codjo.referential.gui.api.Field;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class GuiFieldGroupTest {
    private Map<String, Field> allFields = new HashMap<String, Field>();
    private GuiFieldGroup guiFieldGroup;


    @Before
    public void before() {
        allFields.put("field1", new Field("field1", "label1", "java.lang.String"));
        allFields.put("field2", new Field("field2", "label2", "java.lang.String"));
        allFields.put("field3", new Field("field3", "label3", "java.lang.String"));
        allFields.put("field4", new Field("field4", "label4", "java.lang.String"));

        guiFieldGroup = new GuiFieldGroup("title");
        guiFieldGroup.addGuiField(new GuiField("field1"));
        guiFieldGroup.addGuiField(new GuiField("field2"));
        guiFieldGroup.addGuiField(new GuiField("field3"));
        GuiField guiField4 = new GuiField("field4");
        guiField4.addRelatedGuiFields(new RelatedGuiField("field2", "label2"));
        guiFieldGroup.addGuiField(guiField4);
    }


    @Test
    public void test_getFields_withFilter() throws Exception {
        Field field = allFields.get("field4");
        field.setType("java.lang.Integer");
        Collection<Field> filteredFields = guiFieldGroup.getFields(allFields,
                                                                   new TypeFieldFilter("java.lang.String"));
        assertThat(filteredFields.size(), equalTo(3));
        assertThat(filteredFields.contains(allFields.get("field1")), equalTo(true));
        assertThat(filteredFields.contains(allFields.get("field2")), equalTo(true));
        assertThat(filteredFields.contains(allFields.get("field3")), equalTo(true));
        assertThat(filteredFields.contains(allFields.get("field4")), equalTo(false));
    }


    @Test
    public void test_getFields_withoutFilter() throws Exception {
        allFields.put("field5", new Field("field5", "label5", "java.lang.String"));
        Collection<Field> filteredFields = guiFieldGroup.getFields(allFields);
        assertThat(filteredFields.size(), equalTo(4));
        assertThat(filteredFields.contains(allFields.get("field1")), equalTo(true));
        assertThat(filteredFields.contains(allFields.get("field2")), equalTo(true));
        assertThat(filteredFields.contains(allFields.get("field3")), equalTo(true));
        assertThat(filteredFields.contains(allFields.get("field4")), equalTo(true));
        assertThat(filteredFields.contains(allFields.get("field5")), equalTo(false));
    }


    @Test
    public void test_getRelatedGuiFields() throws Exception {
        assertThat(guiFieldGroup.hasRelatedGuiField("field2"), equalTo(true));
        assertThat(guiFieldGroup.hasRelatedGuiField("field1"), equalTo(false));
        GuiField guiField3 = guiFieldGroup.getGuiField(allFields.get("field3").getName());
        guiField3.addRelatedGuiFields(new RelatedGuiField("field1", "label1"));
        assertThat(guiFieldGroup.hasRelatedGuiField("field1"), equalTo(true));
        assertThat(guiFieldGroup.hasRelatedGuiField("field3"), equalTo(false));
        assertThat(guiFieldGroup.hasRelatedGuiField("field4"), equalTo(false));
    }


    private static class TypeFieldFilter implements Filter<Field> {
        private String type;


        private TypeFieldFilter(String type) {
            this.type = type;
        }


        public Collection<Field> filter(Collection<Field> fields) {
            Collection<Field> filteredFields = new ArrayList<Field>();
            for (Field field : fields) {
                if (field != null && type.equals(field.getType())) {
                    filteredFields.add(field);
                }
            }
            return filteredFields;
        }
    }
}
