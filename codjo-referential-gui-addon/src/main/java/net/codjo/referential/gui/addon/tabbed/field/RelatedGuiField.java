package net.codjo.referential.gui.addon.tabbed.field;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import javax.swing.JComponent;

public class RelatedGuiField extends Field {
    private Field masterField = null;


    public RelatedGuiField(String name, String label) {
        super(name, label, null);
    }


    @Override
    public void setComponent(JComponent component) {
        component.setName(getName());
        super.setComponent(component);
    }


    public Field getMasterField() {
        return masterField;
    }


    public void setMasterField(Field masterField) {
        this.masterField = masterField;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        RelatedGuiField that = (RelatedGuiField)obj;
        boolean comp = super.equals(that);
        comp = comp && (masterField == null ?
                        that.masterField == null :
                        masterField.equals(that.masterField));
        return comp;
    }


    @Override
    public int hashCode() {
        final int prime = 17;
        int code = 31;
        code = code + prime * (super.hashCode());
        code = code + prime * (masterField == null ? 0 : masterField.hashCode());
        return code;
    }


    public void initValuesFromReferential(final Referential referential) {
        final Field relatedField = referential.getField(getName());
        if (relatedField != null) {
            setType(relatedField.getType() == null ? "java.lang.String" : relatedField.getType());
            setLength(relatedField.getLength() < 0 ? 20 : relatedField.getLength());
        }
    }
}
