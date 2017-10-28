package net.frodwith.jaque.prevayler;

import java.util.Date;
import java.util.HashMap;

import org.prevayler.Transaction;

import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Cell;

/* the boot sequence is special because it cannot be meaningfully interrupted;
* therefore a single transaction is used to set up the initial state on system
* creation.
 */

public class Boot implements Transaction<PrevalentSystem> {
  public HashMap<Cell,Location> locations;
  public Object arvo, who, now, wen, sen, sev;

  public Boot(HashMap<Cell,Location> locations, Object arvo, Object who, Object now, Object wen, Object sen, Object sev) {
    this.locations = locations;
    this.arvo = arvo;
    this.who = who;
    this.now = now;
    this.wen = wen;
    this.sen = sen;
    this.sev = sev;
  }

  @Override
  public void executeOn(PrevalentSystem s, Date now) {
    if ( s.context.locations != locations ) {
      s.context.locations.clear();
      s.context.locations.putAll(locations);
    }
    s.arvo = this.arvo;
    s.who = this.who;
    s.now = this.now;
    s.wen = this.wen;
    s.sev = this.sev;
    s.sen = this.sen;
  }

}
