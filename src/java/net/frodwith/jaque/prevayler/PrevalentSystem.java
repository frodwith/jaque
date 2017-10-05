package net.frodwith.jaque.prevayler;

import java.io.Serializable;
import java.util.function.Consumer;

import net.frodwith.jaque.Caller;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.data.Time;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.truffle.Context;

public class PrevalentSystem implements Serializable, Caller {
  public Context context;
  public Object arvo;
  public Object now;
  public Object wen;
  public Object sev;
  public Object sen;
  
  public PrevalentSystem() {
    this.context = new Context();
  }

  // must be set by a Wake when the process starts up, before which they will be null
  // transactions that replay after crash will therefore drop slogs and effects on the floor
  public transient Consumer<Object> slogSink, effectSink;

  public Cell axisGate(Object axis) {
    return Cell.expect(context.nock(arvo, new Qual(9L, axis, 0L, 1L).toCell()));
  }

  @Override
  public Object kernel(String gateName, Object sample) {
    Cell gate = Cell.expect(context.wrapSlam(axisGate(20L), Atom.stringToCord(gateName)));
    return context.wrapSlam(gate, sample);
  }
  
  public Object keep(Object type) {
    Cell hap = new Trel(0L, type, 0L).toCell(),
         sam = new Cell(Time.now(), hap);

    System.out.println("keep " + Noun.toString(sam));
    return context.wrapSlam(axisGate(4L), sam);
  }
  
  @Override
  public void slog(Object tank) {
    if ( slogSink != null ) {
      slogSink.accept(tank);
    }
  }

  public void effect(Object effect) {
    if ( effectSink != null ) {
      effectSink.accept(effect);
    }
  }
}
