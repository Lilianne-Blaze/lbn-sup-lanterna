package demo;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.terminal.swing.ScrollingSwingTerminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorColorConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorDeviceConfiguration;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import lombok.extern.slf4j.Slf4j;
import net.lilianneblaze.support.lanterna.ScrollingSwingTerminal2;
import net.lilianneblaze.support.lanterna.TerminalWriter;

@Slf4j
public class Demo1 {

    static ScrollingSwingTerminal oldTerminal;

    static ScrollingSwingTerminal2 newTerminal;

    // left : right
    static JSplitPane splitLR;

    // top left : bottom left
    static JSplitPane splitTLBL;

    // static JPanel buttons;
    static Box buttons;

    static JFrame frame;

    public static void main(String[] args) {
        log.info("main...");

        initTheme();
        initTerminals();
        initButtons();
        initFrame();

        frame.setVisible(true);

    }

    static void initTheme() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    static void initTerminals() {

        TerminalEmulatorDeviceConfiguration tedc = TerminalEmulatorDeviceConfiguration.getDefault();
        // default is 12
        SwingTerminalFontConfiguration stfc = SwingTerminalFontConfiguration.getDefaultOfSize(18);
        TerminalEmulatorColorConfiguration tecc = TerminalEmulatorColorConfiguration.getDefault();

        oldTerminal = new ScrollingSwingTerminal(tedc, stfc, tecc);

        // fails, at least on Lanterna 3.1.1
        // oldTerminal.putString("Old terminal ScrollingSwingTerminal\n");

        oldTerminal.enableSGR(SGR.BOLD);
        oldTerminal.putString("Old terminal ScrollingSwingTerminal");
        oldTerminal.putCharacter('\n');
        oldTerminal.putString("Will autoscroll, but only if already at the end.");
        oldTerminal.putCharacter('\n');
        oldTerminal.putString("Doesn't accept \\n chars in putString.");
        oldTerminal.putCharacter('\n');
        oldTerminal.putString("Note this is compiled and tested for Lanterna 3.1.1.");
        oldTerminal.putCharacter('\n');

        newTerminal = new ScrollingSwingTerminal2(18, 200);

        // newTerminal.setForceRepaintOnNewline(false);
        newTerminal.enableSGR(SGR.BOLD);
        newTerminal.putString("New terminal ScrollingSwingTerminal2\n");
        newTerminal.putString("Note the mouse wheel works here.\n");
        newTerminal.putString(
                "Force repaint on new line avoids a bug where only the last line is repainted when the buffer is full.\n");

    }

    static void initButtons() {
        // buttons = new JPanel();
        buttons = new Box(BoxLayout.Y_AXIS);

        JButton addSomeRandomLines = new JButton("Add some random lines");
        buttons.add(addSomeRandomLines);
        addSomeRandomLines.addActionListener((e) -> {
            cmdAddSomeRandomLines();
        });

        JButton scrollToTop = new JButton("Scroll to top");
        buttons.add(scrollToTop);
        scrollToTop.addActionListener((e) -> {
            newTerminal.scrollToTop();
        });

        JButton scrollToBottom = new JButton("Scroll to bottom");
        buttons.add(scrollToBottom);
        scrollToBottom.addActionListener((e) -> {
            newTerminal.scrollToBottom();
        });

        JCheckBox autoScrollToBottom = new JCheckBox("Auto scroll to bottom", newTerminal.isAutoScrollToBottom());
        buttons.add(autoScrollToBottom);
        autoScrollToBottom.addActionListener((e) -> {
            newTerminal.setAutoScrollToBottom(autoScrollToBottom.isSelected());
        });

        JCheckBox forceRepaintOnNewline = new JCheckBox("Force repaint on newline",
                newTerminal.isForceRepaintOnNewline());
        buttons.add(forceRepaintOnNewline);
        forceRepaintOnNewline.addActionListener((e) -> {
            newTerminal.setForceRepaintOnNewline(forceRepaintOnNewline.isSelected());
        });

    }

    static void initFrame() {
        frame = new JFrame("Demo1");
        frame.setSize(1100, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        splitLR = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitLR.setDividerLocation(0.67d);
        splitLR.setResizeWeight(0.67d);

        splitTLBL = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitTLBL.setDividerLocation(0.5d);
        splitTLBL.setResizeWeight(0.5d);

        splitTLBL.add(oldTerminal);
        splitTLBL.add(newTerminal);
        splitLR.add(splitTLBL);
        frame.add(splitLR);
        splitLR.add(buttons);

    }

    static void cmdAddSomeRandomLines() {
        log.debug("cmdAddSomeRandomLines...");
        PrintWriter pwo = new PrintWriter(new TerminalWriter(oldTerminal));
        PrintWriter pwn = newTerminal.getWriter();

        int lineCount = (int) (Math.random() * 30 + 1);

        for (int i = 0; i < lineCount; i++) {

            String s = "" + ZonedDateTime.now() + " line " + i;
            pwo.println(s);
            pwn.println(s);
        }
    }

}
