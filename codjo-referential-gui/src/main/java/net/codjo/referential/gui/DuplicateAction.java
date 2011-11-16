/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.referential.gui;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.LoadManager;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.action.AddAction;
import net.codjo.mad.gui.request.action.DetailWindowBuilder;
import net.codjo.mad.gui.request.event.DataSourceSupport;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 *
 */
public class DuplicateAction extends AddAction {
    private final GuiContext guiContext;
    public final static String ACTION_NAME = "DuplicateAction";


    public DuplicateAction(GuiContext guiContext, RequestTable requestTable,
                           DetailWindowBuilder detailWindowBuilder) {
        super(guiContext, requestTable, detailWindowBuilder);
        this.guiContext = guiContext;
        setEnabled(false);
        ListSelectionModel selectionModel = getTable().getSelectionModel();
        selectionModel.addListSelectionListener(new TableSelectionListener(this));
        initAction();
    }


    protected boolean canDuplicate() {
        if (getTable().isEditable()) {
            return false;
        }
        ListSelectionModel lsm = getTable().getSelectionModel();
        return !lsm.isSelectionEmpty() && getTable().getSelectedRowCount() == 1
               && getPreference().getDetailWindowClass() != null
               && getPreference().getSelectByPk() != null;
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled && canDuplicate());
    }


    protected void initAction() {
        putValue(NAME, "Dupliquer");
        putValue(ID_PROPERTY, "DuplicateAction");
        putValue(SHORT_DESCRIPTION, "Dupliquer l'enregistrement sélectionné");
        putValue(SMALL_ICON, UIManager.getIcon("mad.pageCopy"));
    }


    @Override
    protected DetailDataSource newDetailDataSource() {
        final DetailDataSource dataSource = getTable().getSelectedRowDataSource(guiContext);
        final LoadManager wrapper = dataSource.getLoadManager();

        dataSource.setLoadManager(new LoadManager() {
            public void doLoad(DataSourceSupport support) throws RequestException {
                dataSource.setLoadFactory(getPreference().getSelectByPk());
                wrapper.doLoad(support);
                dataSource.setLoadFactory(null);
            }


            public void addLoadRequestTo(MultiRequestsHelper helper) {
                wrapper.addLoadRequestTo(helper);
            }
        });

        dataSource.setInsertFactory(getPreference().getInsert());
        dataSource.setLoadFactory(null);
        return dataSource;
    }


    private class TableSelectionListener implements ListSelectionListener {
        private DuplicateAction duplicateAction;


        TableSelectionListener(DuplicateAction duplicateAction) {
            this.duplicateAction = duplicateAction;
        }


        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            duplicateAction.setEnabled(true);
        }
    }
}
