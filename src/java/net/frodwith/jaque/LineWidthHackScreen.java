package net.frodwith.jaque;

import java.io.IOException;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;

public class LineWidthHackScreen extends TerminalScreen {
  public int stripChars = 0;
  public LineWidthHackScreen(Terminal t) throws IOException {
    super(t);
  }
}
