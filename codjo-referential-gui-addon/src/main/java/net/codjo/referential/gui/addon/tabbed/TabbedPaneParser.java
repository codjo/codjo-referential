package net.codjo.referential.gui.addon.tabbed;
import net.codjo.referential.gui.addon.tabbed.field.GuiField;
import net.codjo.referential.gui.addon.tabbed.field.GuiFieldGroup;
import net.codjo.referential.gui.addon.tabbed.field.GuiReferential;
import net.codjo.referential.gui.addon.tabbed.field.RelatedGuiField;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TabbedPaneParser {

    public TabbedPaneParser() {
    }


    public TabbedPaneConfiguration parse(InputStream input) throws FileNotFoundException {
        if (input == null) {
            throw new FileNotFoundException("Pointeur null");
        }

        return (TabbedPaneConfiguration)createXstream().fromXML(input);
    }


    public TabbedPaneConfiguration fromXml(String xml) {
        return (TabbedPaneConfiguration)createXstream().fromXML(xml);
    }


    public String toXml(TabbedPaneConfiguration configuration) {
        return createXstream().toXML(configuration);
    }


    public static void customizeXStream(XStream xstream) {
        xstream.alias("configuration", TabbedPaneConfiguration.class);
        xstream.alias("referential", GuiReferential.class);
        xstream.alias("group", GuiFieldGroup.class);
        xstream.alias("field", GuiField.class);
        xstream.alias("relatedField", RelatedGuiField.class);

        xstream.addImplicitCollection(TabbedPaneConfiguration.class, "referentials");
        xstream.addImplicitCollection(GuiReferential.class, "groups");
        xstream.addImplicitCollection(GuiFieldGroup.class, "guiFields");
        xstream.addImplicitCollection(GuiField.class, "relatedGuiFields");
    }


    protected XStream createXstream() {
        XStream xstream = new XStream(new DomDriver());

        customizeXStream(xstream);

        return xstream;
    }
}
