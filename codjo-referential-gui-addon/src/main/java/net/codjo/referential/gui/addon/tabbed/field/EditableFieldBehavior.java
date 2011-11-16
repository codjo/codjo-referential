package net.codjo.referential.gui.addon.tabbed.field;
import net.codjo.referential.gui.api.Referential;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public class EditableFieldBehavior implements GuiFieldBehavior {
    private final Referential referential;


    public EditableFieldBehavior(Referential referential) {
        this.referential = referential;
    }


    public void visit(GuiFieldGroup group, GuiField guiField) {
        boolean editable = guiField.isEditable();
        if (!editable) {
            JComponent component = referential.getField(guiField.getName()).getComponent();
            if (component instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent)component;
                textComponent.setEditable(editable);
                textComponent.setEnabled(true);
            }
            else {
                component.setEnabled(editable);
            }
        }
    }
}
