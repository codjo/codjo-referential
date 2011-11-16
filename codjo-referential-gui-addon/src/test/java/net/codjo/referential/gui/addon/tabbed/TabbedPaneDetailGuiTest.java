package net.codjo.referential.gui.addon.tabbed;

import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import javax.swing.JComboBox;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Window;

public class TabbedPaneDetailGuiTest {
    private static final String FIELD_NAME = "currencyCode";
    private DetailDataSource dataSource = new DetailDataSource(new DefaultGuiContext());
    private final MadServerFixture serverFixture = new MadServerFixture();
    private Field field;
    private GuiField guiField;
    private Referential referential;
    private GuiReferential guiReferential;


    @Before
    public void before() throws Exception {
        field = new Field(FIELD_NAME, "code devise", "string");
        referential = new Referential();
        referential.addField(field);

        guiField = new GuiField(FIELD_NAME);
        guiReferential = new GuiReferential();
        guiReferential.add(new GuiFieldGroup("main tab", guiField));

        serverFixture.doSetUp();
    }


    public void after() throws Exception {
        serverFixture.doTearDown();
    }


    @Test
    public void test_combobox() throws Exception {
        serverFixture.mockServerResult(new String[]{"refCode", "refLabel"}, new String[][]{{"EUR", "euro"}, {"USD", "dollar"}});
        field.setHandlerId("selectAllCurrency");
        guiField.setHandlerId("selectAllCurrency");

        createDetailGui();

        assertThat(dataSource.getDeclaredFields().get(FIELD_NAME).getGuiComponent(), is(JComboBox.class));
    }


    @Test
    public void test_combobox_withRelatedFields() throws Exception {
        field.setHandlerId("selectAllCurrency");
        guiField.setHandlerId("selectAllCurrency");

        guiField.addRelatedGuiFields(new RelatedGuiField("refLabel", "le label"));

        Window window = createDetailGui();

        assertThat(dataSource.getDeclaredFields().get(FIELD_NAME).getGuiComponent(), is(JComboBox.class));
        assertThat(window.getTextBox("le label").getText(), is(notNullValue()));
    }


    @Test
    public void test_combobox_withRelatedFields_withType() throws Exception {
        referential.addField(new Field("refLabel", "le label", "java.util.String"));

        field.setHandlerId("selectAllCurrency");
        guiField.setHandlerId("selectAllCurrency");

        guiField.addRelatedGuiFields(new RelatedGuiField("refLabel", "le label"));

        Window window = createDetailGui();

        assertThat(dataSource.getDeclaredFields().get(FIELD_NAME).getGuiComponent(), is(JComboBox.class));
        assertThat(window.getTextBox("le label").getText(), is(notNullValue()));
    }


    private Window createDetailGui() {
        TabbedPaneDetailGui detailGui = new TabbedPaneDetailGui(dataSource, referential, guiReferential);
        return new Window(detailGui);
    }
}
