package net.codjo.referential.gui.api;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.test.common.LogString;
/**
 *
 */
public class ReferentialFrameCustomizerMock implements ReferentialFrameCustomizer {
    private LogString log;


    public ReferentialFrameCustomizerMock(LogString log) {
        this.log = log;
    }


    public void handleFrameOpen(GuiContext context, ReferentialListGui listGui) {
        log.call("handleFrameOpen",
                 String.format("context<%s>, listGui<%s>", context.getClass().getName(), listGui.getTitle()));
    }


    public void handleFrameClose(GuiContext context, ReferentialListGui listGui) {
        log.call("handleFrameClose",
                 String.format("context<%s>, listGui<%s>", context.getClass().getName(), listGui.getTitle()));
    }
}
