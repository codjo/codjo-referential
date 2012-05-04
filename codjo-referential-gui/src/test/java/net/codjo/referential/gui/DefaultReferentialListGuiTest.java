package net.codjo.referential.gui;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import javax.swing.JPanel;
import org.junit.Test;
/**
 *
 */
public class DefaultReferentialListGuiTest {
    @Test
    public void test_topPanel() throws Exception {
        DefaultReferentialListGui gui = new DefaultReferentialListGui(new ReferentialGuiContext());

        JPanel topPanel = new JPanel();
        gui.setTopPanel(topPanel);
        assertThat(gui.getTopPanel(), is(sameInstance(topPanel)));
    }


    @Test
    public void test_topPanel_twice() throws Exception {
        DefaultReferentialListGui gui = new DefaultReferentialListGui(new ReferentialGuiContext());

        gui.setTopPanel(new JPanel());

        JPanel topPanel = new JPanel();
        gui.setTopPanel(topPanel);

        assertThat(gui.getTopPanel(), is(sameInstance(topPanel)));
    }


    @Test
    public void test_topPanel_empty() throws Exception {
        DefaultReferentialListGui gui = new DefaultReferentialListGui(new ReferentialGuiContext());
        assertThat(gui.getTopPanel(), is(nullValue()));
    }


    @Test
    public void test_setTitle() throws Exception {
        DefaultReferentialListGui gui = new DefaultReferentialListGui(new ReferentialGuiContext(), "Titre de la barre");
        assertThat(gui.getTitle(), equalTo("Titre de la barre"));
    }
}
