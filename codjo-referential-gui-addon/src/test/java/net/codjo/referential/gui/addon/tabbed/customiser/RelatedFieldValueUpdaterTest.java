package net.codjo.referential.gui.addon.tabbed.customiser;

import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.text.SimpleDateFormat;
import javax.swing.JTextField;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RelatedFieldValueUpdaterTest {
    private RequestComboBox mockedComboBox;


    @Before
    public void before() {
        mockedComboBox = mock(RequestComboBox.class);
        when(mockedComboBox.getSelectedValue("fred")).thenReturn("fredValue");
    }


    @Test
    public void test_updateValue() throws Exception {
        RelatedGuiField relatedGuiField = new RelatedGuiField("fred", "label");
        JTextField textField = new JTextField();
        RelatedFieldValueUpdater updater = new RelatedFieldValueUpdater(relatedGuiField, textField,
                                                                        mockedComboBox);
        updater.updateValue();
        assertThat(textField.getText(), equalTo("fredValue"));
    }


    @Test
    public void test_updateValue_notEnabledAndnotEditable() throws Exception {
        RelatedGuiField relatedGuiField = new RelatedGuiField("fred", "label");
        JTextField textField = new JTextField();
        textField.setEnabled(false);
        textField.setEditable(false);
        RelatedFieldValueUpdater updater = new RelatedFieldValueUpdater(relatedGuiField, textField,
                                                                        mockedComboBox);
        updater.updateValue();
        assertThat(textField.getText(), equalTo("fredValue"));
    }


    @Test
    public void test_updateValue_withNullMaxLength() throws Exception {
        RelatedGuiField relatedGuiField = new RelatedGuiField("fred", "label");
        TextField textField = new TextField();
        textField.setMaxTextLength(0);
        RelatedFieldValueUpdater updater = new RelatedFieldValueUpdater(relatedGuiField, textField,
                                                                        mockedComboBox);
        updater.updateValue();
        assertThat(textField.getText(), equalTo("fredValue"));
    }


    @Test
    public void test_updateValue_withDate() throws Exception {
        when(mockedComboBox.getSelectedValue("fred")).thenReturn("15/06/2009");
        RelatedGuiField relatedGuiField = new RelatedGuiField("fred", "label");
        DateField dateField = new DateField();
        RelatedFieldValueUpdater updater = new RelatedFieldValueUpdater(relatedGuiField, dateField,
                                                                        mockedComboBox);
        updater.updateValue();
        assertThat(dateField.getDate(), equalTo(new SimpleDateFormat("dd/MM/yyyy").parse("15/06/2009")));
    }


    @Test
    public void test_updateValue_withNumber() throws Exception {
        when(mockedComboBox.getSelectedValue("fred")).thenReturn("1.23");
        RelatedGuiField relatedGuiField = new RelatedGuiField("fred", "label");
        NumberField numberField = new NumberField();
        RelatedFieldValueUpdater updater = new RelatedFieldValueUpdater(relatedGuiField,
                                                                        numberField,
                                                                        mockedComboBox);
        updater.updateValue();
        Number number = numberField.getNumber();
        assertThat(number, notNullValue());
        assertThat(number.doubleValue(), equalTo(1.23));

        when(mockedComboBox.getSelectedValue("fred")).thenReturn("3");
        updater.updateValue();
        assertThat(numberField.getNumber(), notNullValue());
        assertThat(numberField.getNumber().intValue(), equalTo(3));
    }


    @Test
    public void test_numberField_wrongFractionDigit() throws Exception {
        when(mockedComboBox.getSelectedValue("fred")).thenReturn("1.1576");
        RelatedGuiField relatedGuiField = new RelatedGuiField("fred", "label");
        NumberField numberField = new NumberField();
        numberField.setMaximumFractionDigits(0);
        RelatedFieldValueUpdater updater = new RelatedFieldValueUpdater(relatedGuiField,
                                                                        numberField,
                                                                        mockedComboBox);

        updater.updateValue();
        assertThat(numberField.getNumber().doubleValue(), equalTo(1.1576));
    }
}
