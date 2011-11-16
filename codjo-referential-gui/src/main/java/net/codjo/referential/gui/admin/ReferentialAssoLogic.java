package net.codjo.referential.gui.admin;
import net.codjo.gui.toolkit.table.TableFilter;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.JoinKeys;
import net.codjo.mad.gui.request.ListDataSource;
import static net.codjo.mad.gui.request.ListDataSource.CONTENT_PROPERTY;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RowFiller;
import net.codjo.mad.gui.request.util.selection.SelectionGui;
import net.codjo.mad.gui.request.util.selection.SelectionLogic;
import net.codjo.referential.gui.ReferentialMapping;
import net.codjo.referential.gui.api.Referential;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 *
 */
class ReferentialAssoLogic {

    private ReferentialAssoForm gui;
    private String currentFamilyId;
    private RequestTable toTable;
    private RequestTable fromTable;
    private TableFilter filterModel;
    private SortedMap<String, Referential> referentials;
    private RequestTable familyList;


    ReferentialAssoLogic(GuiContext guiContext, ReferentialMapping referentialMapping) throws Exception {
        referentials = referentialMapping.getReferentialsByPreferenceId();

        gui = new ReferentialAssoForm(guiContext, referentials);
        JoinKeys joinSelection = new JoinKeys();
        joinSelection.addAssociation("referentialId", "referentialId");

        final SelectionGui selectionGui = gui.getSelectionGui();

        toTable = selectionGui.getToTable();
        fromTable = selectionGui.getFromTable();
        familyList = gui.getFamilyList();

        initFamilyList();
        initOkbutton();
        initToTable();
        initFromTable();

        SelectionLogic selectionLogic = new SelectionLogic(selectionGui, joinSelection,
                                                           new ReferentialRowFiller());
        selectionLogic.start();
        gui.initTableCellRenderers();
    }


    private void initFromTable() {
        filterModel = new TableFilter(fromTable.getModel()) {
            @Override
            public boolean containsFilterValue(int column, Object value) {
                return super.containsFilterValue(column, referentials.get(value).getTitle());
            }
        };
        fromTable.setModel(filterModel);

        gui.getFilterRefTextField().setName("filterRefTextField");
        gui.getPerformFilterButton().setName("performFilterButton");
        gui.getResetFilterButton().setName("resetFilterButton");

        gui.getPerformFilterButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });
        gui.getFilterRefTextField().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });
        gui.getResetFilterButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.getFilterRefTextField().setText("");
                filterModel.clearAllColumnFilter();
            }
        });
    }


    private void initToTable() {
        toTable.getDataSource().addPropertyChangeListener(CONTENT_PROPERTY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                toTable.sort();
            }
        });
    }


    private void initOkbutton() {
        gui.getOkButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                if (currentFamilyId != null) {
                    try {
                        toTable.getDataSource().save();
                    }
                    catch (RequestException ex) {
                        ErrorDialog.show(gui, "Erreur lors de la sauvegarde des référentiels", ex);
                    }
                }
                gui.dispose();
            }
        });
    }


    private void initFamilyList() {
        familyList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    try {
                        handleSelectionChange();
                    }
                    catch (RequestException e) {
                        ErrorDialog.show(gui,
                                         "Erreur lors du chargement des référentiels associés à la famille",
                                         e);
                    }
                }
            }
        });

        try {
            familyList.load();
        }
        catch (RequestException e) {
            ErrorDialog.show(gui, "Erreur lors du chargement des familles", e);
        }
    }


    private void handleSelectionChange() throws RequestException {
        if (currentFamilyId != null) {
            toTable.getDataSource().save();
        }
        if (familyList.getSelectedRow() != -1) {
            currentFamilyId = familyList.getSelectedFieldValue("familyId");
            if (currentFamilyId != null) {
                toTable.setSelector(new FieldsList("familyId", currentFamilyId));
                toTable.load();
                cleanToTable(); // On enlève les références à des référentiels qui auraient été supprimés et on trie le reste.
                fromTable.getSelectionModel().clearSelection();
                initFromTableDatasource();
                gui.getSelectionGui().getUnSelectButton().getAction().setEnabled(false);
                gui.getSelectionGui().getSelectButton().getAction().setEnabled(false);

                applyFilter();
            }
        }
    }


    private void applyFilter() {
        filterModel.setFilter(0, gui.getFilterRefTextField().getText(), true);
    }


    private void cleanToTable() throws RequestException {
        for (int idx = toTable.getDataSource().getRowCount() - 1; idx >= 0; idx--) {
            Row row = toTable.getDataSource().getRow(idx);
            if (referentials.get(row.getFieldValue("referentialId")) == null) {
                toTable.getDataSource().removeRow(row);
            }
        }
        toTable.sort();
    }


    private void initFromTableDatasource() throws RequestException {
        ListDataSource dataSource = fromTable.getDataSource();
        dataSource.clear();
        List<Referential> refs = new ArrayList<Referential>(referentials.values());
        Collections.sort(refs);
        for (Referential referential : refs) {
            Row row = new Row();
            row.addField("referentialId", referential.getPreferenceId());
            row.addField("referentialLabel", referential.getTitle());
            dataSource.addRow(row);
        }
    }


    ReferentialAssoForm getGui() {
        return gui;
    }


    private class ReferentialRowFiller implements RowFiller {

        public void fillAddedRow(Row row, int idx, ListDataSource lds) {
            row.addField("familyId", currentFamilyId);
        }
    }
}
