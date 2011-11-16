package net.codjo.referential.gui.api;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;

public abstract class AbstractGuiCustomizer implements GuiCustomizer {

    public void declareField(DetailDataSource dataSource, Field field) {
        dataSource.declare(field.getName(), field.getComponent());
        String defaultValue = field.getDefaultValue();
        if (defaultValue != null) {
            dataSource.setDefaultValue(field.getName(), defaultValue);
        }
    }
}
