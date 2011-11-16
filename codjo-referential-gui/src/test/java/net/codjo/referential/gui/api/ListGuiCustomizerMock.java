package net.codjo.referential.gui.api;
import net.codjo.test.common.LogString;
/**
 *
 */
public class ListGuiCustomizerMock implements ListGuiCustomizer {
    private LogString log = new LogString();


    public ListGuiCustomizerMock() {
    }


    public ListGuiCustomizerMock(LogString log) {
        this.log = log;
    }


    public void handleListOpen(Referential referential, ReferentialListGui listGui) {
        log.call("handleListOpen", "ref<" + referential.getTitle() + ">", "list<" + listGui.getTitle() + ">");
    }


    public void handleListClose(ReferentialListGui listGui) {
        log.call("handleListClose", "list<" + listGui.getTitle() + ">");
    }
}
