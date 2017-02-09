package jaque.truffle;

import jaque.noun.*;
import jaque.interpreter.*;

import com.oracle.truffle.api.TruffleLanguage;

public final class NockContext {
  private Machine m;
  private final TruffleLanguage.Env env;

  public NockContext(TruffleLanguage.Env env, Machine m) {
    this.env = env;
    this.m   = m;
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
}
