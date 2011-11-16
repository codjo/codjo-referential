package net.codjo.referential.gui.api;
import net.codjo.mad.gui.framework.GuiContext;

/**
 *
 */
public interface ReferentialFrameCustomizer {
    void handleFrameOpen(GuiContext context, ReferentialListGui listGui);


    void handleFrameClose(GuiContext context, ReferentialListGui listGui);
}