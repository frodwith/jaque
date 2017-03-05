package jaque.truffle;

import jaque.noun.*;
import jaque.interpreter.*;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
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
          return new FragFormula(NockTypesGen.asAtom(arg));
        }
        case 1: {
          if ( arg instanceof Cell ) {
            return new LiteralCellFormula((Cell) arg);
          }
          else if ( arg instanceof Boolean ) {
            return new LiteralBooleanFormula((boolean) arg);
          }
          else if ( arg instanceof Long) {
            return new LiteralLongFormula((long) arg); 
          }
          else {
            return new LiteralAtomFormula((Atom) arg);
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
      Cell c = (Cell) Interpreter.fragment(axis, core);
      CompilerDirectives.transferToInterpreter();
      t = Truffle.getRuntime().createCallTarget(new NockRootNode(readFormula(c)));
      kicks.put(label, t);
    }
    return t;
  }
  
  /*
  public CallTarget getTarget(Formula formula) {
    CallTarget target;
    if ( targets.containsKey(formula) ) {
      target = targets.get(formula);
    }
    else {
      CompilerDirectives.transferToInterpreter();
      target = Truffle.getRuntime().createCallTarget(new NockRootNode(formula));
      targets.put(formula, target); 
    }
    return target;
  }
  */
  
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
