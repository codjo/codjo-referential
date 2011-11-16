package net.codjo.referential.gui.api;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.junit.Test;
import org.mockito.Mockito;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.test.common.matcher.JUnitMatchers;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class AbstractGuiCustomizerTest {

    @Test
    public void test_declareFields() throws Exception {
        GuiContext guiContext = Mockito.mock(GuiContext.class);
        DetailDataSource dataSource = new DetailDataSource(guiContext);
        Field field = new Field("field", "label", "java.lang.String");
        field.setDefaultValue(null);
        field.setComponent(new JTextField());
        TestGuiCustomizer customier = new TestGuiCustomizer();
        customier.declareField(dataSource, field);
        assertThat(dataSource.getDeclaredFields().size(), equalTo(1));
    }


    private static class TestGuiCustomizer extends AbstractGuiCustomizer {

        public boolean handle(Field field) {
            return false;  // Todo
        }


        public JComponent createGui(Field field) {
            return null;  // Todo
        }


        public void initDefaultFieldValue(Field field) {
            // Todo
        }
    }
}
