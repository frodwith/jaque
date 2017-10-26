package net.frodwith.jaque.prevayler;

import java.util.Date;

import org.prevayler.Transaction;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.data.Tank;
import net.frodwith.jaque.data.Tape;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.truffle.Context;

public class Poke implements Transaction<PrevalentSystem> {
  private static final Object
    EXIT = Atom.mote("exit"),
    HEAR = Atom.mote("hear"),
    HOLE = Atom.mote("hole"),
    CRUD = Atom.mote("crud"),
    WARN = Atom.mote("warn");

  public Object event;
  public Object time;
  /* transient result allows us to recompute result during playback
   * without storing the arvo core in the poke */
  public transient Cell result;

  public Poke(Object event, Object time) {
    this.event = event;
    this.time = time;
    this.result = null;
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
  
  /* we "cheat" from outside a little bit to compute result before calling execute
   * this gives us an opportunity not to record a poke if we didn't finish computation,
   * allowing us to match vere's drop-if-not-completed semantics */
  public void deliver(Context ctx, Object arvo) {
    Cell pokeGate = Cell.orBail(ctx.nock(arvo, new Qual(9L, 42L, 0L, 1L).toCell()));
    Cell gon = ctx.softTop(() -> ctx.softSlam(pokeGate, new Cell(this.time, this.event)));
    if ( Noun.equals(0L, gon.head) ) {
      result = Cell.orBail(gon.tail);
      return;
    }
    else {
      Object why = gon.head,
             tan = gon.tail;
      // lame
      Cell bov, ovo = Cell.orBail(this.event);
      if ( Noun.equals(EXIT, why) && Noun.equals(HEAR, Cell.orBail(ovo.tail).head) ) {
        // FIXME -- some method of getting the terminal width is necessary
        // 80 is duct tape
        ctx.err(punt(80L, tan));
        bov = new Trel(ovo.head, HOLE, Cell.orBail(ovo.tail).tail).toCell();
      }
      else {
        bov = new Qual(ovo.head, CRUD, why, tan).toCell();
      }
      gon = ctx.softTop(() -> ctx.softSlam(pokeGate, new Cell(this.time, bov)));
      if ( Noun.equals(0L, gon.head) ) {
        result = Cell.orBail(gon.tail);
        return;
      }
      else {
        Cell vab = new Trel(bov.head, WARN, Tape.fromString("crude crash!")).toCell();
        gon = ctx.softTop(() -> ctx.softSlam(pokeGate, new Cell(this.time, vab)));
        if ( Noun.equals(0L,  gon.head ) ) {
          result = Cell.orBail(gon.tail);
          return;
        }
        else {
          // FIXME 80
          ctx.err("crude: all delivery failed!");
          ctx.err(punt(80L, gon.tail));
          throw new RuntimeException("crude");
        }
      }
    }
  }

  @Override
  public void executeOn(PrevalentSystem s, Date now) {
    if ( null == result ) {
      deliver(s.context, s.arvo);
    }
    s.arvo = result.tail;
    s.effect(result.head);
  }

}
