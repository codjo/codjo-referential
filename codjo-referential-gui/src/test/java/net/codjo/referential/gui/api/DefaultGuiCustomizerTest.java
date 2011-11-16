package net.codjo.referential.gui.api;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.text.TextArea;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultGuiCustomizerTest {
    private Field field;
    private GuiCustomizer customizer = new DefaultGuiCustomizer();


    private void assertDateComponent(String fieldType) {
        when(field.getType()).thenReturn(fieldType);
        final JComponent createdComponent = customizer.createGui(field);
        assertThat(createdComponent, not(nullValue()));
        assertThat(createdComponent instanceof DateField, equalTo(true));
    }


    @Before
    public void before() {
        field = mock(Field.class);
        when(field.isPrimaryKey()).thenReturn(false);
    }


    @Test
    public void test_handleField() throws Exception {
        when(field.isAuditField()).thenReturn(false);
        assertThat(customizer.handle(field), equalTo(true));

        when(field.isAuditField()).thenReturn(true);
        assertThat(customizer.handle(field), equalTo(true));
    }


    @Test
    public void test_createGui_withoutHandlerId_smallLength() throws Exception {
        when(field.getType()).thenReturn("java.lang.String");
        when(field.getHandlerId()).thenReturn(null);
        when(field.getLength()).thenReturn(50);

        final JComponent createdComponent = customizer.createGui(field);
        assertThat(createdComponent, not(nullValue()));
        assertThat(createdComponent instanceof TextField, equalTo(true));
        assertThat(((TextField)createdComponent).getText(), equalTo(""));
    }


    @Test
    public void test_createGui_withoutHandlerId_bigLength() throws Exception {
        when(field.getType()).thenReturn("java.lang.String");
        when(field.getHandlerId()).thenReturn(null);
        when(field.getLength()).thenReturn(150);

        final JComponent createdComponent = customizer.createGui(field);
        assertThat(createdComponent, not(nullValue()));
        assertThat(createdComponent instanceof TextArea, equalTo(true));
        assertThat(((TextArea)createdComponent).getText(), equalTo(""));
    }


    @Test
    public void test_createGui_withStringFieldAndHandler() throws Exception {
        when(field.getType()).thenReturn("java.lang.String");
        when(field.getHandlerId()).thenReturn("fredHandler");

        final JComponent createdComponent = customizer.createGui(field);
        assertThat(createdComponent, not(nullValue()));
        assertThat(createdComponent instanceof RequestComboBox, equalTo(true));
        assertThat(((RequestComboBox)createdComponent).getSelectedValue("refCode"), equalTo("null"));
    }


    @Test
    public void test_createGui_withCheckBox() throws Exception {
        when(field.getType()).thenReturn("boolean");

        final JComponent createdComponent = customizer.createGui(field);
        assertThat(createdComponent, not(nullValue()));
        assertThat(createdComponent instanceof JCheckBox, equalTo(true));
        assertThat(((JCheckBox)createdComponent).isSelected(), equalTo(false));
    }


    @Test
    public void test_createGui_withNumberFieldAndNoHandler() throws Exception {
        when(field.getType()).thenReturn("java.lang.Integer");
        when(field.isNumberField()).thenReturn(true);
        when(field.getHandlerId()).thenReturn(null);
        when(field.getLength()).thenReturn(10);

        final JComponent createdComponent = customizer.createGui(field);
        assertThat(createdComponent, not(nullValue()));
        assertThat(createdComponent instanceof NumberField, equalTo(true));
        assertThat(((NumberField)createdComponent).getText(), equalTo(""));
    }


    @Test
    public void test_createGui_withNumberFieldAndHandler() throws Exception {
        when(field.getType()).thenReturn("java.lang.Integer");
        when(field.isNumberField()).thenReturn(true);
        when(field.getHandlerId()).thenReturn("fredHandler");
        when(field.getLength()).thenReturn(10);

        final JComponent createdComponent = customizer.createGui(field);
        assertThat(createdComponent, not(nullValue()));
        assertThat(createdComponent instanceof RequestComboBox, equalTo(true));
        assertThat(((RequestComboBox)createdComponent).getSelectedValue("refCode"), equalTo("null"));
    }


    @Test
    public void test_createGui_withDateField() throws Exception {
        assertDateComponent("java.sql.Date");
        assertDateComponent("java.util.Date");
        assertDateComponent("date");
    }


    @Test
    public void test_declareField_generatedField() throws Exception {
        when(field.isGenerated()).thenReturn(true);
        when(field.getComponent()).thenReturn(new JTextField());
        DetailDataSource datasource = new DetailDataSource(new DefaultGuiContext());
        customizer.declareField(datasource, field);
        assertThat(datasource.getDeclaredFields().isEmpty(), equalTo(true));
    }
}
