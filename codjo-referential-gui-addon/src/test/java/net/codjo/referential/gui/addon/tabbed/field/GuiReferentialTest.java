package net.codjo.referential.gui.addon.tabbed.field;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.util.List;
import org.junit.Test;

public class GuiReferentialTest {

    @Test
    public void test_getGuiFields() throws Exception {
        GuiReferential guiReferential = new GuiReferential();
        GuiFieldGroup group1 = new GuiFieldGroup("guiFieldGroup1");
        group1.addGuiField(new GuiField("field1"));
        group1.addGuiField(new GuiField("field2"));
        group1.addGuiField(new GuiField("field3"));
        guiReferential.add(group1);
        GuiFieldGroup group2 = new GuiFieldGroup("guiFieldGroup2");
        group2.addGuiField(new GuiField("field1"));
        group2.addGuiField(new GuiField("field4"));
        guiReferential.add(group2);
        List<GuiField> guiFields = guiReferential.findGuiFields("field1");
        assertThat(guiFields.size(), equalTo(2));
        guiFields = guiReferential.findGuiFields("field2");
        assertThat(guiFields.size(), equalTo(1));
    }


    @Test
    public void test_getGuiFields_emptyReferential() throws Exception {
        GuiReferential guiReferential = new GuiReferential();
        final List<GuiField> list = guiReferential.findGuiFields("guiField");
        assertThat(list, not(nullValue()));
        assertThat(list.size(), equalTo(0));
    }
}
