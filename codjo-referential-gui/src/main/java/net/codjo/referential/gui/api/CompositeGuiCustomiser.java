package net.codjo.referential.gui.api;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

public class CompositeGuiCustomiser implements GuiCustomizer {
    private List<GuiCustomizer> guiCustomizerList = new ArrayList<GuiCustomizer>();


    public CompositeGuiCustomiser(GuiCustomizer... guiCustomizerList) {
        for (GuiCustomizer guiCustomizer : guiCustomizerList) {
            addCustomizer(guiCustomizer);
        }
    }


    public boolean handle(Field field) {
        return (this.findWrapper(field) != null);
    }


    public JComponent createGui(Field field) {
        GuiCustomizer wrapper = this.findWrapper(field);
        return (wrapper != null ? wrapper.createGui(field) : null);
    }


    public void declareField(DetailDataSource dataSource, Field field) {
        GuiCustomizer wrapper = this.findWrapper(field);
        if (wrapper != null) {
            wrapper.declareField(dataSource, field);
        }
    }


    public void initDefaultFieldValue(Field field) {
        GuiCustomizer wrapper = this.findWrapper(field);
        if (wrapper != null) {
            wrapper.initDefaultFieldValue(field);
        }
    }


    public void addCustomizer(GuiCustomizer guiCustomiser) {
        guiCustomizerList.add(guiCustomiser);
    }


    public void addFirstCustomizer(GuiCustomizer guiCustomiser) {
        guiCustomizerList.add(0, guiCustomiser);
    }


    public GuiCustomizer getCustomizer(Class<? extends GuiCustomizer> clazz) {
        if (clazz == null) {
            return null;
        }
        for (GuiCustomizer guiCustomizer : guiCustomizerList) {
            if (clazz.equals(guiCustomizer.getClass())) {
                return guiCustomizer;
            }
        }
        return null;
    }


    private GuiCustomizer findWrapper(Field field) {
        if (field == null) {
            return null;
        }
        for (GuiCustomizer guiCustomizer : guiCustomizerList) {
            if (guiCustomizer.handle(field)) {
                return guiCustomizer;
            }
        }
        return null;
    }


    int getNbCustomizers() {
        return guiCustomizerList.size();
    }
}
