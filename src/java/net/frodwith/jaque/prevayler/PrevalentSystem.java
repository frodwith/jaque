package net.frodwith.jaque.prevayler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

import org.prevayler.Prevayler;

import net.frodwith.jaque.Caller;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.truffle.Context;

public class PrevalentSystem implements Serializable, Caller {
  public Object arvo;
  public Object now;
  public Object wen;
  public Object sev;
  public Object sen;
  public HashMap<Cell, Location> locations;

  // used to facilitate externalCall trapping jet registrations
  public transient Queue<Registration> registrations = null;

  // must be set by a Wake when the process starts up
  public transient Context context;
  public transient Consumer<Object> slogSink, effectSink;
  
  public Object externalCall(Prevayler<PrevalentSystem> prevayler, String gateName, Object sample) {
    this.registrations = new LinkedList<Registration>();
    Object product = kernel(gateName, sample);
    for ( Registration r : this.registrations ) {
      prevayler.execute(r);
    }
    this.registrations = null;
    return product;
  }
  
  public void restoreLocations(Map<Cell,Location> from) {
    locations = context.locations;
    if ( null != from && locations != from ) {
      locations.clear();
      locations.putAll(from);
    }
  }

  public Cell axisGate(Object axis) {
    return Cell.expect(context.nock(arvo, new Qual(9L, axis, 0L, 1L).toCell()));
  }
  
  @Override
  public Object kernel(String gateName, Object sample) {
    Cell wishGate = axisGate(20L);
    Object cord = Atom.stringToCord(gateName);
    Cell targetGate = Cell.expect(context.wrapSlam(wishGate, cord));
    return context.wrapSlam(targetGate, sample);
  }
  
  @Override
  public void slog(Object tank) {
    this.slogSink.accept(tank);
  }

  @Override
  public void register(Cell battery, Location location) {
    if ( null != this.registrations ) {
      this.registrations.add(new Registration(battery, location));
    }
  }
}
