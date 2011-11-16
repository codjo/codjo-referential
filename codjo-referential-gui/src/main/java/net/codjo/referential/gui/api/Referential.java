package net.codjo.referential.gui.api;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.PreferenceFactory;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 *
 */
public class Referential implements Serializable, Comparable<Referential> {
    private String preferenceId;
    private String title;
    private Map<String, Field> fieldList = new LinkedHashMap<String, Field>();
    private String name;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getPreferenceId() {
        return preferenceId;
    }


    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }


    public Preference getPreference() {
        return PreferenceFactory.getPreference(getPreferenceId());
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void addField(Field field) {
        fieldList.put(field.getName(), field);
    }


    public Map<String, Field> getFieldList() {
        return fieldList;
    }


    public Field getField(String fieldName) {
        return fieldList.get(fieldName);
    }


    public int compareTo(Referential referential) {
        if (getTitle() == null) {
            return -1;
        }
        if (referential == null || referential.getTitle() == null) {
            return 1;
        }
        return getTitle().compareTo(referential.getTitle());
    }


    public boolean hasField(final String fieldName) {
        return fieldName != null && (getField(fieldName) != null);
    }


    @Override
    public String toString() {
        return getTitle();
    }
}
