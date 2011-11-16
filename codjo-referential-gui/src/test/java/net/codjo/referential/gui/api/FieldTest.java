package net.codjo.referential.gui.api;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Test;

public class FieldTest {
    private Field field;


    @Test
    public void test_isNumberField_typicalNumberTypes() throws Exception {
        field = new Field("testField", "test", "java.lang.Integer");
        assertThat(field.isNumberField(), equalTo(true));

        field = new Field("testField", "test", "java.lang.Double");
        assertThat(field.isNumberField(), equalTo(true));

        field = new Field("testField", "test", "java.lang.Float");
        assertThat(field.isNumberField(), equalTo(true));

        field = new Field("testField", "test", "java.lang.Long");
        assertThat(field.isNumberField(), equalTo(true));
    }


    @Test
    public void test_isNumberField_bigDecimal() throws Exception {
        field = new Field("testField", "test", "big-decimal");
        assertThat(field.isNumberField(), equalTo(true));
    }


    @Test
    public void test_isNumberField_StringType() throws Exception {
        field = new Field("testField", "test", "java.lang.String");
        assertThat(field.isNumberField(), equalTo(false));
    }


    @Test
    public void test_equalsAndHashCode() throws Exception {
        Field field1 = new Field("field", "label", "java.lang.String");
        Field field2 = new Field("field", "label", "java.lang.String");
        assertThat(field1, equalTo(field2));
        assertThat(field1.hashCode(), equalTo(field2.hashCode()));
        field2.setType("java.lang.Integer");
        assertThat(field1, not(equalTo(field2)));
        assertThat(field1, not(equalTo(null)));
        field2.setType("java.lang.String");
        field2.setName("otherField");
        assertThat(field1, not(equalTo(field2)));
    }


    @Test
    public void test_getDefaultValueWithoutQuotes() throws Exception {
        field = new Field("field", "label", "java.lang.String");
        field.setDefaultValue("defaultValue");
        assertThat(field.getDefaultValueWithoutQuotes(), equalTo("defaultValue"));
        field.setDefaultValue("\'VALUE\'");
        assertThat(field.getDefaultValueWithoutQuotes(), equalTo("VALUE"));
    }
}
