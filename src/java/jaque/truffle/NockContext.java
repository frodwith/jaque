package jaque.truffle;

import jaque.noun.*;
import jaque.interpreter.*;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;

public final class NockContext {
  public Machine m;
  private Map<KickLabel,CallTarget> kickRecord;

  public NockContext(Machine m) {
    this.m   = m;
    this.kickRecord = new HashMap<KickLabel, CallTarget>();
  }

  public final Noun startHint(Hint h) {
    Result r = m.startHint(h);
    m = r.m;
    return r.r;
  }

  public final void endHint(Hint h, Object product) {
    m = m.endHint(h, product);
  }

  public final Noun escape(Noun ref, Noun sam) {
    Result r = m.escape(ref, sam);
    m = r.m;
    return r.r;
  }
  
  public final void declare(Cell core, Object clue) {
    m = m.declare(core, clue);
  }

  public boolean fine(Cell core) {
    return m.fine(core);
  }

  public final Jet find(Cell core, Atom axis) {
    return m.find(core, axis);
  }

  public final Object apply(Jet j, Noun subject) {
    Result r = j.apply(m, subject);
    m = r.m;
    return r.r;
  }
  
  public CallTarget getKickTarget(Cell core, Atom axis) {
    KickLabel label = new KickLabel((Cell) core.getHead(), axis);
    CallTarget target = kickRecord.get(label);
    if ( null == target ) {
      Cell c = (Cell) Interpreter.fragment(axis, core);
      NockRootNode root = new NockRootNode(Formula.fromCell(c));
      target = Truffle.getRuntime().createCallTarget(root);
      kickRecord.put(label, target);
    }
    return target;
  }
  
  private class KickLabel {
    public final Cell battery;
    public final Atom axis;
    
    public KickLabel(Cell battery, Atom axis) {
      this.battery = battery;
      this.axis = axis;
    }
    
    public int hashCode() {
      return battery.hashCode() ^ axis.hashCode();
    }
    
    public boolean equals(Object o) {
      KickLabel l = (KickLabel) o;
      return battery.equals(l.battery) && axis.equals(l.axis);
    }
  }
}
