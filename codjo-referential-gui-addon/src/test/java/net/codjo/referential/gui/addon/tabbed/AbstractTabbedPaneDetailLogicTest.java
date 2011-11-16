package net.codjo.referential.gui.addon.tabbed;

import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.AbstractDetailGui;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.addon.TestUtil;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.api.Referential;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.TabGroup;
import org.uispec4j.TextBox;
import org.uispec4j.Window;

public class AbstractTabbedPaneDetailLogicTest {
    private DetailDataSource dataSource = new DetailDataSource(new DefaultGuiContext());


    @Before
    public void setUp() {

    }


    @Test
    public void test_getGui() throws Exception {
        AbstractTabbedPaneDetailLogic logic = new MyTabbedPaneDetailLogic(dataSource,
                                                                          new Referential(),
                                                                          new GuiReferential());
        assertThat(logic.getGui(), is(notNullValue()));
    }


    @Test
    public void test_getGui_enabledFields() throws Exception {
        Referential referential = createReferential("field1", "field2", "field3");
        GuiField guiField1 = new GuiField("field1");
        GuiField guiField2 = new GuiField("field2");
        GuiField guiField3 = new GuiField("field3");
        guiField3.setEditable(false);
        GuiReferential guiReferential = new GuiReferential();
        guiReferential.add(createGroup("group1", guiField1, guiField2, guiField3));
        AbstractTabbedPaneDetailLogic tabbedPaneDetailLogic = new MyTabbedPaneDetailLogic(dataSource,
                                                                                          referential,
                                                                                          guiReferential);
        Window window = new Window(tabbedPaneDetailLogic.getGui());
        TextBox textBox3 = window.getInputTextBox("field3");
        assertThat(textBox3.isEnabled().isTrue(), equalTo(true));
        assertThat(textBox3.isEditable().isTrue(), equalTo(false));
        TextBox textBox2 = window.getInputTextBox("field2");
        assertThat(textBox2.isEnabled().isTrue(), equalTo(true));
        assertThat(textBox2.isEditable().isTrue(), equalTo(true));
    }


    @Test
    public void test_empty() throws Exception {
        Referential referential = new Referential();
        referential.setTitle("my title");
        AbstractTabbedPaneDetailLogic logic = new MyTabbedPaneDetailLogic(dataSource,
                                                                          referential, new GuiReferential());
        Window window = new Window(logic.getGui());

        assertThat(window.getTitle(), equalTo("my title"));
        assertThat(dataSource.getDeclaredFields().size(), equalTo(0));
    }


    @Test
    public void test_oneGroup() throws Exception {
        GuiReferential guiReferential = new GuiReferential();
        guiReferential.add(createGroup("onglet", new GuiField("datasourceCode")));

        Referential referential = createReferential("datasourceCode");

        AbstractTabbedPaneDetailLogic logic = new MyTabbedPaneDetailLogic(dataSource,
                                                                          referential,
                                                                          guiReferential);
        Window window = new Window(logic.getGui());

        assertThat(dataSource.getDeclaredFields().keySet().toString(), equalTo("[datasourceCode]"));
        assertThat(window.getTextBox("dataSourceCode"), is(notNullValue()));
    }


    @Test(expected = IllegalArgumentException.class)
    public void test_guiFieldWithoutMatching() throws Exception {
        GuiReferential guiReferential = new GuiReferential();
        guiReferential.add(createGroup("onglet", new GuiField("fieldWithoutMatching")));

        new MyTabbedPaneDetailLogic(dataSource, createReferential(), guiReferential).getGui();
    }


    @Test
    public void test_multiGroup() throws Exception {
        Referential referential = createReferential("code1", "code2", "code3", "unused4", "unused5");

        GuiReferential guiReferential = new GuiReferential();
        guiReferential.add(createGroup("onglet 1", new GuiField("code1"), new GuiField("code2")));
        guiReferential.add(createGroup("onglet 2", new GuiField("code3")));

        AbstractTabbedPaneDetailLogic logic = new MyTabbedPaneDetailLogic(dataSource,
                                                                          referential,
                                                                          guiReferential);
        Window window = new Window(logic.getGui());

        TabGroup tabGroup = window.getTabGroup();
        assertThat(tabGroup.tabNamesEquals(new String[]{"onglet 1", "onglet 2"}).isTrue(), is(true));

        assertThat(tabGroup.selectedTabEquals("onglet 1").isTrue(), is(true));
        assertThat(tabGroup.getSelectedTab().getTextBox("code1"), is(notNullValue()));
        assertThat(tabGroup.getSelectedTab().getTextBox("code2"), is(notNullValue()));

        tabGroup.selectTab("onglet 2");
        assertThat(tabGroup.selectedTabEquals("onglet 2").isTrue(), is(true));
        assertThat(tabGroup.getSelectedTab().getTextBox("code3"), is(notNullValue()));
    }


    @Test
    public void test_switchToUpdateMode() throws Exception {
        Referential referential = createReferential("datasourceCode");
        referential.getFieldList().get("datasourceCode").setPrimaryKey(true);

        GuiReferential guiReferential = new GuiReferential();
        guiReferential.add(createGroup("onglet", new GuiField("datasourceCode")));

        AbstractTabbedPaneDetailLogic logic = new MyTabbedPaneDetailLogic(dataSource,
                                                                          referential,
                                                                          guiReferential);
        AbstractDetailGui detailGui = logic.getGui();
        detailGui.switchToUpdateMode();

        Window window = new Window(detailGui);
        TextBox textBox = window.getTextBox("dataSourceCode");
        assertThat(textBox, is(notNullValue()));
        assertThat(textBox.isEnabled().isTrue(), is(true));
    }


    private static GuiFieldGroup createGroup(String title, GuiField... guiFieldNames) {
        GuiFieldGroup group = new GuiFieldGroup(title);
        for (GuiField guiFieldName : guiFieldNames) {
            group.addGuiField(guiFieldName);
        }
        return group;
    }


    private static Referential createReferential(String... fieldNames) {
        Referential referential = new Referential();
        for (String fieldName : fieldNames) {
            referential.addField(TestUtil.field(fieldName));
        }
        return referential;
    }


    private class MyTabbedPaneDetailLogic extends AbstractTabbedPaneDetailLogic<TabbedPaneDetailGui> {
        private GuiReferential guiReferential;


        private MyTabbedPaneDetailLogic(DetailDataSource dataSource,
                                        Referential referential,
                                        GuiReferential guiReferential)
              throws RequestException, FileNotFoundException {
            super(dataSource, referential);
            this.guiReferential = guiReferential;
        }


        @Override
        protected GuiReferential createGuiConfiguration(Referential referential) {
            return guiReferential;
        }


        @Override
        protected TabbedPaneDetailGui createGui() throws Exception {
            return new TabbedPaneDetailGui(getDetailDataSource(), getReferential(), getGuiReferential());
        }


        @Override
        protected String retrieveEntityName() {
            return "MyEntity";
        }
    }
}