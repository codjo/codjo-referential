package net.codjo.referential.gui.addon.tabbed.util.filter;
import java.util.Collection;

public interface Filter<T> {

    public Collection<T> filter(Collection<T> collection);
}
