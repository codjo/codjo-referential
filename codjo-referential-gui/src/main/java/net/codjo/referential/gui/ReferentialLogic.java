package net.codjo.referential.gui;

import java.awt.Dimension;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JTree;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.Sender;
import net.codjo.mad.gui.request.GuiLogic;
import net.codjo.referential.gui.api.ListGuiCustomizer;
import net.codjo.referential.gui.api.Referential;
import net.codjo.referential.gui.api.ReferentialFrameCustomizer;
import net.codjo.referential.gui.api.TreeGuiCustomizer;

public class ReferentialLogic implements GuiLogic<ReferentialGui> {
    private ReferentialGui referentialGui;
    private Map<String, Set<Referential>> referentialFamilyList = new TreeMap<String, Set<Referential>>();
    private DefaultReferentialListGui referentialListGui;
    public static final String DEFAULT_FAMILY = "zzz-Autres";
    private Sender sender;
    private ListGuiCustomizer listGuiCustomizer;
    private ReferentialFrameCustomizer referentialFrameCustomizer;
    private WaitingPanel waitingPanel = null;


    ReferentialLogic(GuiContext guiContext,
                     ReferentialMapping referentialMapping,
                     ListGuiCustomizer listGuiCustomizer,
                     TreeGuiCustomizer treeGuiCustomizer,
                     ReferentialFrameCustomizer referentialFrameCustomizer,
                     boolean waitingPanel,
                     Dimension preferredSize)
          throws Exception {
        this.listGuiCustomizer = listGuiCustomizer;
        this.referentialFrameCustomizer = referentialFrameCustomizer;
        this.referentialGui = new ReferentialGui(guiContext, "Gestion des référentiels", preferredSize);
        sender = guiContext.getSender();
        initGuiListener();
        initListGui(guiContext, referentialMapping, treeGuiCustomizer, waitingPanel);
    }


    private void initListGui(GuiContext guiContext,
                             ReferentialMapping referentialMapping,
                             TreeGuiCustomizer treeGuiCustomizer,
                             boolean isWaitingPanelActivated)
          throws Exception {
        this.referentialListGui = new DefaultReferentialListGui(guiContext);
        referentialGui.addReferentialPanel(referentialListGui);
        buildFamilyList(referentialMapping.getReferentialsByPreferenceId());
        if (!referentialFamilyList.isEmpty()) {
            referentialGui.fillFamilyTree(referentialFamilyList);
        }
        treeGuiCustomizer.init(referentialGui);
        if (isWaitingPanelActivated) {
            initWaitingPanel();
        }
    }


    private void initWaitingPanel() {
        waitingPanel = new WaitingPanel();
        waitingPanel.setDelayBeforeAnimation(0);
        waitingPanel.setName("waitingPanel");
        referentialGui.getRootPane().setGlassPane(waitingPanel);
        waitingPanel.setVisible(false);
    }


    private void buildFamilyList(Map<String, Referential> referentialList) throws RequestException {
        Set<Referential> unaffectedReferentials = new TreeSet<Referential>(referentialList.values());

        FieldsList selector = new FieldsList();
        selector.addField("familyLabel", "familyLabel");
        selector.addField("referentialId", "referentialId");
        Result result = execute("selectAllPmRefFamilyRefAsso", selector);

        if (result != null && result.getRows() != null) {
            for (Row row : result.getRows()) {
                String familyLabel = row.getFieldValue("familyLabel");
                String referentialId = row.getFieldValue("referentialId");
                Referential referential = referentialList.get(referentialId);
                if (referential != null) {
                    // On ignore les référentiels inconnus dans le cas ou ils auraient été supprimés
                    unaffectedReferentials.remove(referential);
                    Set<Referential> familyReferentials = referentialFamilyList.get(familyLabel);
                    if (familyReferentials == null) {
                        familyReferentials = new TreeSet<Referential>();
                        referentialFamilyList.put(familyLabel, familyReferentials);
                    }
                    familyReferentials.add(referential);
                }
            }
        }
        referentialFamilyList.put(DEFAULT_FAMILY, unaffectedReferentials);
    }


    private Result execute(String handlerId, FieldsList selector) throws RequestException {
        SelectRequest select = new SelectRequest(handlerId, selector);
        select.setPage(1, 1000000);
        return sender.send(select);
    }


    protected boolean isLastSelectionALeaf(DefaultMutableTreeNode node) {
        if (node == null) {
            return false;
        }
        Object userObject = node.getUserObject();
        return userObject != null && userObject instanceof Referential;
    }


    private void initGuiListener() {
        final JTree tree = referentialGui.getReferentialTree();
        final GuiContext ctx = referentialGui.getGuiContext();
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent event) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if (isLastSelectionALeaf(node)) {
                    Object userObject = node.getUserObject();
                    Referential referential = (Referential)userObject;
                    handleReferentialChange(referential, ctx);
                }
            }
        });

        referentialGui.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
                if (referentialFrameCustomizer != null) {
                    referentialFrameCustomizer.handleFrameOpen(ctx, referentialListGui);
                }
            }


            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                if (referentialFrameCustomizer != null) {
                    referentialFrameCustomizer.handleFrameClose(ctx, referentialListGui);
                }
            }
        });
    }


    private void handleReferentialChange(Referential referential, final GuiContext ctx) {
        referentialListGui.create();
        listGuiCustomizer.handleListClose(referentialListGui);
        referentialListGui.setTitle(referential.getTitle());
        referentialListGui.init(referential.getPreference(), referential);
        listGuiCustomizer.handleListOpen(referential, referentialListGui);

        if (waitingPanel != null) {
            waitingPanel.exec(new Runnable() {
                public void run() {
                    doLoad(ctx);
                }
            });
        }
        else {
            doLoad(ctx);
        }

        referentialListGui.repaint();
    }


    private void doLoad(GuiContext ctx) {
        try {
            getListGui().load();
        }
        catch (RequestException e) {
            ErrorDialog.show(ctx.getMainFrame(), "Impossible d'afficher le référentiel.", e);
        }
    }


    public ReferentialGui getGui() {
        return referentialGui;
    }


    public DefaultReferentialListGui getListGui() {
        return referentialListGui;
    }


    public Map<String, Set<Referential>> getReferentialFamilyList() {
        return referentialFamilyList;
    }
}
