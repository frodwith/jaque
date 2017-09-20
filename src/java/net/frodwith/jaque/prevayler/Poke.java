package net.frodwith.jaque.prevayler;

import java.util.Date;

import org.prevayler.Transaction;

import net.frodwith.jaque.Fail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.data.Tank;
import net.frodwith.jaque.data.Tape;
import net.frodwith.jaque.data.Time;
import net.frodwith.jaque.data.Trel;

public class Poke implements Transaction<PrevalentSystem> {
  private static final Object
    EXIT = Atom.mote("exit"),
    HEAR = Atom.mote("hear"),
    HOLE = Atom.mote("hole"),
    CRUD = Atom.mote("crud"),
    WARN = Atom.mote("warn");

  public Object event;

  public Poke(Object event) {
    this.event = event;
  }
  
  private String punt(long width, Object tan) {
    StringBuffer buf = new StringBuffer();
    for ( Object tank : new List(List.flop(tan)) ) {
      for ( Object tape : new List(Tank.wash(2L, width, tank)) )
        buf.append(Tape.toString(tape));
        buf.append("\n");
    }
    return buf.toString();
  }
  
  @Override
  public void executeOn(PrevalentSystem s, Date now) {
    Cell pokeGate = s.axisGate(42L);
    Object t = Time.fromInstant(now.toInstant());
    Cell gon = s.context.softTop(() -> s.context.softSlam(pokeGate, new Cell(t, this.event)));
    if ( Noun.equals(0L, gon.head) ) {
      Cell pro = Cell.expect(gon.tail);
      s.arvo = pro.tail;
      s.effect(pro.head);
    }
    else {
      Object why = gon.head,
             tan = gon.tail;
      // lame
      Cell bov, ovo = Cell.expect(this.event);
      if ( Noun.equals(EXIT, why) && Noun.equals(HEAR, Cell.expect(ovo.tail).head) ) {
        // FIXME -- some method of getting the terminal width is necessary
        // 80 is duct tape
        s.context.err(punt(80L, tan));
        bov = new Trel(ovo.head, HOLE, Cell.expect(ovo.tail).tail).toCell();
      }
      else {
        bov = new Qual(ovo.head, CRUD, why, tan).toCell();
      }
      gon = s.context.softTop(() -> s.context.softSlam(pokeGate, new Cell(t, bov)));
      if ( Noun.equals(0L, gon.head) ) {
        Cell pro = Cell.expect(gon.tail);
        s.arvo = pro.tail;
        s.effect(pro.head);
      }
      else {
        Cell vab = new Trel(bov.head, WARN, Tape.fromString("crude crash!")).toCell();
        gon = s.context.softTop(() -> s.context.softSlam(pokeGate, new Cell(t, vab)));
        if ( Noun.equals(0L,  gon.head ) ) {
          Cell pro = Cell.expect(gon.tail);
          s.arvo = pro.tail;
          s.effect(pro.head);
        }
        else {
          // FIXME 80
          s.context.err("crude: all delivery failed!");
          s.context.err(punt(80L, tan));
          throw new RuntimeException("crude");
        }
      }
    }
  }

}
