package net.codjo.referential.gui;

import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.Sender;
import net.codjo.mad.gui.request.GuiLogic;
import net.codjo.referential.gui.api.EmptyListGuiCustomizer;
import net.codjo.referential.gui.api.EmptyReferentialFrameCustomizer;
import net.codjo.referential.gui.api.EmptyTreeGuiCustomizer;
import net.codjo.security.common.api.UserMock;
import java.awt.Dimension;
import org.uispec4j.Panel;
import org.uispec4j.Tree;
import org.uispec4j.UISpecTestCase;

public class ReferentialGuiTest extends UISpecTestCase {
    MadServerFixture madServerFixture = new MadServerFixture();
    private GuiLogic<ReferentialGui> referentialLogic;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        madServerFixture.doSetUp();
        mockFamilyList(madServerFixture);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void test_hasSetPreferredSize() throws Exception {
        final Dimension preferredSize = new Dimension(1000, 600);
        referentialLogic = new ReferentialLogic(createGuiContext(madServerFixture),
                                                XmlReferentialMapping.loadFrom("/conf/basicReferential.xml"),
                                                new EmptyListGuiCustomizer(),
                                                new EmptyTreeGuiCustomizer(),
                                                new EmptyReferentialFrameCustomizer(),
                                                true,
                                                preferredSize);
        assertEquals(preferredSize, referentialLogic.getGui().getPreferredSize());
    }

    public void test_defaultPreferredSize() throws Exception {
        referentialLogic = new ReferentialLogic(createGuiContext(madServerFixture),
                                                XmlReferentialMapping.loadFrom("/conf/basicReferential.xml"),
                                                new EmptyListGuiCustomizer(),
                                                new EmptyTreeGuiCustomizer(),
                                                new EmptyReferentialFrameCustomizer(),
                                                true,
                                                null);
        assertEquals(new Dimension(990, 600), referentialLogic.getGui().getPreferredSize());
    }


    public void test_hasWaitingPanel() throws Exception {
        referentialLogic = new ReferentialLogic(createGuiContext(madServerFixture),
                                                XmlReferentialMapping.loadFrom("/conf/basicReferential.xml"),
                                                new EmptyListGuiCustomizer(),
                                                new EmptyTreeGuiCustomizer(),
                                                new EmptyReferentialFrameCustomizer(),
                                                true,
                                                new Dimension(900, 600));
        Panel mainPanel = new Panel(referentialLogic.getGui());
        Tree tree = mainPanel.getTree();
        assertTrue(referentialLogic.getGui().getRootPane().getGlassPane() instanceof WaitingPanel);
        assertTrue(tree.isVisible());
    }


    public void test_doesNotHaveWaitingPanel() throws Exception {
        referentialLogic = new ReferentialLogic(createGuiContext(madServerFixture),
                                                XmlReferentialMapping.loadFrom("/conf/basicReferential.xml"),
                                                new EmptyListGuiCustomizer(),
                                                new EmptyTreeGuiCustomizer(),
                                                new EmptyReferentialFrameCustomizer(),
                                                false,
                                                new Dimension(900, 600));

        Panel mainPanel = new Panel(referentialLogic.getGui());
        Tree tree = mainPanel.getTree();
        assertFalse(referentialLogic.getGui().getRootPane().getGlassPane() instanceof WaitingPanel);
        assertTrue(tree.isVisible());
    }


    private static void mockFamilyList(MadServerFixture madFixture) {
        String[] names = new String[]{
              "referentialId", "familyLabel"
        };
        String[][] rows = new String[][]{
              {"1", "referential1"},
              {"2", "referential2"},
              {"3", "referential3"},
              {"4", "referential4"}
        };
        Result result = MadServerFixture.createResult(names, rows);
        result.setRequestId("selectAllPmRefFamilyRefAsso");
        madFixture.mockServerResult(result);
    }


    private static GuiContext createGuiContext(MadServerFixture serverFixture) {
        DefaultGuiContext guiContext = new ReferentialGuiContext();
        guiContext.setSender(new Sender(serverFixture.getOperations()));
        guiContext.setUser(new UserMock().mockIsAllowedTo(true));
        return guiContext;
    }
}
