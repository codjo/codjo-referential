package net.codjo.referential.gui.api;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import javax.swing.JPanel;
/**
 * Représente l'affichage en liste d'un referentiel.
 */
public interface ReferentialListGui {
    public String getTitle();


    public void setTitle(String title);


    public JPanel getTopPanel();


    public void setTopPanel(JPanel topPanel);


    public RequestTable getTable();


    public RequestToolBar getToolBar();
}
