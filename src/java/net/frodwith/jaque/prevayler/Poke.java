package net.frodwith.jaque.prevayler;

import java.util.Date;

import org.prevayler.Transaction;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Time;

public class Poke implements Transaction<PrevalentSystem> {
  public Object event;

  public Poke(Object event) {
    this.event = event;
  }
  
  @Override
  public void executeOn(PrevalentSystem s, Date now) {
    Cell pokeGate = s.axisGate(42L);
    Cell sample = new Cell(Time.fromInstant(now.toInstant()), this.event);
    Cell pro = Cell.expect(s.context.wrapSlam(pokeGate, sample));
    s.arvo = pro.tail;
    s.tankSink.accept(pro.head);
  }

}
