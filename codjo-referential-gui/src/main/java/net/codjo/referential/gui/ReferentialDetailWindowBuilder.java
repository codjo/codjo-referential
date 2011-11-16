package net.codjo.referential.gui;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.action.DetailWindowBuilder;
import net.codjo.mad.gui.request.action.FatherContainer;
import net.codjo.referential.gui.api.Referential;
import java.util.Map;
import org.picocontainer.MutablePicoContainer;

public class ReferentialDetailWindowBuilder extends DetailWindowBuilder {
    private String frameTitle;
    private Referential referential;


    public ReferentialDetailWindowBuilder(FatherContainer fatherContainer,
                                   String frameTitle,
                                   Referential referential) {
        super(fatherContainer);
        this.frameTitle = frameTitle;
        this.referential = referential;
    }


    @Override
    protected void fillPicoContainer(MutablePicoContainer picoContainer,
                                     DetailDataSource detailDataSource,
                                     Preference preference) {
        super.fillPicoContainer(picoContainer, detailDataSource, preference);
        picoContainer.registerComponentInstance(frameTitle);
        picoContainer.registerComponentInstance(Map.class, referential.getFieldList());
        picoContainer.registerComponentInstance(referential);
    }
}