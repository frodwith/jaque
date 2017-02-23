package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Formula extends NockNode {
  
  public abstract Cell toNoun();
  public abstract Object execute(VirtualFrame frame);
  private static final Atom maxLongAtom = Atom.fromLong(Long.MAX_VALUE);


  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectLong(execute(frame));
  }

  public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectBoolean(execute(frame));
  }

  public Atom executeAtom(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectAtom(execute(frame));
  }

  public Cell executeCell(VirtualFrame frame) throws UnexpectedResultException {
    return NockTypesGen.expectCell(execute(frame));
  }

  public Noun executeNoun(VirtualFrame frame) {
    return (Noun) execute(frame);
  }

  public static Noun getSubject(VirtualFrame frame) {
     return (Noun) frame.getArguments()[0];
  }


  private static final Cell forceCell(Noun n) {
    if (n instanceof Cell) {
      return (Cell) n;
    }
    else {
      throw new IllegalArgumentException();
    }
  }

  public static final Formula fromCell(Cell formula) {
    Noun op  = formula.p,
         arg = formula.q;

    if ( op instanceof Cell ) {
      return ConsFormulaNodeGen.create(fromCell((Cell) op), fromCell(forceCell(arg)));
    }
    else {
      if ( !(op instanceof DirectAtom) ) {
        throw new IllegalArgumentException();
      }
      switch ( ((DirectAtom) op).val ) {
        case 0: {
          return new FragFormula((Atom) arg);
        }
        case 1: {
          if ( arg instanceof Cell ) {
            return new LiteralCellFormula((Cell) arg);
          }
          else {
            Atom a = (Atom) arg;
            if ( 1 > a.compareTo(maxLongAtom) ) {
              return new LiteralLongFormula(a.longValue());
            }
            else {
              return new LiteralAtomFormula(a);
            }
          }
        }
        case 2: {
          Cell c = forceCell(arg);
          return NockFormulaNodeGen.create(
            fromCell(forceCell(c.p)),
            fromCell(forceCell(c.q)));
        }
        case 3:
          return DeepFormulaNodeGen.create(fromCell(forceCell(arg)));
        case 4:
          assert arg instanceof Cell;
          return BumpFormulaNodeGen.create(fromCell(forceCell(arg)));
        case 5: {
          Cell c = forceCell(arg);
          return SameFormulaNodeGen.create(
            fromCell(forceCell(c.p)),
            fromCell(forceCell(c.q)));
        }
        case 6: {
          Cell trel = forceCell(arg);
          Cell pair = forceCell(trel.q);

          return new CondFormula(
            fromCell(forceCell(trel.p)),
            fromCell(forceCell(pair.p)),
            fromCell(forceCell(pair.q)));
        }
        case 7: {
          Cell c = forceCell(arg);
          return new ComposeFormula(
            fromCell(forceCell(c.p)),
            fromCell(forceCell(c.q)));
        }
        case 8: {
          Cell c = forceCell(arg);
          return new PushFormula(
            fromCell(forceCell(c.p)),
            fromCell(forceCell(c.q)));
        }
        case 9: {
          Cell c = forceCell(arg);
          if ( !(c.p instanceof Atom) ) {
            throw new IllegalArgumentException();
          }
          return new KickFormula((Atom) c.p, fromCell(forceCell(c.q)));
        }
        case 10: {
          Cell    c = forceCell(arg);
          Formula k = fromCell(forceCell(c.q));
          if ( c.p instanceof Atom ) {
            return new StaticHintFormula((Atom) c.p, k);
          }
          else {
            Cell h = forceCell(c.p);
            if ( !(h.p instanceof Atom) ) {
              throw new IllegalArgumentException();
            }
            else {
              return new DynamicHintFormula((Atom) h.p, fromCell(forceCell(h.q)), k);
            }
          }
        }
        case 11: {
          Cell c = forceCell(arg);
          return EscapeFormulaNodeGen.create(
            fromCell(forceCell(c.p)),
            fromCell(forceCell(c.q)));
        }
        default: {
          throw new IllegalArgumentException();
        }
      }
    }
  }
}
