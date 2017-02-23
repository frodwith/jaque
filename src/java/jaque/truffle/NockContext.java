package jaque.truffle;

import jaque.noun.*;
import jaque.interpreter.*;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;

public final class NockContext {
  private Machine m;
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

  public final void endHint(Hint h, Noun product) {
    m = m.endHint(h, product);
  }

  public final Noun escape(Noun ref, Noun sam) {
    Result r = m.escape(ref, sam);
    m = r.m;
    return r.r;
  }

  public final Dashboard dashboard() {
    return m.dashboard();
  }

  public boolean fineCore(Cell core) {
    return dashboard().fine(core);
  }

  public final Jet findJet(Cell core, Atom axis) {
    return dashboard().find(core, axis);
  }

  public final Noun applyJet(Jet j, Cell core) {
    Result r = j.applyCore(m, core);
    this.m   = r.m;
    return r.r;
  }
  
  public CallTarget getKickTarget(Cell core, Atom axis) {
    KickLabel label = new KickLabel((Cell) core.p, axis);
    CallTarget target = kickRecord.get(label);
    if ( null == target ) {
      Cell c = (Cell) NockNode.fragment(axis, core);
      NockRootNode root = new NockRootNode(Formula.fromCell(c));
      target = Truffle.getRuntime().createCallTarget(root);
      kickRecord.put(label, target);
    }
    return target;
  }

}
