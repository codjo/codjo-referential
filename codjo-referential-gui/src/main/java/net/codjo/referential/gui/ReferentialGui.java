package net.codjo.referential.gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import net.codjo.gui.toolkit.tree.Matcher;
import net.codjo.gui.toolkit.tree.TreeFilterModel;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.Internationalizable;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.referential.gui.api.Referential;
import net.codjo.referential.gui.api.ReferentialTreeGui;

public class ReferentialGui extends JInternalFrame implements ReferentialTreeGui,
                                                              InternationalizableContainer,
                                                              Internationalizable {
    private JPanel panel;
    private JTree referentialTree;
    private JPanel referentialPanel;
    private JTextField searchField;
    private JButton resetSearchButton;
    private JButton performSearchButton;
    private JLabel filterLabel;
    private JPanel refListPanel;
    private final GuiContext guiContext;

    private TreeFilterModel treeFilterModel;
    private TranslationNotifier translationNotifier;


    ReferentialGui(GuiContext guiContext, String title, Dimension preferredSize) {
        super(title, true, true, true, true);
        this.guiContext = guiContext;
        initFamilyTree();
        initPerformSearchButton();
        initResetSearchButton();
        initSearchField();

        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        translationNotifier.addInternationalizableContainer(this);
        translationNotifier.addInternationalizable(this);

        updateSearchFieldTooltip(InternationalizationUtil.retrieveTranslationManager(guiContext));

        getContentPane().add(panel);
        this.setPreferredSize(preferredSize == null ? new Dimension(990, 600) : preferredSize);
        this.pack();
    }


    public void updateTranslation(Language language, TranslationManager translator) {
        updateSearchFieldTooltip(translator);
    }


    private void updateSearchFieldTooltip(TranslationManager translator) {
        searchField.setToolTipText(translator.translate("ReferentialGui.searchField.tooltip",
                                                        translationNotifier.getLanguage()));
    }


    public void addInternationalizableComponents(TranslationNotifier notifier) {
        translationNotifier.addInternationalizableComponent(this, "ReferentialGui.title");
        translationNotifier.addInternationalizableComponent(refListPanel, "ReferentialGui.refListPanel.title");
        translationNotifier.addInternationalizableComponent(filterLabel, "ReferentialGui.filterLabel");
        translationNotifier.addInternationalizableComponent(performSearchButton,
                                                            null,
                                                            "ReferentialGui.performSearchButton.tooltip");
        translationNotifier.addInternationalizableComponent(resetSearchButton,
                                                            null,
                                                            "ReferentialGui.resetSearchButton.tooltip");
    }


    private void initFamilyTree() {
        referentialTree.setName("ReferentialList");
        referentialTree.setRootVisible(false);
        referentialTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        referentialTree.setCellRenderer(new FamilyTreeRenderer());
    }


    public void addReferentialPanel(JPanel refPanel) {
        referentialPanel.add(new JScrollPane(refPanel), BorderLayout.CENTER);
    }


    public void fillFamilyTree(Map<String, Set<Referential>> familyList) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");
        for (Map.Entry<String, Set<Referential>> entry : familyList.entrySet()) {
            addFamilyNode(rootNode, entry);
        }
        treeFilterModel = initTreeFilterModel(rootNode);
        referentialTree.setModel(treeFilterModel);
    }


    private TreeFilterModel initTreeFilterModel(DefaultMutableTreeNode rootNode) {
        TreeFilterModel model = new TreeFilterModel(rootNode);
        model.addFilteringCriteria("searchString", new Matcher() {
            public boolean isApplicable(DefaultMutableTreeNode node) {
                return node.isLeaf();
            }


            public boolean match(Object userObject, String constraint) {
                return constraint == null || userObject.toString().toLowerCase()
                      .contains(constraint.toLowerCase());
            }
        });
        return model;
    }


    private void initPerformSearchButton() {
        performSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                treeFilterModel.setFilteringCriteriaConstraint("searchString", searchField.getText());
            }
        });
    }


    private void initResetSearchButton() {
        resetSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                searchField.setText("");
                treeFilterModel.resetFilteringCriteria();
            }
        });
    }


    private void initSearchField() {
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                treeFilterModel.setFilteringCriteriaConstraint("searchString", searchField.getText());
            }
        });
    }


    private void addFamilyNode(DefaultMutableTreeNode rootNode,
                               Map.Entry<String, Set<Referential>> familyEntry) {
        String familyName = familyEntry.getKey();
        if (ReferentialLogic.DEFAULT_FAMILY.equals(familyName)) {
            addReferentialNodes(rootNode, familyEntry.getValue());
        }
        else {
            DefaultMutableTreeNode family = new DefaultMutableTreeNode(familyEntry.getKey());
            addReferentialNodes(family, familyEntry.getValue());
            rootNode.add(family);
        }
    }


    private void addReferentialNodes(DefaultMutableTreeNode parentNode, Set<Referential> referentialNames) {
        for (Referential referential : referentialNames) {
            parentNode.add(new DefaultMutableTreeNode(referential));
        }
    }


    public GuiContext getGuiContext() {
        return guiContext;
    }


    public JTree getReferentialTree() {
        return referentialTree;
    }


    private static class FamilyTreeRenderer extends DefaultTreeCellRenderer {

        private Icon referentialIcon;

        private Icon familyIcon;


        private FamilyTreeRenderer() {
            referentialIcon = createImageIcon("table.png");
            familyIcon = createImageIcon("table_multiple.png");
        }


        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                      boolean expanded, boolean leaf, int row,
                                                      boolean focus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
            if (leaf) {
                setIcon(referentialIcon);
            }
            else {
                setIcon(familyIcon);
            }

            return this;
        }
    }


    private static ImageIcon createImageIcon(String path) {
        URL imgURL = ReferentialGui.class.getResource(path);
        return new ImageIcon(imgURL);
    }
}
