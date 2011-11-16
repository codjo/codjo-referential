package net.codjo.referential.gui.addon.tabbed;

import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.test.common.matcher.JUnitMatchers;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.Test;

public class TabbedPaneParserTest {
    private TabbedPaneParser tabbedPaneParser = new TabbedPaneParser();


    @Test(expected = FileNotFoundException.class)
    public void test_notFindXMLFile() throws Exception {
        tabbedPaneParser.parse(null);
    }


    @Test
    public void test_parse() throws Exception {
        TabbedPaneConfiguration conf = tabbedPaneParser
              .parse(this.getClass().getResourceAsStream("tabbed-config.xml"));

        GuiReferential firstReferential = conf.getReferentials().get(0);

        assertThat(firstReferential.getName(), JUnitMatchers.equalTo("referential"));
        assertThat(firstReferential, JUnitMatchers.equalTo(conf.getReferential("referential")));
        List<GuiFieldGroup> groups = firstReferential.getFieldsGroup();
        assertThat(groups, JUnitMatchers.not(JUnitMatchers.nullValue()));
        assertThat(groups.size(), JUnitMatchers.equalTo(2));

        GuiFieldGroup firstGroup = firstReferential.getFieldsGroup().get(0);
        assertThat(firstGroup.getTitle(), JUnitMatchers.equalTo("les pk"));
        assertThat(firstGroup.getGuiFields().get(0).getName(),
                   JUnitMatchers.equalTo("dataSourceCode"));
        assertThat(firstGroup.getGuiFields().get(0).isEditable(), JUnitMatchers.equalTo(true));
    }


    @Test
    public void test_masterCombox() throws Exception {
        TabbedPaneConfiguration tabbedPaneConfiguration = parseConfigFromXML("tabbed-config-combo.xml");
        GuiFieldGroup guiFieldGroup = tabbedPaneConfiguration.getReferential("referential")
              .getGuiFieldGroup("group1");

        assertThat(guiFieldGroup, JUnitMatchers.not(JUnitMatchers.equalTo(null)));
        GuiField slave = guiFieldGroup.getGuiField("slave");
        assertThat(slave.getMasterName(), JUnitMatchers.equalTo("master"));
    }


    private TabbedPaneConfiguration parseConfigFromXML(String configName)
          throws FileNotFoundException {
        return tabbedPaneParser
              .parse(TabbedPaneParserTest.class.getResourceAsStream(configName));
    }
}
