package net.lilianneblaze.support.lanterna;

import com.googlecode.lanterna.terminal.swing.ScrollingSwingTerminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorColorConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorDeviceConfiguration;
import java.lang.reflect.Field;
import javax.swing.JScrollBar;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExposingScrollingSwingTerminal extends ScrollingSwingTerminal {

    public ExposingScrollingSwingTerminal() {
        this(TerminalEmulatorDeviceConfiguration.getDefault(), SwingTerminalFontConfiguration.getDefault(),
                TerminalEmulatorColorConfiguration.getDefault());
    }

    public ExposingScrollingSwingTerminal(TerminalEmulatorDeviceConfiguration deviceConfiguration) {
        this(deviceConfiguration, SwingTerminalFontConfiguration.getDefault(),
                TerminalEmulatorColorConfiguration.getDefault());
    }

    public ExposingScrollingSwingTerminal(TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration, TerminalEmulatorColorConfiguration colorConfiguration) {
        super(deviceConfiguration, fontConfiguration, colorConfiguration);
    }

    public SwingTerminal getSwingTerminal() {
        try {
            Class c = ScrollingSwingTerminal.class;
            Field f = c.getDeclaredField("swingTerminal");
            f.setAccessible(true);
            return (SwingTerminal) f.get(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public JScrollBar getScrollBar() {
        try {
            Class c = ScrollingSwingTerminal.class;
            Field f = c.getDeclaredField("scrollBar");
            f.setAccessible(true);
            return (JScrollBar) f.get(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
