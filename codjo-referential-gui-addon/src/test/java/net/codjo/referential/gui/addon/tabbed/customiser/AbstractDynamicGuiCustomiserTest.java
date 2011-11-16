package net.codjo.referential.gui.addon.tabbed.customiser;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.api.Field;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import javax.swing.JComponent;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class AbstractDynamicGuiCustomiserTest {
    protected GuiFieldGroup guiFieldGroup;
    protected AbstractDynamicGuiCustomiser customizer;


    private GuiFieldGroup mockGuiFieldGroup() {
        GuiFieldGroup mockedGuiFieldGroup = new GuiFieldGroup("group");
        GuiField field1 = new GuiField("champ1");
        field1.setComponentClassName("javax.swing.JTextField");
        mockedGuiFieldGroup.addGuiField(field1);
        mockedGuiFieldGroup.addGuiField(new GuiField("champ2"));
        return mockedGuiFieldGroup;
    }


    @Before
    public void before() {
        guiFieldGroup = mockGuiFieldGroup();
        customizer = new AbstractDynamicGuiCustomiser(guiFieldGroup) {
            public boolean handle(Field field) {
                return false;
            }


            public JComponent createGui(Field field) {
                return null;
            }


            public void declareField(DetailDataSource dataSource, Field field) {
            }


            public void initDefaultFieldValue(Field field) {
            }
        };
    }


    @Test
    public void test_translate() throws Exception {
        assertThat((customizer.classNameTranslator.get("javax.swing.JComboBox") == null ?
                    "javax.swing.JComboBox" :
                    customizer.classNameTranslator.get("javax.swing.JComboBox")),
                   equalTo("javax.swing.JComboBox"));
        assertThat((customizer.classNameTranslator.get("autoCompleteComboBox") == null ?
                    "autoCompleteComboBox" :
                    customizer.classNameTranslator.get("autoCompleteComboBox")),
                   equalTo("net.codjo.mad.gui.request.AutoCompleteRequestComboBox"));
    }


    @Test
    public void test_translate_nullCase() throws Exception {
        assertThat((customizer.classNameTranslator.get(null) == null ?
                    (String)null :
                    customizer.classNameTranslator.get(null)), is(nullValue()));
    }
}
