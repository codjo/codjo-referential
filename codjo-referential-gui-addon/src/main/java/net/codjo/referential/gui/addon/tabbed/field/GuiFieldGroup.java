package net.codjo.referential.gui.addon.tabbed.field;
import net.codjo.referential.gui.addon.tabbed.util.filter.Filter;
import net.codjo.referential.gui.api.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GuiFieldGroup {
    private String title;
    private List<GuiField> guiFields;


    public GuiFieldGroup(String title) {
        this.title = title;
        guiFields = new ArrayList<GuiField>();
    }


    public GuiFieldGroup(String title, GuiField... fields) {
        this(title);
        guiFields.addAll(Arrays.asList(fields));
    }


    public void addGuiField(GuiField guiField) {
        guiFields.add(guiField);
    }


    public String getTitle() {
        return title;
    }


    public Collection<Field> getFields(Map<String, Field> allFields, Filter<Field>... filters) {
        Collection<Field> list = new ArrayList<Field>();
        for (GuiField guiField : guiFields) {
            Field field = allFields.get(guiField.getName());
            if (field == null) {
                throw new IllegalArgumentException(
                      "Field '" + guiField.getName() + "' absent du dictionnaire");
            }
            list.add(field);
        }
        for (Filter<Field> filter : filters) {
            list = filter.filter(list);
        }
        return list;
    }


    public boolean hasRelatedGuiField(String fieldName) {
        final List<RelatedGuiField> emptyList = new ArrayList<RelatedGuiField>();
        List<RelatedGuiField> relatedGuiFields = new ArrayList<RelatedGuiField>();
        for (GuiField guiField : guiFields) {
            relatedGuiFields.addAll(
                  guiField.getRelatedGuiFields() == null ? emptyList : guiField.getRelatedGuiFields());
        }
        for (RelatedGuiField relatedGuiField : relatedGuiFields) {
            if (fieldName.equals(relatedGuiField.getName())) {
                return true;
            }
        }
        return false;
    }


    public List<GuiField> getGuiFields() {
        return guiFields;
    }

    public GuiField getGuiField(String fieldName) {
        for (GuiField guiField : guiFields) {
            if (fieldName.equals(guiField.getName())) {
                return guiField;
            }
        }
        return null;
    }
}
