package net.codjo.referential.gui.addon.tabbed;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.GuiLogic;
import net.codjo.mad.gui.request.util.ButtonPanelGui;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import net.codjo.referential.gui.addon.tabbed.field.EditableFieldBehavior;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.addon.tabbed.util.ExternalLinkManager;
import net.codjo.referential.gui.addon.tabbed.util.RequiredFieldValidator;
import net.codjo.referential.gui.api.Referential;
import org.apache.log4j.Logger;

public abstract class AbstractTabbedPaneDetailLogic<T extends TabbedPaneDetailGui> implements GuiLogic<T> {
    private static final Logger LOG = Logger.getLogger(AbstractTabbedPaneDetailLogic.class);
    private T gui;
    private DetailDataSource detailDataSource;
    private Referential referential;
    private GuiReferential guiReferential;
    protected ExternalLinkManager linkManager;


    protected AbstractTabbedPaneDetailLogic(DetailDataSource dataSource, Referential referential) {
        setReferential(referential);
        setDetailDataSource(dataSource);
    }


    public DetailDataSource getDetailDataSource() {
        return detailDataSource;
    }


    public void setDetailDataSource(DetailDataSource detailDataSource) {
        this.detailDataSource = detailDataSource;
        initEntityName(detailDataSource);
    }


    public Referential getReferential() {
        return referential;
    }


    public void setReferential(Referential referential) {
        this.referential = referential;
    }


    public GuiReferential getGuiReferential() throws Exception {
        if (guiReferential == null) {
            guiReferential = createGuiConfiguration(referential);
        }
        return guiReferential;
    }


    public void setGuiReferential(GuiReferential guiReferential) {
        this.guiReferential = guiReferential;
    }


    public T getGui() throws Exception {
        if (gui == null) {
            checkDatas();
            initLinkManager(referential, getGuiReferential());

            gui = createGui();
            prepareDatasource();
            getGuiReferential().accept(new EditableFieldBehavior(referential));

            gui.setGuiContext(detailDataSource.getGuiContext());
            gui.setDesktopPane(detailDataSource.getGuiContext().getDesktopPane());
            gui.declareFields(detailDataSource);

            if (detailDataSource.getLoadFactory() != null) {
                gui.switchToUpdateMode();
            }

            final ButtonPanelLogic buttonPanelLogic = createButtonPanelLogic(gui.getButtonPanelGui());
            buttonPanelLogic.setMainDataSource(detailDataSource);
            buttonPanelLogic.setButtonLogicValidator(new RequiredFieldValidator(detailDataSource));
            loadFields();
        }
        return gui;
    }


    protected ButtonPanelLogic createButtonPanelLogic(ButtonPanelGui buttonPanelGui) {
        return new ButtonPanelLogic(buttonPanelGui);
    }


    protected abstract GuiReferential createGuiConfiguration(Referential ref) throws Exception;


    protected abstract T createGui() throws Exception;


    protected void initLinkManager(Referential ref, GuiReferential guiRef) {
        linkManager = ExternalLinkManager.getInstance(ref, guiRef);
    }


    protected void loadFields() throws RequestException {
        detailDataSource.load();
        if (detailDataSource.getLoadResult() == null) {
            getLinkManager().forceLoad(new FieldsList());
        }
    }


    protected void prepareDatasource() throws RequestException {
        getLinkManager().connect(detailDataSource);
    }


    protected String retrieveEntityName() {
        return referential.getPreference().getEntity();
    }


    protected ExternalLinkManager getLinkManager() {
        return linkManager;
    }


    private void initEntityName(DetailDataSource dataSource) {
        try {
            if (dataSource != null) {
                dataSource.setEntityName(retrieveEntityName());
            }
        }
        catch (IllegalArgumentException e) {
            LOG.info("impossible de récupérer la préférence du référentiel.");
        }
    }


    private void checkDatas() throws Exception {
        assertNotNull("datasource non positionné", detailDataSource);
        assertNotNull("referential non positionné", referential);
        assertNotNull("guiReferential non positionné", getGuiReferential());
    }


    private static void assertNotNull(String message, Object object) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
