package net.codjo.referential.gui.addon.tabbed.field;
import java.util.ArrayList;
import java.util.List;

public class GuiField {
    private String name;
    private String handlerId;
    private String selector;
    private Boolean editable;
    private Boolean rootDisplayed;
    private String master;
    private Boolean sort;
    private Integer width;
    private String componentClassName = null;
    private List<RelatedGuiField> relatedGuiFields = new ArrayList<RelatedGuiField>();


    public GuiField(String name) {
        this.name = name;
    }


    public GuiField(String name, String handlerId, String selector) {
        this.name = name;
        this.handlerId = handlerId;
        this.selector = selector;
    }


    public String getName() {
        return name;
    }


    public String getHandlerId() {
        return handlerId;
    }


    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }


    public String getSelector() {
        return selector;
    }


    public List<RelatedGuiField> getRelatedGuiFields() {
        if (relatedGuiFields == null) {
            relatedGuiFields = new ArrayList<RelatedGuiField>();
        }
        return relatedGuiFields;
    }


    public void addRelatedGuiFields(RelatedGuiField relatedGuiField) {
        getRelatedGuiFields().add(relatedGuiField);
    }


    public void setEditable(boolean editable) {
        this.editable = editable;
    }


    public boolean isEditable() {
        return getValue(editable);
    }


    public void setRootDisplayed(boolean rootDisplayed) {
        this.rootDisplayed = rootDisplayed;
    }


    public boolean isRootDisplayed() {
        return getValue(rootDisplayed);
    }


    public boolean hasRelatedGuiFields() {
        return relatedGuiFields != null && !relatedGuiFields.isEmpty();
    }


    public String getMasterName() {
        return master;
    }


    public void setMaster(String master) {
        this.master = master;
    }


    public boolean isSort() {
        return getValue(sort);
    }


    public void setSort(boolean sort) {
        this.sort = sort;
    }


    public int getWidth() {
        return getValue(width);
    }


    public void setWidth(int width) {
        this.width = width;
    }


    public String getComponentClassName() {
        return componentClassName;
    }


    public void setComponentClassName(String componentClassName) {
        this.componentClassName = componentClassName;
    }


    private boolean getValue(Boolean value) {
        return value == null || value;
    }


    private int getValue(Integer value) {
        return value == null ? -1 : value;
    }
}
