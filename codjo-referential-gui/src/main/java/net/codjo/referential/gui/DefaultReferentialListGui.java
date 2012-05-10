/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.referential.gui;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.Internationalizable;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.Position;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import net.codjo.referential.gui.api.Referential;
import net.codjo.referential.gui.api.ReferentialListGui;

/**
 *
 */
public class DefaultReferentialListGui extends JPanel implements ReferentialListGui, Internationalizable {
    protected RequestTable table;
    protected RequestToolBar toolBar;
    private String title = null;
    private TitledBorder titleBorder;
    private JPanel topPanelContainer = new JPanel(new BorderLayout());
    private JPanel centerPanel;
    private GuiContext guiContext;


    public DefaultReferentialListGui(GuiContext guiContext) {
        this.guiContext = guiContext;
        initI18n();
        titleBorder = BorderFactory.createTitledBorder(computeTitle());
        setBorder(titleBorder);
        buildGui();
    }


    public DefaultReferentialListGui(GuiContext guiContext, String title) {
        this.guiContext = guiContext;
        initI18n();
        this.title = title;
        titleBorder = BorderFactory.createTitledBorder(title);
        buildGui();
    }


    public void init(Preference preference, Referential referential) {
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


    public void updateTranslation(Language language, TranslationManager translator) {
        titleBorder.setTitle(computeTitle());
    }


    private String computeTitle() {
        String fixTitle = InternationalizationUtil.translate("DefaultReferentialListGui.title", guiContext);
        return ((title == null) || ("".equals(title))) ? fixTitle : fixTitle + " '" + title + "'";
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


    private void initI18n() {
        TranslationNotifier translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        translationNotifier.addInternationalizable(this);
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
        titleBorder.setTitle(computeTitle());
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
