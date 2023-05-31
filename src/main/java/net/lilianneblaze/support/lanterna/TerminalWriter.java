package net.lilianneblaze.support.lanterna;

import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.io.Writer;

public class TerminalWriter extends Writer {

    protected Terminal terminal;

    public TerminalWriter(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        boolean needsFlush = false;
        for (int i = off; i < off + len; i++) {
            char ch = cbuf[i];
            terminal.putCharacter(cbuf[i]);
            if (ch == '\n') {
                needsFlush = true;
            }
        }
        if (needsFlush) {
            terminal.flush();
        }
    }

    @Override
    public void flush() throws IOException {
        terminal.flush();
    }

    @Override
    public void close() throws IOException {
    }

}
