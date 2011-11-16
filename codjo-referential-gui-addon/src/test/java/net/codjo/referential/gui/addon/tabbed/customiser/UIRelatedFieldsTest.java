package net.codjo.referential.gui.addon.tabbed.customiser;
import java.awt.Component;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import net.codjo.agent.UserId;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.Sender;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import net.codjo.security.common.api.User;
import org.uispec4j.ComboBox;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecAdapter;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.finder.ComponentMatcher;

import static java.util.Arrays.asList;

public class UIRelatedFieldsTest extends UISpecTestCase {
    private final JFrame mainFrame = new JFrame();
    private final MadServerFixture serverFixture = new MadServerFixture();
    private GuiReferential guiReferential;
    private Referential referential;
    private GuiFieldGroup guiFieldGroup;
    private Field numericField;
    private GuiField guiNumField;
    private RelatedFieldsGuiCustomiser customizer;
    RelatedGuiField slave1 = createRelatedField("slave1", numericField);
    RelatedGuiField slave2 = createRelatedField("slave2", numericField);


    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.setAdapter(new UISpecAdapter() {
            public Window getMainWindow() {
                return new Window(mainFrame);
            }
        });
        numericField = new Field("numField", "numField", "java.lang.Integer");
        initReferential();
        initGuiReferential();
        serverFixture.doSetUp();
        customizer = new RelatedFieldsGuiCustomiser(guiFieldGroup,
                                                    referential,
                                                    guiReferential);
    }


    @Override
    protected void tearDown() throws Exception {
        serverFixture.doTearDown();
    }

    ///// Fonctions de "mock" /////


    private void initReferential() {
        referential = new Referential();
        referential.addField(numericField);
    }


    private void initGuiReferential() {
        guiReferential = new GuiReferential();
        guiFieldGroup = new GuiFieldGroup("onglet");
        guiNumField = new GuiField("numField", "handlerId", null);
        guiNumField.setRootDisplayed(true);
        guiNumField.addRelatedGuiFields(slave1);
        guiNumField.addRelatedGuiFields(slave2);
        guiFieldGroup.addGuiField(guiNumField);
        guiReferential.add(guiFieldGroup);
    }


    private RelatedGuiField createRelatedField(final String relatedFieldName, Field masterField) {
        final RelatedGuiField relatedGuiField = new RelatedGuiField(relatedFieldName, relatedFieldName);
        relatedGuiField.setMasterField(masterField);
        return relatedGuiField;
    }


    private static Result mockComboResult(final int nbRows, final Integer... nullIndexes) {
        final Result result = new Result();
        final List<Integer> nullIndexList = asList(nullIndexes);
        Row row;
        for (int i = 0; i < nbRows; i++) {
            row = new Row();
            row.addField("refCode", String.valueOf(i));
            row.addField("refLabel", "label" + i);
            row.addField("slave1", String.valueOf(i));
            if (nullIndexList.contains(i)) {
                row.addField("slave2", "null");
            }
            else {
                row.addField("slave2", String.valueOf(100 + i));
            }
            result.addRow(row);
        }
        return result;
    }


    private static Result mockDatasourceResult(final int refCode) {
        final Result result = new Result();
        Row row = new Row();
        row.addField("numField", String.valueOf(refCode));
        return result;
    }


    private static DetailDataSource mockDetailDatasource(final MadServerFixture madServerFixture) {
        DefaultGuiContext guiContext = new DefaultGuiContext();
        guiContext.setSender(new Sender(madServerFixture.getOperations()));
        guiContext.setUser(new User() {
            public UserId getId() {
                return UserId.createId("test", "test");
            }


            public boolean isAllowedTo(String function) {
                return true;
            }


            public boolean isInRole(String roleId) {
                return true;
            }
        });
        return new DetailDataSource(guiContext);
    }


    private RequestComboBox initGuiTest(final Integer... nullIndexes) throws RequestException {
        serverFixture.mockServerResult(mockComboResult(3, nullIndexes));
        DetailDataSource detailDataSource = mockDetailDatasource(serverFixture);
        final JComponent box = customizer.createGui(numericField);
        detailDataSource.setLoadResult(mockDatasourceResult(1));
        RequestComboBox comboBox = (RequestComboBox)numericField.getComponent();
        comboBox.load();
        customizer.declareField(detailDataSource, numericField);
        mainFrame.add(box);
        return comboBox;
    }

    ///// Tests unitaires /////


    public void test_referentials() throws Exception {
        assertEquals(guiFieldGroup, guiReferential.getGuiFieldGroup("onglet"));
        assertEquals(guiNumField, guiFieldGroup.getGuiField("numField"));
        assertEquals("handlerId", guiNumField.getHandlerId());
    }


    public void test_numericMasterField() throws Exception {
        RequestComboBox comboBox = initGuiTest();

        Window mainWindow = this.getMainWindow();
        ComboBox numComboBox = mainWindow.getComboBox("numField");
        assertFalse(numComboBox == null);
        TextBox slave1TextField = mainWindow.getTextBox(new NameComponentFinder("slave1"));
        assertFalse(slave1TextField == null);
        TextBox slave2TextField = mainWindow.getTextBox(new NameComponentFinder("slave2"));
        assertFalse(slave2TextField == null);
        assertTrue(numComboBox.contains(new String[]{"label0", "label1", "label2"}));
        numComboBox.select("label1");
        assertEquals("1", slave1TextField.getText());
        assertEquals("101", slave2TextField.getText());
        numComboBox.select("label2");
        assertEquals("2", slave1TextField.getText());
        assertEquals("102", slave2TextField.getText());
        assertEquals("null", comboBox.getItemAt(0));
        comboBox.setSelectedIndex(0);
        assertEquals("", slave1TextField.getText());
        assertEquals("", slave2TextField.getText());
    }


    public void test_numericSlaveField() throws Exception {

        slave1.setType("java.lang.Integer");
        slave2.setType("java.lang.Integer");
        initGuiTest();

        Window mainWindow = this.getMainWindow();
        ComboBox masterComboBox = mainWindow.getComboBox("numField");
        TextBox slave1TextBox = mainWindow.getTextBox(new NameComponentFinder("slave1"));
        TextBox slave2TextBox = mainWindow.getTextBox(new NameComponentFinder("slave2"));

        masterComboBox.select("label1");
        assertEquals("1", slave1TextBox.getText());
        assertEquals("101", slave2TextBox.getText());
    }


    public void test_numericSlaves_nullValues() throws Exception {
        slave1.setType("java.lang.Integer");
        slave2.setType("java.lang.Integer");
        initGuiTest(2);

        Window mainWindow = this.getMainWindow();
        ComboBox masterComboBox = mainWindow.getComboBox("numField");
        TextBox slave1TextBox = mainWindow.getTextBox(new NameComponentFinder("slave1"));
        TextBox slave2TextBox = mainWindow.getTextBox(new NameComponentFinder("slave2"));

        masterComboBox.select("label1");
        assertEquals("1", slave1TextBox.getText());
        assertEquals("101", slave2TextBox.getText());
        masterComboBox.select("label2");
        assertEquals("2", slave1TextBox.getText());
        assertEquals("", slave2TextBox.getText());
        assertTrue(slave2TextBox.isVisible());
    }

    ///// Classes internes /////

    private static class NameComponentFinder implements ComponentMatcher {
        private final String componentName;


        NameComponentFinder(String componentName) {
            this.componentName = componentName;
        }


        public boolean matches(Component component) {
            //noinspection SimplifiableIfStatement
            if (component == null) {
                return false;
            }
            return componentName.equals(component.getName());
        }
    }
}
