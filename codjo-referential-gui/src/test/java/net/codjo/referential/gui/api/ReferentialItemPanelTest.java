package net.codjo.referential.gui.api;

import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static net.codjo.test.common.matcher.JUnitMatchers.allOf;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.junit.Test;
import org.uispec4j.Panel;

public class ReferentialItemPanelTest {
    private Set<Field> fields = Collections.singleton(new Field("code", "un code", "string"));
    private ReferentialItemPanel itemPanel = new ReferentialItemPanel();
    private DetailDataSource dataSource = new DetailDataSource(new DefaultGuiContext());


    @Test
    public void test_buildPanel() throws Exception {
        itemPanel.buildPanel(fields);

        Panel panel = new Panel(itemPanel);
        assertThat(panel.getTextBox("code"), is(notNullValue()));
    }


    @Test
    public void test_buildPanel_audit() throws Exception {
        itemPanel.buildPanel(Collections.singleton(createAuditField()));

        Panel panel = new Panel(itemPanel);
        assertThat(panel.getTextBox("updateBy"), is(notNullValue()));
    }


    @Test
    public void test_buildPanel_customization() throws Exception {
        itemPanel.buildPanel(fields, new GuiCustomizerMock() {

            @Override
            public JComponent createGui(Field field) {
                JTable gui = new JTable();
                field.setComponent(gui);
                return gui;
            }
        });
        Panel panel = new Panel(itemPanel);
        assertThat(panel.getTable(), is(notNullValue()));
    }


    @Test(expected = IllegalStateException.class)
    public void test_buildPanel_doNotSetComponent() throws Exception {
        itemPanel.buildPanel(fields, new GuiCustomizerMock() {

            @Override
            public JComponent createGui(Field field) {
                return new JTextField();
            }
        });
    }


    @Test
    public void test_declare() throws Exception {
        itemPanel.buildPanel(fields);
        itemPanel.declareFields(dataSource, fields);

        Set<String> fieldNames = dataSource.getDeclaredFields().keySet();
        assertThat(fieldNames.toString(), equalTo("[code]"));
    }


    @Test
    public void test_declare_autogenerated_field() throws Exception {
        fields = new HashSet<Field>(fields);
        Field autoGeneratedField =  new Field("autogenerated", "autogenere", "string");
        autoGeneratedField.setGenerated(true);
        fields.add(autoGeneratedField);

        itemPanel.buildPanel(fields);
        itemPanel.declareFields(dataSource, fields);

        Set<String> fieldNames = dataSource.getDeclaredFields().keySet();
        assertThat(fieldNames.size(), equalTo(1));
        assertThat(fieldNames, allOf(hasItems("code")));
    }


    @Test
    public void test_declare_audit() throws Exception {
        fields = Collections.singleton(createAuditField());

        itemPanel.buildPanel(fields);
        itemPanel.declareFields(dataSource, fields);

        Set<String> fieldNames = dataSource.getDeclaredFields().keySet();
        assertThat(fieldNames.size(), equalTo(5));
        assertThat(fieldNames, allOf(hasItems("creationDatetime"),
                                     hasItems("creationBy"),
                                     hasItem("updateBy"),
                                     hasItem("comment"),
                                     hasItem("updateDatetime")));
    }


    @Test
    public void test_declare_customize() throws Exception {
        itemPanel.buildPanel(fields, new GuiCustomizerMock() {

            @Override
            public JComponent createGui(Field field) {
                field.setComponent(new JTextField());
                return field.getComponent();
            }
        });
        itemPanel.declareFields(dataSource, fields, new GuiCustomizerMock() {

            @Override
            public void declareField(DetailDataSource dataSource, Field field) {
                dataSource.declare("customized");
            }
        });

        Set<String> fieldNames = dataSource.getDeclaredFields().keySet();
        assertThat(fieldNames.toString(), equalTo("[customized]"));
    }


    @Test
    public void test_declare_customizeNoDeclare() throws Exception {
        itemPanel.buildPanel(fields, new GuiCustomizerMock() {

            @Override
            public JComponent createGui(Field field) {
                field.setComponent(new JTextField());
                return field.getComponent();
            }


            public void initDefaultFieldValue(Field field) {
            }
        });
        itemPanel.declareFields(dataSource, fields, new GuiCustomizerMock());

        Set<String> fieldNames = dataSource.getDeclaredFields().keySet();
        assertThat(fieldNames.toString(), equalTo("[]"));
    }


    private Field createAuditField() {
        return new Field("audit", "une audit", "net.codjo.Audit");
    }


    private static class GuiCustomizerMock implements GuiCustomizer {
        public boolean handle(Field field) {
            return true;
        }


        public JComponent createGui(Field field) {
            return null;
        }


        public void declareField(DetailDataSource dataSource, Field field) {

        }


        public void initDefaultFieldValue(Field field) {
            
        }
    }
}