package net.codjo.referential.gui.api;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizerAdapter;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.junit.Before;
import org.junit.Test;

public class CompositeGuiCustomiserTest {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private CompositeGuiCustomiser customizer;
    private Field testDateField;
    private Field testFredField;
    private Field testStringField;


    @Before
    public void before() {
        testDateField = new Field("testDate", "testDate", "java.util.Date");
        testDateField.setDefaultValue("15/06/2010");
        testFredField = new Field("fredField", "Fred's field", "java.math.BigDecimal");
        testFredField.setDefaultValue("12");
        testStringField = new Field("testStringDate", "testDate", "string");
        testStringField.setDefaultValue("defaultValue");

        customizer = new CompositeGuiCustomiser();
        customizer.addCustomizer(new TestDateFieldGuiCustomiser());
        customizer.addCustomizer(new TestFieldNameGuiCustomiser("fredField"));
    }


    @Test
    public void test_handleField() throws Exception {
        assertThat(customizer.handle(testStringField), equalTo(false));
        assertThat(customizer.handle(testDateField), equalTo(true));
        assertThat(customizer.handle(testFredField), equalTo(true));
    }


    @Test
    public void test_createGui() throws Exception {
        assertThat(customizer.createGui(testDateField) instanceof DateField, equalTo(true));
        JComponent component = customizer.createGui(testFredField);
        assertThat(component, not(nullValue()));
        assertThat(component instanceof JTextField, equalTo(true));
        assertThat(customizer.createGui(testStringField), is(nullValue()));
    }


    @Test
    public void test_createGui_withOrder() throws Exception {
        customizer = new CompositeGuiCustomiser();
        customizer.addCustomizer(new TestFieldNameGuiCustomiser("fredField"));
        customizer.addCustomizer(new TestDateFieldGuiCustomiser());
        assertThat(customizer.createGui(testDateField) instanceof DateField, equalTo(true));
        testDateField.setName("fredField");
        assertThat(customizer.createGui(testDateField) instanceof JTextField, equalTo(true));
    }


    @Test
    public void test_initDefaultValue() throws Exception {
        customizer.initDefaultFieldValue(testDateField);
        assertThat(DATE_FORMAT.format(((DateField)customizer.createGui(testDateField)).getDate()),
                   equalTo("15/06/2010"));
        customizer.initDefaultFieldValue(testFredField);
        assertThat(((JTextField)customizer.createGui(testFredField)).getText(), equalTo("12"));
    }


    private static class TestDateFieldGuiCustomiser extends GuiCustomizerAdapter {
        private DateField dateField = new DateField();


        @Override
        public boolean handle(Field field) {
            return (field != null && field.getType().toLowerCase().contains("date"));
        }


        @Override
        public JComponent createGui(Field field) {
            return dateField;
        }


        @Override
        public void initDefaultFieldValue(Field field) {
            try {
                dateField.setDate(DATE_FORMAT.parse(field.getDefaultValue()));
            }
            catch (ParseException e) {
                // EMPTY
            }
        }
    }

    private static class TestFieldNameGuiCustomiser extends GuiCustomizerAdapter {
        private String fieldName;
        private JTextField jTextField = new JTextField();


        private TestFieldNameGuiCustomiser(String fieldName) {
            this.fieldName = fieldName;
        }


        @Override
        public boolean handle(Field field) {
            return (field != null && fieldName.equals(field.getName()));
        }


        @Override
        public JComponent createGui(Field field) {
            return jTextField;
        }


        @Override
        public void initDefaultFieldValue(Field field) {
            jTextField.setText(field.getDefaultValue());
        }
    }
}
