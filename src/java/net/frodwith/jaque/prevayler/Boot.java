package net.frodwith.jaque.prevayler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;

import org.prevayler.Transaction;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.Location;

/* the boot sequence is special because it cannot be meaningfully interrupted;
* therefore a single transaction is used to set up the initial state on system
* creation.
 */

public class Boot implements Transaction<PrevalentSystem> {
  public Object arvo, now, wen, sen, sev;
  public HashMap<Cell,Location> locations;

  public Boot(HashMap<Cell,Location> locations, Object arvo, Object now, Object wen, Object sen, Object sev) {
    this.locations = locations;
    this.arvo = arvo;
    this.now = now;
    this.wen = wen;
    this.sen = sen;
    this.sev = sev;
  }

  @Override
  public void executeOn(PrevalentSystem s, Date now) {
    s.arvo = this.arvo;
    s.now = this.now;
    s.wen = this.wen;
    s.sev = this.sev;
    s.sen = this.sen;
  }

}
