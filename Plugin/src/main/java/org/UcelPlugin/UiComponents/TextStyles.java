package org.UcelPlugin.UiComponents;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

public class TextStyles {

    private final static StyleContext CTX = StyleContext.getDefaultStyleContext();
    public final static AttributeSet DEFAULT = CTX.addAttribute(CTX.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
    public final static AttributeSet RED = CTX.addAttribute(CTX.getEmptySet(), StyleConstants.Foreground, Color.RED);
    public final static AttributeSet YELLOW = CTX.addAttribute(CTX.getEmptySet(), StyleConstants.Foreground, Color.YELLOW);
    public final static AttributeSet DARK_YELLOW = CTX.addAttribute(CTX.getEmptySet(), StyleConstants.Foreground, new Color(126, 126, 0));
    public final static AttributeSet GREEN = CTX.addAttribute(CTX.getEmptySet(), StyleConstants.Foreground, Color.GREEN);
    public final static AttributeSet DARK_GREEN = CTX.addAttribute(CTX.getEmptySet(), StyleConstants.Foreground, new Color(0, 120, 0));
}
