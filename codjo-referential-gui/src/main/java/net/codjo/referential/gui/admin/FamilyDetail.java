package net.codjo.referential.gui.admin;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonPanelGui;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 *
 */
public class FamilyDetail extends JInternalFrame {
    private JTextField nameField;
    private ButtonPanelGui buttonPanel;
    private JPanel mainPanel;


    public FamilyDetail(DetailDataSource dataSource) throws RequestException {
        super("Famille de référentiels");
        nameField.setName("familyLabel");
        final ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic(buttonPanel);
        buttonPanelLogic.setMainDataSource(dataSource);
        dataSource.declare("familyLabel", nameField);
        dataSource.load();
        getContentPane().add(mainPanel);      
        pack();
    }

}
