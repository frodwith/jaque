package net.frodwith.jaque.prevayler;

import java.util.Date;

import org.prevayler.Transaction;

import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Cell;

public class Registration implements Transaction<PrevalentSystem> {
  public Cell battery;
  public Location location;
  
  public Registration(Cell battery, Location location) {
    this.battery = battery;
    this.location = location;
  }

  @Override
  public void executeOn(PrevalentSystem s, Date now) {
    s.context.locations.put(battery, location);
  }

}
