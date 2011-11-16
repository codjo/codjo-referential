package net.codjo.referential.gui;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.api.DefaultGuiCustomizer;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class ReferentialDetailLogicTest {
    private GuiCustomizer defaultGuiCustomizer = new DefaultGuiCustomizer();
    private MadServerFixture serverFixture = new MadServerFixture();


    private static RequestComboBox createRequestComboBox(Field field) {
        RequestComboBox requestComboBox = new RequestComboBox();
        requestComboBox.initRequestComboBox("refCode", "refLabel", !field.isRequired());
        return requestComboBox;
    }


    @Before
    public void before() throws Exception {
        serverFixture.doSetUp();
    }


    @After
    public void after() throws Exception {
        serverFixture.doTearDown();
    }


    @Test
    public void initCheckBoxWithDefaultValue() {
        Field field = new Field();
        field.setType("boolean");
        field.setComponent(new JCheckBox());
        field.setDefaultValue("1");

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertTrue(((JCheckBox)field.getComponent()).isSelected());
    }


    @Test
    public void initCheckBoxWithoutDefaultValue() {
        Field field = new Field();
        field.setType("boolean");
        field.setComponent(new JCheckBox());

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertFalse(((JCheckBox)field.getComponent()).isSelected());
    }


    @Test
    public void initNumberFieldWithDefaultValue() {
        Field field = new Field();
        field.setType("integer");
        field.setComponent(new JTextField());
        field.setDefaultValue("15");

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("15", ((JTextField)field.getComponent()).getText());
    }


    @Test
    public void initNumberFieldWithoutDefaultValue() {
        Field field = new Field();
        field.setType("integer");
        field.setComponent(new JTextField());

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("", ((JTextField)field.getComponent()).getText());
    }


    @Test
    public void initTextComponentWithDefaultValue() {
        Field field = new Field();
        field.setType("string");
        field.setComponent(new JTextField());
        field.setDefaultValue("myDefaultStringValue");

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("myDefaultStringValue", ((JTextField)field.getComponent()).getText());
    }


    @Test
    public void initTextComponentWithDefaultValueWithQuotes() throws Exception {
        Field field = new Field();
        field.setType("string");
        field.setComponent(new JTextField());
        field.setDefaultValue("'myDefaultStringValue'");

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("myDefaultStringValue", ((JTextField)field.getComponent()).getText());
    }


    @Test
    public void initTextComponentWithoutDefaultValue() {
        Field field = new Field();
        field.setType("string");
        field.setComponent(new JTextField());

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("", ((JTextField)field.getComponent()).getText());
    }


    @Test
    public void initComboWithStringDefaultValue() {
        Field field = new Field();
        field.setType("string");
        field.setHandlerId("loadComboHandler");
        field.setDefaultValue("myDefaultStringValue");

        Result loadResult = buildSimpleResult("refCode",
                                              "refLabel",
                                              new String[]{"code1", "myDefaultStringValue", "code2"}
        );
        serverFixture.mockServerResult(loadResult);
        field.setComponent(createRequestComboBox(field));
        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("myDefaultStringValue",
                     ((RequestComboBox)field.getComponent()).getSelectedValue("refCode"));
    }


    @Test
    public void initComboWithUnknownStringDefaultValue() {
        Field field = new Field();
        field.setType("string");
        field.setHandlerId("loadComboHandler");
        field.setDefaultValue("'myUnknownDefaultStringValue'");

        serverFixture.mockServerResult(buildSimpleResult("refCode",
                                                         "refLabel",
                                                         new String[]{"code1", "code3", "code2"}));
        field.setComponent(createRequestComboBox(field));

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("null", ((RequestComboBox)field.getComponent()).getSelectedValue("refCode"));
    }


    @Test
    public void initComboWithoutStringDefaultValue() {
        Field field = new Field();
        field.setType("string");
        field.setHandlerId("loadComboHandler");

        serverFixture.mockServerResult(buildSimpleResult("refCode",
                                                         "refLabel",
                                                         new String[]{"code1", "code3", "code2"}));
        field.setComponent(createRequestComboBox(field));

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("null", ((RequestComboBox)field.getComponent()).getSelectedValue("refCode"));
    }


    @Test
    public void initComboWithNumberDefaultValue() {
        Field field = new Field();
        field.setType("integer");
        field.setHandlerId("loadComboHandler");
        field.setDefaultValue("15");

        serverFixture.mockServerResult(buildSimpleResult("refCode",
                                                         "refLabel",
                                                         new String[]{"1", "15", "2"}));
        field.setComponent(createRequestComboBox(field));

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("15", ((RequestComboBox)field.getComponent()).getSelectedValue("refCode"));
    }


    @Test
    public void initComboWithUnknownNumberDefaultValue() {
        Field field = new Field();
        field.setType("integer");
        field.setHandlerId("loadComboHandler");
        field.setDefaultValue("7");

        serverFixture.mockServerResult(buildSimpleResult("refCode",
                                                         "refLabel",
                                                         new String[]{"1", "15", "2"}));
        field.setComponent(createRequestComboBox(field));

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("null", ((RequestComboBox)field.getComponent()).getSelectedValue("refCode"));
    }


    @Test
    public void initComboWithoutNumberDefaultValue() {
        Field field = new Field();
        field.setType("integer");
        field.setHandlerId("loadComboHandler");

        serverFixture.mockServerResult(buildSimpleResult("refCode",
                                                         "refLabel",
                                                         new String[]{"1", "15", "2"}));
        field.setComponent(createRequestComboBox(field));

        defaultGuiCustomizer.initDefaultFieldValue(field);
        assertEquals("null", ((RequestComboBox)field.getComponent()).getSelectedValue("refCode"));
    }


    private static Result buildSimpleResult(String refCodeName,
                                            String refLabelName,
                                            String[] values
    ) {
        String[][] rows = new String[values.length][2];
        for (int i = 0; i < values.length; i++) {
            rows[i][0] = values[i];
            rows[i][1] = values[i];
        }
        return buildResult(new String[]{refCodeName, refLabelName}, rows);
    }


    private static Result buildResult(String[] columnNames, String[][] rows) {
        Result loadResult = new Result();

        for (String[] row : rows) {
            Map<String, String> fields = new HashMap<String, String>();

            for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex++) {
                String columnName = columnNames[columnIndex];
                fields.put(columnName, row[columnIndex]);
            }

            loadResult.addRow(new Row(fields));
        }
        return loadResult;
    }
}
