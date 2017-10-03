package net.frodwith.jaque.prevayler;

import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.prevayler.TransactionWithQuery;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Time;
import net.frodwith.jaque.truffle.driver.Arm;

public class Wake implements TransactionWithQuery<PrevalentSystem,Boolean> {
  public Arm[] arms;
  public transient Consumer<Object> slogSink;
  public transient Consumer<Object> effectSink;
  public boolean profile;

  private final static Logger logger = Logger.getGlobal();
  private static Object
    DA = Atom.mote("da"),
    UV = Atom.mote("uv");

  public Wake(Arm[] arms, Consumer<Object> slogSink, Consumer<Object> effectSink, boolean profile) {
    this.arms = arms;
    this.slogSink = slogSink;
    this.effectSink = effectSink;
    this.profile = profile;
  }
  
  @Override
  public Boolean executeAndQuery(PrevalentSystem s, Date now) {
    if ( null == slogSink ) {
      profile = false;
    }
    s.slogSink = slogSink;
    s.effectSink = effectSink;
    s.context.wake(arms, s, profile);
    if ( null == s.arvo ) {
      return true;
    }
    else {
      Object old = s.sen;
      s.now = Time.now();
      s.sev = (long) Atom.mug(s.now);
      s.wen = s.kernel("scot", new Cell(DA, s.now));
      s.sen = s.kernel("scot", new Cell(UV, s.sev));
      logger.info(String.format("wake: old %s, new %s", Atom.toString(old), Atom.toString(s.sen)));
      return false;
    }
  }
}
