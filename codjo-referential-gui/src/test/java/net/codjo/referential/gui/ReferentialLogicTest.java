package net.codjo.referential.gui;

import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.Sender;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.referential.gui.api.EmptyListGuiCustomizer;
import net.codjo.referential.gui.api.EmptyReferentialFrameCustomizer;
import net.codjo.referential.gui.api.EmptyTreeGuiCustomizer;
import net.codjo.referential.gui.api.ListGuiCustomizer;
import net.codjo.referential.gui.api.ListGuiCustomizerMock;
import net.codjo.referential.gui.api.Referential;
import net.codjo.referential.gui.api.ReferentialFrameCustomizer;
import net.codjo.referential.gui.api.ReferentialFrameCustomizerMock;
import net.codjo.referential.gui.api.TreeGuiCustomizer;
import net.codjo.referential.gui.api.TreeGuiCustomizerMock;
import net.codjo.security.common.api.UserMock;
import net.codjo.test.common.GuiUtil;
import net.codjo.test.common.LogString;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.awt.Dimension;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import junit.framework.TestCase;

public class ReferentialLogicTest extends TestCase {
    private ReferentialLogic referentialLogic;
    private MadServerFixture server = new MadServerFixture();
    private LogString log = new LogString();


    @Override
    protected void setUp() throws Exception {
        server.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        server.doTearDown();
    }


    public void test_loadReferentialList() throws Exception {
        mockEmptyFamilyAssoResult(server);
        referentialLogic = createReferentialLogic("/conf/basicReferential.xml");

        Map<String, Set<Referential>> referentialFamilyList = referentialLogic.getReferentialFamilyList();
        assertEquals(1, referentialFamilyList.size());
        Iterator<Map.Entry<String, Set<Referential>>> iterator = referentialFamilyList.entrySet().iterator();

        Map.Entry<String, Set<Referential>> family = iterator.next();
        assertEquals(ReferentialLogic.DEFAULT_FAMILY, family.getKey());
        assertFamilyReferentialList(family.getValue(),
                                    "Devise", "Fréquence de valorisation", "Pays", "VL d'exécution");
    }


    public void test_loadReferentialFamilyList() throws Exception {
        mockFamilyAssoResults(server);
        referentialLogic = createReferentialLogic("/conf/basicReferential.xml");

        Map<String, Set<Referential>> referentialFamilyList = referentialLogic.getReferentialFamilyList();
        assertEquals(4, referentialFamilyList.size());

        Iterator<Map.Entry<String, Set<Referential>>> iterator = referentialFamilyList.entrySet().iterator();

        Map.Entry<String, Set<Referential>> entry = iterator.next();
        assertEquals("Finance", entry.getKey());
        assertFamilyReferentialList(entry.getValue(), "Fréquence de valorisation");

        entry = iterator.next();
        assertEquals("Géographie", entry.getKey());
        assertFamilyReferentialList(entry.getValue(), "Devise", "Pays");

        entry = iterator.next();
        assertEquals("Monétaire", entry.getKey());
        assertFamilyReferentialList(entry.getValue(), "Devise");

        entry = iterator.next();
        assertEquals("zzz-Autres", entry.getKey());
        assertFamilyReferentialList(entry.getValue(), "VL d'exécution");
    }


    public void test_customizer() throws Exception {
        mockEmptyFamilyAssoResult(server);
        referentialLogic = createReferentialLogic("/conf/basicReferential.xml",
                                                  new ListGuiCustomizerMock(log),
                                                  new TreeGuiCustomizerMock(log),
                                                  new ReferentialFrameCustomizerMock(log));

        ReferentialGui gui = referentialLogic.getGui();
        DefaultReferentialListGui listGui = referentialLogic.getListGui();

        JFrame frame = new JFrame();
        JDesktopPane jDesktopPane = new JDesktopPane();
        frame.setContentPane(jDesktopPane);
        jDesktopPane.add(gui);
        gui.setVisible(true);
        log.assertContent("init(tree<Gestion des référentiels>), " 
                + "handleFrameOpen(context<net.codjo.referential.gui.ReferentialGuiContext>, listGui<>)");

        assertEquals("[root, Devise] [root, Fréquence de valorisation] [root, Pays] [root, VL d'exécution]",
                     GuiUtil.uiDisplayedContent(gui.getReferentialTree()));

        mockePreference("CurrencyList", new Column("currencyCode", "code devise"));
        gui.getReferentialTree().setSelectionRow(0);
        assertEquals("code devise", listGui.getTable().getColumnName(0));

        mockePreference("ValFrequencyList", new Column("refCode", "le code"));
        gui.getReferentialTree().setSelectionRow(1);
        assertEquals("le code", listGui.getTable().getColumnName(0));

        log.assertContent("init(tree<Gestion des référentiels>), "
                + "handleFrameOpen(context<net.codjo.referential.gui.ReferentialGuiContext>, listGui<>), "
                + "handleListClose(list<>), "
                + "handleListOpen(ref<Devise>, list<Devise>), handleListClose(list<Devise>), "
                + "handleListOpen(ref<Fréquence de valorisation>, list<Fréquence de valorisation>)");

        gui.doDefaultCloseAction();
        log.assertContent("init(tree<Gestion des référentiels>), "
                + "handleFrameOpen(context<net.codjo.referential.gui.ReferentialGuiContext>, listGui<>), "
                + "handleListClose(list<>), "
                + "handleListOpen(ref<Devise>, list<Devise>), handleListClose(list<Devise>), "
                + "handleListOpen(ref<Fréquence de valorisation>, list<Fréquence de valorisation>), "
                + "handleFrameClose(context<net.codjo.referential.gui.ReferentialGuiContext>, listGui<Fréquence de valorisation>)");
    }


    private void assertFamilyReferentialList(Set<Referential> referentialList,
                                             String... expectedReferentialList) {
        assertEquals(expectedReferentialList.length, referentialList.size());
        int index = 0;
        for (Referential referential : referentialList) {
            assertTrue("'" + referential + "' appartient a la liste",
                       expectedReferentialList[index++].equals(referential.toString()));
        }
    }


    public static void mockEmptyFamilyAssoResult(MadServerFixture fixture) {
        Result result = MadServerFixture.createResult(
              new String[]{"familyLabel", "referentialId"},
              new String[0][0]);
        fixture.queueServerResult(result);
    }


    public static void mockFamilyAssoResults(MadServerFixture fixture) {
        Result result = MadServerFixture.createResult(
              new String[]{"familyLabel", "referentialId"},
              new String[][]{
                    {"Géographie", "CurrencyList"},
                    {"Géographie", "CountryList"},
                    {"Monétaire", "CurrencyList"},
                    {"Finance", "ValFrequencyList"},
              });

        fixture.queueServerResult(result);
    }


    private void mockePreference(String preferenceId, Column displayedColumn) {
        PreferenceFactory.initFactory();
        Preference preference = new Preference(preferenceId);
        preference.setColumns(Collections.singletonList(displayedColumn));
        PreferenceFactory.addPreference(preference);
    }


    private DefaultGuiContext createGuiContext() {
        ReferentialGuiContext guiContext = new ReferentialGuiContext();
        guiContext.setSender(new Sender(server.getOperations()));
        guiContext.setUser(new UserMock().mockIsAllowedTo(true));
        return guiContext;
    }


    private ReferentialLogic createReferentialLogic(String name) throws Exception {
        return createReferentialLogic(name, new EmptyListGuiCustomizer(), new EmptyTreeGuiCustomizer(),
                                      new EmptyReferentialFrameCustomizer());
    }


    private ReferentialLogic createReferentialLogic(String name,
                                                    ListGuiCustomizer listGuiCustomizer,
                                                    TreeGuiCustomizer treeGuiCustomizer,
                                                    ReferentialFrameCustomizer referentialFrameCustomizer)
          throws Exception {
        return new ReferentialLogic(createGuiContext(),
                                    XmlReferentialMapping.loadFrom(name),
                                    listGuiCustomizer,
                                    treeGuiCustomizer,
                                    referentialFrameCustomizer,
                                    false,
                                    new Dimension(900, 600));
    }
}
