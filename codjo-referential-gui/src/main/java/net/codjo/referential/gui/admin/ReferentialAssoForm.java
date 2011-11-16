package net.codjo.referential.gui.admin;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import net.codjo.mad.gui.request.util.comparators.RowComparator;
import net.codjo.mad.gui.request.util.selection.SelectionGui;
import net.codjo.referential.gui.api.Referential;
import java.awt.Component;
import java.awt.Dimension;
import static java.util.Arrays.asList;
import java.util.SortedMap;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 *
 */
class ReferentialAssoForm extends JInternalFrame {
    private JPanel mainPanel;
    private JButton okButton;
    private RequestTable familyList;
    private RequestToolBar familyListToolbar;
    private JSplitPane splitPane;
    private SelectionGui selectionGui;
    private JTextField filterRefTextField;
    private JButton resetFilterButton;
    private JButton performFilterButton;
    private SortedMap<String, Referential> referentials;


    ReferentialAssoForm(GuiContext guiContext, SortedMap<String, Referential> referentials) throws Exception {
        super("Gestion des familles de référentiels", true, true, true, true);
        this.referentials = referentials;
        setPreferredSize(new Dimension(1000, 600));
        splitPane.setDividerLocation(300);
        getContentPane().add(mainPanel);
        pack();
        familyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        familyList.setPreference("PmReferentialFamily");
        familyList.getPreference().getInsert().setExcludedFieldList(new String[]{"familyId"});
        familyListToolbar.init(guiContext, familyList);

        selectionGui.init(buildReferentialFromTable(), buildReferentialToTable());
        selectionGui.setFromLabel("");
        selectionGui.setToLabel("");
        selectionGui.getSelectButton().setName("addReferential");
        selectionGui.getUnSelectButton().setName("removeReferential");
    }


    private class ReferentialListCompartor implements RowComparator {
        public int compare(Row row1, Row row2) {
            String title1 = referentials.get(row1.getFieldValue("referentialId")).getTitle();
            String title2 = referentials.get(row2.getFieldValue("referentialId")).getTitle();
            return title1.compareTo(title2);
        }
    }


    private RequestTable buildReferentialFromTable() {
        RequestTable fromTable = new RequestTable();
        fromTable.disableSorter();

        Preference fromPreference = new Preference("ReferentialList");
        fromPreference.setColumns(asList(new Column("referentialId", "Tous les référentiels")));
        fromTable.setPreference(fromPreference);

        return fromTable;
    }


    private RequestTable buildReferentialToTable() {
        RequestTable toTable = new RequestTable();
        toTable.setRowComparator(new ReferentialListCompartor());

        toTable.setPreference("RefFamilyAsso");
        toTable.getPreference().getInsert().setExcludedFieldList(new String[]{"referentialLabel"});
        toTable.getPreference().getUpdate().setExcludedFieldList(new String[]{"referentialLabel"});

        return toTable;
    }


    void initTableCellRenderers() {
        RequestTable fromTable = selectionGui.getFromTable();
        RequestTable toTable = selectionGui.getToTable();
        toTable.setCellRenderer("referentialId", new MyDefaultTableCellRenderer());
        TableCellRenderer cellRenderer = fromTable.getDefaultRenderer(String.class);
        fromTable.setCellRenderer("referentialId", new MyDefaultTableCellRenderer(cellRenderer));
    }


    SelectionGui getSelectionGui() {
        return selectionGui;
    }


    RequestTable getFamilyList() {
        return familyList;
    }


    JButton getOkButton() {
        return okButton;
    }


    JTextField getFilterRefTextField() {
        return filterRefTextField;
    }


    JButton getResetFilterButton() {
        return resetFilterButton;
    }


    JButton getPerformFilterButton() {
        return performFilterButton;
    }


    private class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {
        private TableCellRenderer renderer;


        private MyDefaultTableCellRenderer() {
        }


        private MyDefaultTableCellRenderer(TableCellRenderer cellRenderer) {
            this.renderer = cellRenderer;
        }


        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean focus,
                                                       int row,
                                                       int column) {
            String title = referentials.get(value.toString()).getTitle();
            if (renderer != null) {
                return renderer.getTableCellRendererComponent(table, title, isSelected, focus, row, column);
            }
            else {
                return super.getTableCellRendererComponent(table, title, isSelected, focus, row, column);
            }
        }
    }
}
