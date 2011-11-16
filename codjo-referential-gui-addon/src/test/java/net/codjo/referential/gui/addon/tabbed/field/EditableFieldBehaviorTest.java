package net.codjo.referential.gui.addon.tabbed.field;

import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EditableFieldBehaviorTest {
    private Referential mockedReferential;
    private Field mockedField;
    private Field mockedField2;


    @Before
    public void before() {
        mockedReferential = mock(Referential.class);
        mockedField = new Field("fred", "fred", "java.lang.String");
        mockedField.setComponent(new JTextField());
        mockedField2 = new Field("fred2", "fred2", "java.lang.String");
        mockedField2.setComponent(new JTextField());
        when(mockedReferential.getField("fred")).thenReturn(mockedField);
        when(mockedReferential.getField("fred2")).thenReturn(mockedField2);
    }


    @Test
    public void test_visit() throws Exception {
        EditableFieldBehavior behavior = new EditableFieldBehavior(mockedReferential);
        GuiField guiField = new GuiField("fred");
        guiField.setEditable(false);
        GuiField guiField2 = new GuiField("fred2");
        guiField2.setEditable(true);
        GuiFieldGroup fieldGroup = new GuiFieldGroup("testGroup", guiField, guiField2);
        behavior.visit(fieldGroup, guiField);
        JComponent mockedFieldComponent = mockedField.getComponent();
        assertThat(mockedFieldComponent instanceof JTextField, equalTo(true));
        assertThat(((JTextField)mockedFieldComponent).isEditable(), equalTo(false));
        behavior.visit(fieldGroup, guiField2);
        JComponent mockedFieldComponent2 = mockedField2.getComponent();
        assertThat(mockedFieldComponent2 instanceof JTextField, equalTo(true));
        assertThat(mockedFieldComponent2.isEnabled(), equalTo(true));
        //noinspection ConstantConditions
        assertThat(((JTextField)mockedFieldComponent2).isEditable(), equalTo(true));
    }


    @Test
    public void test_visit_bis() throws Exception {
        EditableFieldBehavior behavior = new EditableFieldBehavior(mockedReferential);
        GuiField guiField = new GuiField("fred");
        guiField.setEditable(false);
        GuiField guiField2 = new GuiField("fred2");
        guiField2.setEditable(true);
        GuiFieldGroup fieldGroup = new GuiFieldGroup("testGroup", guiField, guiField2);
        behavior.visit(fieldGroup, guiField);
        assertThat(((JTextComponent)mockedField.getComponent()).isEditable(), equalTo(false));
        behavior.visit(fieldGroup, guiField2);
        assertThat(((JTextComponent)mockedField2.getComponent()).isEditable(), equalTo(true));
    }
}
