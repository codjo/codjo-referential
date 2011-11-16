package net.codjo.referential.gui.addon.tabbed;
import net.codjo.mad.gui.framework.AbstractDetailGui;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.addon.tabbed.customiser.DynamicComponentGuiCustomiser;
import net.codjo.referential.gui.addon.tabbed.customiser.RelatedFieldsGuiCustomiser;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.api.CompositeGuiCustomiser;
import net.codjo.referential.gui.api.DefaultGuiCustomizer;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import net.codjo.referential.gui.api.ReferentialItemPanel;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public class TabbedPaneDetailGui extends AbstractDetailGui {
    protected List<GuiStruct> guiPanels = new ArrayList<GuiStruct>();
    protected Map<String, Field> fieldMap;
    protected Referential referential;


    public TabbedPaneDetailGui(DetailDataSource dataSource,
                               Referential referential,
                               GuiReferential guiReferential) {
        super(referential.getTitle());
        this.referential = referential;
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setName("tabbedPane");
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        addEscapeKeystrokeToCloseFrame();
        buildTabbedPane(dataSource, guiReferential, tabbedPane);
    }


    protected void buildTabbedPane(final DetailDataSource dataSource,
                                   final GuiReferential guiReferential, final JTabbedPane tabbedPane) {
        fieldMap = referential.getFieldList();
        for (GuiFieldGroup guiFieldGroup : guiReferential.getFieldsGroup()) {
            ReferentialItemPanel panel = new ReferentialItemPanel();
            guiPanels.add(new GuiStruct(guiFieldGroup, panel));
            addItemPanel(tabbedPane, panel, guiFieldGroup.getTitle());
            GuiCustomizer customiser = createCustomizer(panel, guiReferential, guiFieldGroup);
            Collection<Field> fields = guiFieldGroup.getFields(fieldMap);
            panel.buildPanel(fields, customiser);
            panel.declareFields(dataSource, fields, customiser);
            initDefaultValues(fields, customiser);
        }
    }


    protected void addItemPanel(JTabbedPane tabbedPane,
                                Component panel, String title) {
        tabbedPane.addTab(title, panel);
    }


    protected void initDefaultValues(Collection<Field> fields, GuiCustomizer customiser) {
        for (Field field : fields) {
            customiser.initDefaultFieldValue(field);
        }
    }


    protected GuiCustomizer createCustomizer(ReferentialItemPanel panel,
                                             GuiReferential guiReferential, GuiFieldGroup guiFieldGroup) {
        CompositeGuiCustomiser compositeGuiCustomizer = new CompositeGuiCustomiser();
        compositeGuiCustomizer.addCustomizer(new RelatedFieldsGuiCustomiser(guiFieldGroup, referential,
                                                                            guiReferential));
        compositeGuiCustomizer.addCustomizer(new DynamicComponentGuiCustomiser(guiFieldGroup));
        compositeGuiCustomizer.addCustomizer(new DefaultGuiCustomizer());
        return compositeGuiCustomizer;
    }


    @Override
    public void declareFields(DetailDataSource dataSource) {
    }


    @Override
    public void switchToUpdateMode() {
        for (GuiStruct guiPanel : guiPanels) {
            guiPanel.itemPanel.switchToUpdateMode(guiPanel.group.getFields(fieldMap));
        }
    }


    @Override
    protected void buildAndAddItems() {
    }


    private void addEscapeKeystrokeToCloseFrame() {
        InputMap inputMap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                try {
                    setClosed(true);
                }
                catch (PropertyVetoException e) {
                    ;
                }
            }
        });
    }


    public static class GuiStruct {
        ReferentialItemPanel itemPanel;
        GuiFieldGroup group;


        public GuiStruct(GuiFieldGroup group, ReferentialItemPanel itemPanel) {
            this.group = group;
            this.itemPanel = itemPanel;
        }
    }
}
