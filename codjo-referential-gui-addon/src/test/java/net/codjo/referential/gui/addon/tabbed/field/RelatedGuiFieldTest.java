package net.codjo.referential.gui.addon.tabbed.field;
import net.codjo.referential.gui.api.Field;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Test;

public class RelatedGuiFieldTest {

    @Test
    public void test_equals() throws Exception {
        RelatedGuiField field1 = new RelatedGuiField("field1", "label1");
        RelatedGuiField field2 = new RelatedGuiField("field1", "label1");
        assertThat(field1, equalTo(field2));
        assertThat(field1.hashCode(), equalTo(field2.hashCode()));
        field2.setLabel("otherLabel");
        assertThat(field1, not(equalTo(field2)));
        assertThat(field1, not(equalTo(null)));
    }


    @Test
    public void test_equalsAndHashCode_masterField() throws Exception {
        Field masterField = new Field("masterField", "label", "java.lang.String");
        RelatedGuiField field1 = new RelatedGuiField("field1", "label1");
        RelatedGuiField field2 = new RelatedGuiField("field1", "label1");
        field1.setMasterField(masterField);
        field2.setMasterField(masterField);
        assertThat(field1, equalTo(field2));
        assertThat(field1.hashCode(), equalTo(field2.hashCode()));
        field2.setMasterField(null);
        assertThat(field1, not(equalTo(field2)));
    }
}
