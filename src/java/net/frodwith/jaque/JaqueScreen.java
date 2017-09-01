package net.frodwith.jaque;

import java.io.IOException;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;

/* In addition to being the Lanterna screen, I'm keeping all the
 * state here because Java has state and it's very handy in this case.
 */

public class JaqueScreen extends TerminalScreen {
  public int stripChars = 0;
  public String lastLine;
  public Object lastHop;
  public boolean active = true;
  public boolean init = false;
  private int spinCounter = 0;
  private final static char[] spinChars = { '|', '/', '-', '\\' };

  public JaqueScreen(Terminal t) throws IOException {
    super(t);
  }
  
  public char getSpinChar() {
    char c = spinChars[spinCounter];
    spinCounter = (spinCounter + 1) % spinChars.length;
    return c;
  }
  
  public void shutdown() throws IOException {
    this.close();
    this.active = false;
  }
}
