package net.codjo.referential.gui.addon.tabbed.customiser;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import net.codjo.referential.gui.api.Field;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;

public class RelatedFieldValueUpdater implements PropertyChangeListener {
    private static final Logger LOG = Logger.getLogger(RelatedFieldValueUpdater.class);
    private static final List<FieldSetter> FIELD_SETTERS = new ArrayList<FieldSetter>();
    private RelatedGuiField relatedGuiField;
    private JComponent relatedGuiFieldComponent;
    private RequestComboBox comboBox;


    static {
        FIELD_SETTERS.add(new DateFieldSetter());
        FIELD_SETTERS.add(new NumberFieldSetter());
        FIELD_SETTERS.add(new JTextComponentFieldSetter());
    }


    RelatedFieldValueUpdater(RelatedGuiField relatedGuiField,
                             JComponent relatedGuiFieldComponent, RequestComboBox comboBox) {
        this.relatedGuiField = relatedGuiField;
        this.comboBox = comboBox;
        this.relatedGuiFieldComponent = relatedGuiFieldComponent;
    }


    public void propertyChange(PropertyChangeEvent evt) {
        updateValue();
    }


    public static void activate(Field masterField, RelatedGuiField relatedGuiField) {
        RequestComboBox comboBox = (RequestComboBox)masterField.getComponent();
        RelatedFieldValueUpdater listener =
              new RelatedFieldValueUpdater(relatedGuiField,
                                           relatedGuiField.getComponent(),
                                           comboBox);
        comboBox.getDataSource().addPropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY, listener);
        listener.updateValue();
    }


    void updateValue() {
        String value = comboBox.getSelectedValue(relatedGuiField.getName());
        for (FieldSetter fieldSetter : FIELD_SETTERS) {
            if (fieldSetter.handle(relatedGuiFieldComponent)) {
                fieldSetter.setValue(relatedGuiFieldComponent, value);
                break;
            }
        }
    }


    private static interface FieldSetter {

        public void setValue(JComponent component, String value);


        public boolean handle(JComponent component);
    }

    private static class JTextComponentFieldSetter implements FieldSetter {

        public void setValue(JComponent component, String value) {
            initLength(value, component);
            ((JTextComponent)component).setText(("null".equals(value) ? "" : value));
        }


        public boolean handle(JComponent component) {
            return (component != null && (component instanceof JTextComponent));
        }


        private void initLength(String text, JComponent component) {
            if (text == null) {
                return;
            }
            if (component instanceof TextField) {
                ((TextField)component).setMaxTextLength(text.length() + 1);
            }
        }
    }

    private static class DateFieldSetter implements FieldSetter {
        private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");


        public void setValue(JComponent component, String value) {
            if ("null".equals(value)) {
                value = null;
            }
            ((DateField)component).setDate(parseValue(value));
        }


        private Date parseValue(String value) {
            if (value == null) {
                return null;
            }
            Date date = null;
            try {
                date = DEFAULT_DATE_FORMAT.parse(value);
            }
            catch (ParseException exception) {
                LOG.error("Le format de la date doit être : dd/MM/yyyy", exception);
            }
            return date;
        }


        public boolean handle(JComponent component) {
            return (component != null && (component instanceof DateField));
        }
    }

    private static class NumberFieldSetter implements FieldSetter {

        public void setValue(JComponent component, String value) {
            NumberField numberField = (NumberField)component;
            if ("null".equals(value)) {
                numberField.setNumber(null);
                return;
            }
            BigDecimal number = getNumber(value);
            numberField.setMaximumFractionDigits(number.precision());
            numberField.setNumber(number);
        }


        private static BigDecimal getNumber(String value) {
            if (value == null) {
                return null;
            }
            else {
                return new BigDecimal(value);
            }
        }


        public boolean handle(JComponent component) {
            return (component != null && (component instanceof NumberField));
        }
    }
}