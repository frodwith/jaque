package jaque.truffle;

import jaque.noun.*;
import jaque.interpreter.*;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;

public final class NockContext {
  public Machine m;
  private Map<Cell,CallTarget> targets;
  private Map<KickLabel,CallTarget> kicks;

  public NockContext(Machine m) {
    this.m = m;
    this.targets = new HashMap<Cell,CallTarget>();
    this.kicks = new HashMap<KickLabel,CallTarget>();
  }
  
  private Formula readFormula(Cell formulaCell) {
    Object op  = formulaCell.getHead(),
           arg = formulaCell.getTail();

    if ( op instanceof Cell ) {
      return new ConsFormula(readFormula((Cell) op), readFormula((Cell) arg));
    }
    else {
      switch ( (int) NockTypesGen.asLong(op) ) {
        case 0: {
          Atom axis = NockTypesGen.asAtom(arg);
          if ( axis.isZero() ) {
            return new BailNode();
          }
          else {
            return new FragFormula(axis);
          }
        }
        case 1: {
          if ( arg instanceof Cell ) {
            return new LiteralCellFormula((Cell) arg);
          }
          else if ( arg instanceof Boolean ) {
            return new LiteralLongFormula(NockTypesGen.asLong(arg));
          }
          else if ( arg instanceof Long) {
            return new LiteralLongFormula((long) arg); 
          }
          else {
            Atom a = (Atom) arg;
            if ( a.words().length <= 2 ) {
              return new LiteralLongFormula(a.longValue());
            }
            else {
              return new LiteralAtomFormula((Atom) arg);
            }
          }
        }
        case 2: {
          Cell c = (Cell) arg;
          return new NockFormula(
            readFormula((Cell) c.getHead()),
            readFormula((Cell) c.getTail()));
        }
        case 3:
          return DeepFormulaNodeGen.create(readFormula((Cell) arg));
        case 4:
          return BumpFormulaNodeGen.create(readFormula((Cell) arg));
        case 5: {
          Cell c = (Cell) arg;
          return SameFormulaNodeGen.create(
            readFormula((Cell) c.getHead()),
            readFormula((Cell) c.getTail()));
        }
        case 6: {
          Cell trel = (Cell) arg;
          Cell pair = (Cell) trel.getTail();

          return new CondFormula(
            readFormula((Cell) trel.getHead()),
            readFormula((Cell) pair.getHead()),
            readFormula((Cell) pair.getTail()));
        }
        case 7: {
          Cell c = (Cell) arg;
          return new ComposeFormula(
            readFormula((Cell) c.getHead()),
            readFormula((Cell) c.getTail()));
        }
        case 8: {
          Cell c = (Cell) arg;
          return new PushFormula(
            readFormula((Cell) c.getHead()),
            readFormula((Cell) c.getTail()));
        }
        case 9: {
          Cell c = (Cell) arg;
          return new KickFormula((Atom) c.getHead(), readFormula((Cell)c.getTail()));
        }
        case 10: {
          Cell    cell = (Cell) arg;
          Formula next = readFormula((Cell) cell.getTail());
          Object  head = cell.getHead();
          if ( head instanceof Atom ) {
            if ( head.equals(Atom.MEMO) ) {
              return new MemoHintFormula(next);
            }
            else {
              return new StaticHintFormula(Atom.coerceAtom(head), next);
            }
          }
          else {
            Cell dyn  = (Cell) head;
            Atom kind = Atom.coerceAtom(dyn.getHead());
            Formula dynF = readFormula((Cell) dyn.getTail());
            if ( kind.equals(Atom.FAST) ) {
              return new FastHintFormula(dynF, next);
            }
            else {
              return new DynamicHintFormula(kind, dynF, next);
            }
          }
        }
        case 11: {
          Cell c = (Cell) arg;
          return new EscapeFormula(
            readFormula((Cell) c.getHead()),
            readFormula((Cell) c.getTail()));
        }
        default: {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  public CallTarget getNockTarget(Cell c) {
    CallTarget t;
    if ( targets.containsKey(c) ) {
      t = targets.get(c);
    }
    else {
      CompilerDirectives.transferToInterpreter();
      t = Truffle.getRuntime().createCallTarget(new NockRootNode(readFormula(c)));
      targets.put(c, t);
    }
    return t;
  }

  public CallTarget getKickTarget(Cell core, Atom axis) {
    KickLabel label = new KickLabel((Cell) core.getHead(), axis);
    CallTarget t;
    if ( kicks.containsKey(label) ) {
      t = kicks.get(label);
    }
    else {
      Cell c = (Cell) new Fragmenter(axis).fragment(core);
      CompilerDirectives.transferToInterpreter();
      t = Truffle.getRuntime().createCallTarget(new NockRootNode(readFormula(c)));
      kicks.put(label, t);
    }
    return t;
  }
  
  @TruffleBoundary
  public final Noun startHint(Hint h) {
    Result r = m.startHint(h);
    m = r.m;
    return r.r;
  }

  @TruffleBoundary
  public final void endHint(Hint h, Object product) {
    m = m.endHint(h, product);
  }

  @TruffleBoundary
  public final Noun escape(Noun ref, Noun sam) {
    Result r = m.escape(ref, sam);
    m = r.m;
    return r.r;
  }
  
  @TruffleBoundary
  public final void declare(Cell core, Object clue) {
    m = m.declare(core, clue);
  }

  @TruffleBoundary
  public boolean fine(Cell core) {
    return m.fine(core);
  }

  @TruffleBoundary
  public final Jet find(Cell core, Atom axis) {
    return m.find(core, axis);
  }

  @TruffleBoundary
  public final Object apply(Jet j, Object[] arguments) {
    Result r = j.apply(m, arguments);
    m = r.m;
    return r.r;
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
