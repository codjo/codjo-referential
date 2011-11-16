package net.codjo.referential.gui;
import net.codjo.referential.gui.api.Referential;
import java.util.SortedMap;
/**
 *
 */
public interface ReferentialMapping {
    public SortedMap<String, Referential> getReferentialsByPreferenceId() throws Exception;
}
