package net.lilianneblaze.support.lanterna;

import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorColorConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorDeviceConfiguration;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import javax.swing.JScrollBar;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScrollingSwingTerminal2 extends ExposingScrollingSwingTerminal {

    @Setter
    @Getter
    protected boolean autoScrollToBottom = true;

    @Setter
    @Getter
    protected boolean forceRepaintOnNewline = true;

    private boolean reflectionProblemsLogged = false;

    public ScrollingSwingTerminal2() {
        this(TerminalEmulatorDeviceConfiguration.getDefault(), SwingTerminalFontConfiguration.getDefault(),
                TerminalEmulatorColorConfiguration.getDefault());
    }

    /**
     * 
     * @param fontSize
     *            default 12.
     * @param maxLines
     *            default 2000.
     */

    public ScrollingSwingTerminal2(int fontSize, int maxLines) {
        this(TerminalEmulatorDeviceConfiguration.getDefault().withLineBufferScrollbackSize(maxLines),
                SwingTerminalFontConfiguration.getDefaultOfSize(fontSize),
                TerminalEmulatorColorConfiguration.getDefault());
    }

    public ScrollingSwingTerminal2(TerminalEmulatorDeviceConfiguration deviceConfiguration) {
        this(deviceConfiguration, SwingTerminalFontConfiguration.getDefault(),
                TerminalEmulatorColorConfiguration.getDefault());
    }

    public ScrollingSwingTerminal2(TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration, TerminalEmulatorColorConfiguration colorConfiguration) {
        super(deviceConfiguration, fontConfiguration, colorConfiguration);
        init();
    }

    public PrintWriter getWriter() {
        return new PrintWriter(new TerminalWriter(this));
    }

    public void scrollToTop() {
        getScrollBar().setValue(0);
    }

    public void scrollToBottom() {
        getScrollBar().setValue(getScrollBar().getMaximum());
    }

    /**
     * 
     * @param x
     *            0.0 - 1.0
     */
    public void scrollToX(double x) {
        int max = getScrollBar().getMaximum();
        int newValue = (int) (max * x);
        getScrollBar().setValue(newValue);
    }

    public void forceRepaint() {
        try {
            SwingTerminal st = getSwingTerminal();

            Field SwingTerminal_terminalImplementation = st.getClass().getDeclaredField("terminalImplementation");
            SwingTerminal_terminalImplementation.setAccessible(autoScrollToBottom);
            Object terminalImplementation = SwingTerminal_terminalImplementation.get(st);

            Class class_GraphicalTerminalImplementation = terminalImplementation.getClass().getSuperclass();

            Field GraphicalTerminalImplementation_virtualTerminal = class_GraphicalTerminalImplementation
                    .getDeclaredField("virtualTerminal");
            GraphicalTerminalImplementation_virtualTerminal.setAccessible(autoScrollToBottom);

            Object virtualTerminal = GraphicalTerminalImplementation_virtualTerminal.get(terminalImplementation);

            Field GraphicalTerminalImplementation_needFullRedraw = class_GraphicalTerminalImplementation
                    .getDeclaredField("needFullRedraw");
            GraphicalTerminalImplementation_needFullRedraw.setAccessible(true);

            GraphicalTerminalImplementation_needFullRedraw.set(terminalImplementation, true);

            repaint();
        } catch (Exception e) {
            if (!reflectionProblemsLogged) {
                String s = "Shouldn't happen. If it does either the implementation changed or reflection is not allowed. Either way ignore silently.";
                log.warn(s, e);
                reflectionProblemsLogged = true;
            }
        }
    }

    @Override
    public void putString(String str) {
        if (str.indexOf('\n') == -1) {
            super.putString(str);
        } else {
            // note ScrollingSwingTerminal 3.1.1 supports newlines but only as chars, not in Strings
            for (char ch : str.toCharArray()) {
                putCharacter(ch);
            }
        }
    }

    @Override
    public void putCharacter(char c) {
        super.putCharacter(c);
        if (c == '\n') {
            handleNewline();
        }
    }

    protected void handleNewline() {
        if (autoScrollToBottom) {
            scrollToBottom();
        }
        if (forceRepaintOnNewline) {
            forceRepaint();
        }
    }

    /**
     * During tests scroll amount was 3, so the default implementation causes mouse wheel to scroll 30% of the visible
     * part at one time.
     */
    protected double getScrollAmount() {
        int scrollFactor = 10;
        double d = getSwingTerminal().getHeight();
        return d / scrollFactor;
    }

    private void init() {
        SwingTerminal st = getSwingTerminal();
        JScrollBar sb = getScrollBar();

        st.addMouseWheelListener(this::mouseWheelMoved);
        sb.addMouseWheelListener(this::mouseWheelMoved);
    }

    private void mouseWheelMoved(MouseWheelEvent e) {

        // Font font = new Font("Serif", Font.PLAIN, 14);
        // BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        // FontMetrics fm = img.getGraphics().getFontMetrics(font);
        // int height = fm.getHeight();

        // negative = up
        double rotations = e.getPreciseWheelRotation();
        int eventScrollAmount = e.getScrollAmount();

        int valueDelta = (int) (rotations * eventScrollAmount * getScrollAmount());

        JScrollBar sbar = getScrollBar();
        sbar.setValue(sbar.getValue() + valueDelta);

    }
}
