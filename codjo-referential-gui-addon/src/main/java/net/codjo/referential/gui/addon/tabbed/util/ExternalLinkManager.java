package net.codjo.referential.gui.addon.tabbed.util;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.mad.gui.request.SelectionDataSource;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldBehavior;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

public class ExternalLinkManager {
    private static final Logger LOG = Logger.getLogger(ExternalLinkManager.class);
    private final Referential referential;
    private final GuiReferential guiReferential;
    private List<ComboBoxLoader> loaderList = new ArrayList<ComboBoxLoader>();


    protected ExternalLinkManager(final Referential referential, GuiReferential guiReferential) {
        this.referential = referential;
        this.guiReferential = guiReferential;
        guiReferential.accept(new GuiFieldBehavior() {
            public void visit(GuiFieldGroup group, GuiField guiField) {
                if (guiField.getHandlerId() != null) {
                    Field field = referential.getFieldList().get(guiField.getName());
                    if (field == null) {
                        throw new IllegalStateException("Le champ '" + guiField.getName()
                                                        + "' n'existe pas dans l'entité");
                    }
                    field.setHandlerId(guiField.getHandlerId());
                }
            }
        });
    }


    public static ExternalLinkManager getInstance(Referential referential, GuiReferential guiReferential) {
        return new ExternalLinkManager(referential, guiReferential);
    }


    public void connect(DetailDataSource detailDataSource) throws RequestException {
        for (Field field : referential.getFieldList().values()) {
            if (field.getHandlerId() != null) {
                final RequestComboBox comboBox = (RequestComboBox)field.getComponent();
                GuiField guiField = guiReferential.findFirstGuiField(field.getName());

                if (guiField.getMasterName() != null) {
                    Field masterField = referential.getField(guiField.getMasterName());
                    RequestComboBox masterComboBox = (RequestComboBox)masterField.getComponent();
                    masterComboBox.addItemListener(new MasterComboItemListener(comboBox));
                }
                ExternalLinkManager.ComboBoxLoader loader = new ComboBoxLoader(comboBox, guiField);
                loaderList.add(loader);
                detailDataSource.addPropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY, loader);
            }
        }
    }


    public void forceLoad(FieldsList fields) throws RequestException {
        for (ComboBoxLoader comboBoxLoader : loaderList) {
            comboBoxLoader.initSelector(fields);
            comboBoxLoader.loadComboBox();
        }
    }


    protected List<ComboBoxLoader> getLoaderList() {
        return loaderList;
    }


    protected class ComboBoxLoader implements PropertyChangeListener {
        private final RequestComboBox comboBox;
        private final GuiField guiField;
        private static final String SELECTOR_DELIM = ",";
        private static final int HEIGHT = 20;


        private ComboBoxLoader(RequestComboBox comboBox, GuiField guiField) {
            this.comboBox = comboBox;
            this.guiField = guiField;
        }


        public void propertyChange(PropertyChangeEvent evt) {
            Row selectedRow = ((SelectionDataSource)evt.getNewValue()).getRow();
            if (selectedRow != null) {
                initSelector(selectedRow);
            }
            loadComboBox();
        }


        public void initSelector(FieldsList fieldsList) {
            if (guiField.getSelector() != null) {
                FieldsList selector = new FieldsList();
                StringTokenizer selectTokenizer = new StringTokenizer(guiField.getSelector(), SELECTOR_DELIM);
                while (selectTokenizer.hasMoreTokens()) {
                    String name = selectTokenizer.nextToken();
                    String value = fieldsList.getFieldValue(name);
                    selector.addField(name, value);
                }
                ListDataSource dataSource = comboBox.getDataSource();
                dataSource.setSelector(selector);
            }
        }


        public void loadComboBox() {
            try {
                comboBox.getDataSource().setPageSize(Integer.MAX_VALUE);
                comboBox.setSortEnabled(guiField.isSort());
                if (guiField.getWidth() > 0) {
                    comboBox.setMinimumSize(new Dimension(guiField.getWidth(), HEIGHT));
                    comboBox.setPreferredSize(new Dimension(guiField.getWidth(), HEIGHT));
                }
                comboBox.load();
            }
            catch (RequestException e) {
                throw new IllegalStateException(e);
            }
        }


        public RequestComboBox getComboBox() {
            return comboBox;
        }


        public GuiField getGuiField() {
            return guiField;
        }
    }

    private static class MasterComboItemListener implements ItemListener {
        private final RequestComboBox slaveComboBox;


        private MasterComboItemListener(RequestComboBox slaveComboBox) {
            this.slaveComboBox = slaveComboBox;
        }


        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                slaveComboBox.getDataSource()
                      .setSelector(new FieldsList("hierView", (String)event.getItem()));
                try {
                    slaveComboBox.load();
                }
                catch (RequestException e) {
                    LOG.error("Erreur lors du chargement de la ComboBox : " + slaveComboBox.getName());
                }
            }
        }
    }
}
