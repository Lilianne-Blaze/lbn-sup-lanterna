# lbn-sup-lanterna

Support lib for Lanterna (tested with 3.1.1).
For now contains simple extension for ScrollingSwingTerminal that allows scrolling with mouse wheel and fixes some bugs.

See Demo1 in test packages for bugs and issues it fixes.

ScrollingSwingTerminal2 - useable as drop-in replacement
Adds constructor(fontSize,maxLines) - self-explanatory
Adds scrollToTop, scrollToBottom - self-explanatory
Adds scrollToX - scroll to any position 0..1
Adds setAutoScrollToBottom - scrolls to bottom on newlines (default true)
Adds forceRepaintOnNewline - fixes a bug where only the last line gets repainted (default true)
Adds getWriter - returns a PrintWriter wrapping the terminal
Adds forceRepaint - self-explanatory

ExposingScrollingSwingTerminal - to make subclassing easier
Adds getSwingTerminal, getScrollBar to access child components

TerminalWriter
Wraps a Terminal and allows it to be used via PrintWriter, works with other Terminals too
