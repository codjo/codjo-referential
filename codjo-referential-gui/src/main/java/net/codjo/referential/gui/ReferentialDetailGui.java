package net.codjo.referential.gui;
import net.codjo.mad.gui.framework.AbstractDetailGui;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.ReferentialItemPanel;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;
import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JScrollPane;

public class ReferentialDetailGui extends AbstractDetailGui {
    private Map<String, Field> fieldMap = new LinkedHashMap<String, Field>();
    private ReferentialItemPanel referentialPanel = new ReferentialItemPanel();
    private GuiCustomizer customizer;


    public ReferentialDetailGui(String title, Map<String, Field> fieldMap, GuiCustomizer customizer)
          throws Exception {
        super(title);
        this.customizer = customizer;
        itemPanel = null;
        this.fieldMap = fieldMap;
        getContentPane().add(new JScrollPane(referentialPanel), BorderLayout.CENTER);
        referentialPanel.buildPanel(this.fieldMap.values(), customizer);
        this.initDefaultValues();
    }


    @Override
    protected void buildAndAddItems() {
    }


    @Override
    public void declareFields(DetailDataSource dataSource) {
        referentialPanel.declareFields(dataSource, fieldMap.values(), customizer);
    }


    @Override
    public void switchToUpdateMode() {
        referentialPanel.switchToUpdateMode(fieldMap.values());
    }


    public void initDefaultValues() {
        for (Field field : fieldMap.values()) {
            if (customizer != null && customizer.handle(field)) {
                customizer.initDefaultFieldValue(field);
            }
        }
    }
}
