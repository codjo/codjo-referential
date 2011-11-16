package net.codjo.referential.gui.api;
import net.codjo.gui.toolkit.date.NoNullDateField;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.text.TextArea;
import net.codjo.gui.toolkit.text.TextField;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

public class DefaultGuiCustomizer extends CompositeGuiCustomiser {
    private static final Logger LOG = Logger.getLogger(DefaultGuiCustomizer.class);
    private static final List<GuiCustomizer> CUSTOMIZERS = new ArrayList<GuiCustomizer>();


    static {
        CUSTOMIZERS.add(new TextFieldGuiCustomizer());
        CUSTOMIZERS.add(new TextAreaGuiCustomizer());
        CUSTOMIZERS.add(new ComboBoxGuiCustomizer());
        CUSTOMIZERS.add(new NumberFieldGuiCustomizer());
        CUSTOMIZERS.add(new DateGuiCustomizer());
        CUSTOMIZERS.add(new BooleanGuiCustomizer());
        CUSTOMIZERS.add(new AuditGuiCustomizer());
    }


    public DefaultGuiCustomizer() {
        super(CUSTOMIZERS.toArray(new GuiCustomizer[CUSTOMIZERS.size()]));
    }


    @Override
    public boolean handle(Field field) {
        return !(field.isGenerated());
    }


    @Override
    public JComponent createGui(Field field) {
        JComponent createdComponent = super.createGui(field);
        if (createdComponent == null) {
            throw new IllegalArgumentException("Type non supporté :" + field.getType().toLowerCase());
        }
        return createdComponent;
    }


    @Override
    public void declareField(DetailDataSource dataSource, Field field) {
        if (!field.isGenerated()) {
            super.declareField(dataSource, field);
        }
    }


    public static class TextFieldGuiCustomizer extends AbstractGuiCustomizer {

        public boolean handle(Field field) {
            String type = field.getType().toLowerCase();
            return (type.contains("string") && field.getLength() <= 100 && field.getHandlerId() == null);
        }


        public JComponent createGui(Field field) {
            TextField textField = new TextField();
            textField.setMaxTextLength(field.getLength());
            field.setComponent(textField);
            return textField;
        }


        public void initDefaultFieldValue(Field field) {
            ((JTextField)field.getComponent()).setText(field.getDefaultValueWithoutQuotes());
        }
    }

    public static class TextAreaGuiCustomizer extends AbstractGuiCustomizer {

        public boolean handle(Field field) {
            return (field.getType().toLowerCase().contains("string") && field.getHandlerId() == null
                    && field.getLength() > 100);
        }


        public JComponent createGui(Field field) {
            TextArea textArea = new TextArea(field.getLabel(), field.getLength());
            textArea.setRows(3);
            textArea.setColumns(30);
            textArea.setLineWrap(true);
            field.setComponent(textArea);
            return textArea;
        }


        public void initDefaultFieldValue(Field field) {
            ((TextArea)field.getComponent()).setText(field.getDefaultValueWithoutQuotes());
        }
    }

    public static class ComboBoxGuiCustomizer extends AbstractGuiCustomizer {

        public boolean handle(Field field) {
            return ((field.getType().toLowerCase().contains("string") || field.isNumberField())
                    && field.getHandlerId() != null);
        }


        public JComponent createGui(Field field) {
            RequestComboBox requestComboBox = new RequestComboBox();
            requestComboBox.initRequestComboBox("refCode", "refLabel", !field.isRequired());
            field.setComponent(requestComboBox);
            return requestComboBox;
        }


        public void initDefaultFieldValue(Field field) {
            RequestComboBox requestComboBox = (RequestComboBox)field.getComponent();
            requestComboBox.setDataSource(buildFatherDataSource(field.getHandlerId()));
            try {
                requestComboBox.load();
            }
            catch (Exception exception) {
                LOG.error(exception);
                throw new RuntimeException(exception);
            }
            requestComboBox.setSelectedItem(field.getDefaultValueWithoutQuotes());
        }


        protected ListDataSource buildFatherDataSource(String selectHandlerId) {
            ListDataSource listDataSource = new ListDataSource();
            listDataSource.setColumns(new String[]{"refCode", "refLabel"});
            listDataSource.setLoadFactoryId(selectHandlerId);
            return listDataSource;
        }
    }

    public static class NumberFieldGuiCustomizer extends AbstractGuiCustomizer {

        public boolean handle(Field field) {
            return (field.isNumberField() && field.getHandlerId() == null);
        }


        public JComponent createGui(Field field) {
            NumberField numberField = new NumberField();
            Util.initField(numberField, field);
            field.setComponent(numberField);
            return numberField;
        }


        public void initDefaultFieldValue(Field field) {
            ((JTextField)field.getComponent()).setText(field.getDefaultValueWithoutQuotes());
        }
    }

    public static class DateGuiCustomizer extends AbstractGuiCustomizer {

        public boolean handle(Field field) {
            return field.getType().toLowerCase().contains("date");
        }


        public JComponent createGui(Field field) {
            NoNullDateField dateField = new NoNullDateField();
            field.setComponent(dateField);
            return dateField;
        }


        public void initDefaultFieldValue(Field field) {
            // Todo
        }
    }

    public static class BooleanGuiCustomizer extends AbstractGuiCustomizer {

        public boolean handle(Field field) {
            return field.getType().toLowerCase().contains("boolean");
        }


        public JComponent createGui(Field field) {
            JCheckBox checkBox = new JCheckBox();
            field.setComponent(checkBox);
            return checkBox;
        }


        public void initDefaultFieldValue(Field field) {
            ((JCheckBox)field.getComponent()).setSelected("1".equals(field.getDefaultValueWithoutQuotes()));
        }
    }
}
