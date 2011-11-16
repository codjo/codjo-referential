package net.codjo.referential.gui.addon.tabbed.util;

import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.referential.gui.addon.TestUtil;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExternalLinkManagerTest {
    private Field portfolioField = TestUtil.field("portfolioId");
    private Referential referential = new Referential();
    private GuiReferential guiReferential = new GuiReferential();
    private DetailDataSource detailDataSource = new DetailDataSource(new DefaultGuiContext());
    private MadServerFixture server = new MadServerFixture();
    private static final String SELECT_HANDLER_ID = "selectPtf";


    @Before
    public void setUpFixture() throws Exception {
        server.doSetUp();
    }


    @Before
    public void initializeConfiguration() {
        referential.addField(portfolioField);
        guiReferential.add(new GuiFieldGroup("first", new GuiField("portfolioId", SELECT_HANDLER_ID, null)));
    }


    @After
    public void tearDownFixture() throws Exception {
        server.doTearDown();
    }


    @Test
    public void test_init() throws Exception {
        ExternalLinkManager.getInstance(referential, guiReferential);
        assertThat(portfolioField.getHandlerId(), equalTo(SELECT_HANDLER_ID));
    }


    @Test
    public void test_connect() throws Exception {
        ExternalLinkManager linkManager = ExternalLinkManager.getInstance(referential, guiReferential);

        RequestComboBox portfolioFieldGui = mockGuiConstruction();

        linkManager.connect(detailDataSource);

        server.mockServerResult(TestUtil.result(TestUtil.row("refCode=69", "refLabel=le PTF"),
                                                TestUtil.row("refCode=68", "refLabel=Etudiant")));
        detailDataSource.setLoadResult(TestUtil.result(TestUtil.row("portfolioId=69")));

        assertThat(portfolioFieldGui.getModel().getSize(), equalTo(3));
        assertThat(portfolioFieldGui.getSelectedValue("refCode"), equalTo("69"));
        assertThat(portfolioFieldGui.getSelectedValue("refLabel"), equalTo("le PTF"));
    }


    @Test
    public void test_connect_withSelector() throws Exception {
        referential.addField(TestUtil.field("beginDate"));
        guiReferential.clearGroups();
        guiReferential
              .add(new GuiFieldGroup("first", new GuiField("portfolioId", SELECT_HANDLER_ID, "beginDate")));

        ExternalLinkManager linkManager = ExternalLinkManager.getInstance(referential, guiReferential);

        mockGuiConstruction();

        linkManager.connect(detailDataSource);

        server.mockServerResult(TestUtil.result(TestUtil.row("refCode=69", "refLabel=le PTF"),
                                                TestUtil.row("refCode=68", "refLabel=Etudiant")));
        detailDataSource.setLoadResult(TestUtil.result(TestUtil.row("portfolioId=69", "beginDate=20080101")));

        server.assertSentRequests("<requests>"
                                  + "  <select request_id='1'>"
                                  + "    <id>" + SELECT_HANDLER_ID + "</id>"
                                  + "    <selector>"
                                  + "      <field name='beginDate'>"
                                  + "         20080101"
                                  + "      </field>"
                                  + "    </selector>"
                                  + "    <attributes>"
                                  + "      <name>refLabel</name>"
                                  + "      <name>refCode</name>"
                                  + "    </attributes>"
                                  + "    <page num='1' rows='" + Integer.MAX_VALUE + "'/>"
                                  + "  </select>"
                                  + "</requests>");
    }


    private RequestComboBox mockGuiConstruction() {
        RequestComboBox comboBox = new RequestComboBox();
        portfolioField.setComponent(comboBox);
        comboBox.initRequestComboBox("refCode", "refLabel", true);
        comboBox.getDataSource().setColumns(new String[]{"refCode", "refLabel"});
        comboBox.getDataSource().setLoadFactoryId(SELECT_HANDLER_ID);
        detailDataSource.declare(portfolioField.getName(), comboBox);
        return comboBox;
    }
}
