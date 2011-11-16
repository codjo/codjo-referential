package net.codjo.referential.gui.api;
import net.codjo.test.common.LogString;
/**
 *
 */
public class TreeGuiCustomizerMock implements TreeGuiCustomizer {
    private LogString log = new LogString();


    public TreeGuiCustomizerMock(LogString log) {
        this.log = log;
    }


    public void init(ReferentialTreeGui referentialGui) {
        log.call("init", "tree<" + referentialGui.getTitle() + ">");
    }
}
