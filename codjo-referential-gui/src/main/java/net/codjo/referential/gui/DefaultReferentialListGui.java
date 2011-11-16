/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.referential.gui;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.Position;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import net.codjo.referential.gui.api.Referential;
import net.codjo.referential.gui.api.ReferentialListGui;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
/**
 *
 */
public class DefaultReferentialListGui extends JPanel implements ReferentialListGui {

    protected RequestTable table;
    protected RequestToolBar toolBar;
    private String title = "";
    private TitledBorder titleBorder;
    private JPanel topPanelContainer = new JPanel(new BorderLayout());
    private JPanel centerPanel;


    public DefaultReferentialListGui() {
        titleBorder = BorderFactory.createTitledBorder("Listes de données du référentiel " + title);
        setBorder(titleBorder);
        buildGui();
    }


    public DefaultReferentialListGui(String title) {
        this.title = title;
        titleBorder = BorderFactory.createTitledBorder(title);
        buildGui();
    }


    public void init(GuiContext guiContext, Preference preference, Referential referential) {
        table.setPreference(preference);
        toolBar.setHasExcelButton(true);
        toolBar.init(guiContext, table);

        ReferentialDetailWindowBuilder windowDetailBuilder =
              new ReferentialDetailWindowBuilder(toolBar, this.title, referential);
        toolBar.replace(RequestToolBar.ACTION_EDIT,
                        new EditReferentialAction(guiContext, table, windowDetailBuilder));
        toolBar.replace(RequestToolBar.ACTION_ADD,
                        new AddReferentialAction(guiContext, table, windowDetailBuilder));
        toolBar.add(new DuplicateAction(guiContext, table, windowDetailBuilder),
                    DuplicateAction.ACTION_NAME,
                    Position.right(RequestToolBar.ACTION_DELETE),
                    true);
    }


    public void load() throws RequestException {
        table.load();
    }


    public void create() {
        table = new RequestTable();
        toolBar = new RequestToolBar();

        addCenterPanel();
    }


    private void buildGui() {
        this.setLayout(new BorderLayout());
        this.add(topPanelContainer, BorderLayout.NORTH);
    }


    private void addCenterPanel() {
        invalidate();
        if (centerPanel != null) {
            this.remove(centerPanel);
        }

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        centerPanel.add(toolBar, BorderLayout.SOUTH);
        this.add(centerPanel, BorderLayout.CENTER);
        validate();
    }


    public void setTitle(String title) {
        this.title = title;
        titleBorder.setTitle("Listes de données du référentiel " + title);
    }


    public String getTitle() {
        return title;
    }


    public JPanel getTopPanel() {
        if (topPanelContainer.getComponentCount() == 0) {
            return null;
        }
        return (JPanel)topPanelContainer.getComponent(0);
    }


    public void setTopPanel(JPanel topPanel) {
        topPanelContainer.removeAll();
        if (topPanel == null) {
            return;
        }
        topPanelContainer.add(topPanel, BorderLayout.CENTER);
    }


    public RequestTable getTable() {
        return table;
    }


    public RequestToolBar getToolBar() {
        return (toolBar);
    }
}
