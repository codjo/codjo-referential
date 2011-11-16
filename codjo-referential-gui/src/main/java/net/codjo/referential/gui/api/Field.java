package net.codjo.referential.gui.api;
import javax.swing.JComponent;
/**
 *
 */
public class Field {
    private String name;
    private String label;
    private String type;
    private int length;
    private int decimalLength;
    private boolean isPrimaryKey;
    private boolean isRequired;
    private String handlerId;
    private JComponent component;
    private boolean generated;
    private String defaultValue;


    public Field() {
    }


    public Field(String name, String label, String type) {
        this.name = name;
        this.label = label;
        this.type = type;
    }


    @Override
    public int hashCode() {
        final int prime = 17;
        int code = 31;
        code = code + prime * (name == null ? 0 : name.hashCode());
        code = code + prime * (label == null ? 0 : label.hashCode());
        code = code + prime * (type == null ? 0 : type.hashCode());
        return code;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Field that = (Field)obj;
        boolean comp = (name == null ? that.name == null : name.equals(that.name));
        comp = comp && (label == null ? that.label == null : label.equals(that.label));
        comp = comp && (type == null ? that.type == null : type.equals(that.type));
        return comp;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public int getDecimalLength() {
        return decimalLength;
    }


    public void setDecimalLength(int decimalLength) {
        this.decimalLength = decimalLength;
    }


    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }


    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public boolean isRequired() {
        return isRequired;
    }


    public void setRequired(boolean required) {
        isRequired = required;
    }


    public JComponent getComponent() {
        return component;
    }


    public void setComponent(JComponent component) {
        this.component = component;
    }


    public String getHandlerId() {
        return handlerId;
    }


    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }


    public int getLength() {
        return length;
    }


    public void setLength(int length) {
        this.length = length;
    }


    public boolean isAuditField() {
        return "audit".equals(getName());
    }


    public boolean isNumberField() {
        String lowerType = type.toLowerCase();
        return lowerType.contains("integer")
               || type.toLowerCase().contains("double")
               || "big-decimal".equals(type)
               || lowerType.contains("float")
               || lowerType.contains("long");
    }


    public void setGenerated(boolean generated) {
        this.generated = generated;
    }


    public boolean isGenerated() {
        return generated;
    }


    public String getDefaultValue() {
        return defaultValue;
    }


    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public String getDefaultValueWithoutQuotes() {
        return (getDefaultValue() == null ? null : getDefaultValue().replace("'", ""));
    }


}
