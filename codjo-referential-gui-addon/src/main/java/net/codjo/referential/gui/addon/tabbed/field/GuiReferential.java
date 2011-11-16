package net.codjo.referential.gui.addon.tabbed.field;
import java.util.ArrayList;
import java.util.List;

public class GuiReferential {
    private String name;
    private List<GuiFieldGroup> groups = new ArrayList<GuiFieldGroup>();


    public void add(GuiFieldGroup group) {
        groups.add(group);
    }


    public List<GuiFieldGroup> getFieldsGroup() {
        return groups;
    }


    public GuiFieldGroup getGuiFieldGroup(String groupName) {
        for (GuiFieldGroup guiFieldGroup : groups) {
            if (groupName.equals(guiFieldGroup.getTitle())) {
                return guiFieldGroup;
            }
        }
        return null;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void clearGroups() {
        groups.clear();
    }


    public void accept(GuiFieldBehavior behavior) {
        for (GuiFieldGroup group : getFieldsGroup()) {
            for (GuiField guiField : group.getGuiFields()) {
                behavior.visit(group, guiField);
            }
        }
    }


    public GuiFieldGroup findGuiFieldGroup(final String groupName) {
        if (groupName == null) {
            return null;
        }
        for (GuiFieldGroup group : groups) {
            if (groupName.equals(group.getTitle())) {
                return group;
            }
        }
        return null;
    }


    public GuiField findFirstGuiField(final String fieldName) {
        SearchGuiFieldBehavior search = new SearchGuiFieldBehavior(fieldName);
        accept(search);
        return search.getResult();
    }


    public List<GuiField> findGuiFields(final String fieldName) {
        SearchGuiFieldListBehavior searchBehavior = new SearchGuiFieldListBehavior(fieldName);
        accept(searchBehavior);
        return searchBehavior.getResult();
    }


    private abstract static class AbstractSearchGuiFieldBehavior<T> implements GuiFieldBehavior {
        protected T result;


        public T getResult() {
            return result;
        }
    }

    private static class SearchGuiFieldListBehavior extends AbstractSearchGuiFieldBehavior<List<GuiField>> {
        private final String fieldName;

        private SearchGuiFieldListBehavior(String fieldName) {
            this.fieldName = fieldName;
            this.result = new ArrayList<GuiField>();
        }


        public void visit(GuiFieldGroup group, GuiField guiField) {
            if (fieldName.equals(guiField.getName())) {
                result.add(guiField);
            }
        }
    }

    private static class SearchGuiFieldBehavior extends AbstractSearchGuiFieldBehavior<GuiField> {
        private final String fieldName;


        private SearchGuiFieldBehavior(String fieldName) {
            this.fieldName = fieldName;
        }


        public void visit(GuiFieldGroup group, GuiField guiField) {
            if (fieldName.equals(guiField.getName())) {
                this.result = guiField;
            }
        }
    }
}
