package net.codjo.referential.gui.api;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Before;
import org.junit.Test;

public class ReferentialTest {
    private Referential referential;


    @Before
    public void setUp() throws Exception {
        referential = new Referential();
        referential.addField(new Field("field1", "label1", "string"));
        referential.addField(new Field("field2", "label2", "string"));
    }


    @Test
    public void test_getFieldByName() throws Exception {
        assertThat(referential.getField("field1"), not(nullValue()));
        assertThat(referential.getField("field1").getLabel(), equalTo("label1"));
        assertThat(referential.getField("field1").getType(), equalTo("string"));

        assertThat(referential.getField("field2"), not(nullValue()));
        assertThat(referential.getField("field2").getLabel(), equalTo("label2"));
        assertThat(referential.getField("field2").getType(), equalTo("string"));

        assertThat(referential.getField("toto"), is(nullValue()));
    }
}
