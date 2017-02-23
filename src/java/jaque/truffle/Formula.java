package jaque.truffle;

import jaque.interpreter.Bail;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.TypeSystemReference;

public abstract class Formula extends NockNode {
  
  public abstract Cell toNoun();
  public abstract Object execute(VirtualFrame frame);
  private static final Atom maxLongAtom = Atom.fromLong(Long.MAX_VALUE);


  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.expectLong(execute(frame));
  }

  public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.expectBoolean(execute(frame));
  }

  public Atom executeAtom(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.expectAtom(execute(frame));
  }

  public Cell executeCell(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.expectCell(execute(frame));
  }

  public Noun executeNoun(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.expectNoun(execute(frame));
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

  public static final Formula fromNoun(Cell formula) {
    Noun op  = formula.p,
         arg = formula.q;

    if ( op instanceof Cell ) {
      return ConsNodeGen.create(fromNoun((Cell) op), fromNoun(forceCell(arg)));
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
          return NockNodeGen.create(
            fromNoun(forceCell(c.p)),
            fromNoun(forceCell(c.q)));
        }
        case 3:
          return DeepNodeGen.create(fromNoun(forceCell(arg)));
        case 4:
          assert arg instanceof Cell;
          return BumpNodeGen.create(fromNoun(forceCell(arg)));
        case 5: {
          Cell c = forceCell(arg);
          return SameNodeGen.create(
            fromNoun(forceCell(c.p)),
            fromNoun(forceCell(c.q)));
        }
        case 6: {
          Cell trel = forceCell(arg);
          Cell pair = forceCell(trel.q);

          return new CondFormula(
            fromNoun(forceCell(trel.p)),
            fromNoun(forceCell(pair.p)),
            fromNoun(forceCell(pair.q)));
        }
        case 7: {
          Cell c = forceCell(arg);
          return new ComposeFormula(
            fromNoun(forceCell(c.p)),
            fromNoun(forceCell(c.q)));
        }
        case 8: {
          Cell c = forceCell(arg);
          return new PushFormula(
            fromNoun(forceCell(c.p)),
            fromNoun(forceCell(c.q)));
        }
        case 9: {
          Cell c = forceCell(arg);
          if ( !(c.p instanceof Atom) ) {
            throw new IllegalArgumentException();
          }
          return new KickFormula((Atom) c.p, fromNoun(forceCell(c.q)));
        }
        case 10: {
          Cell    c = forceCell(arg);
          Formula k = fromNoun(forceCell(c.q));
          if ( c.p instanceof Atom ) {
            return new StaticHintFormula((Atom) c.p, k);
          }
          else {
            Cell h = forceCell(c.p);
            if ( !(h.p instanceof Atom) ) {
              throw new IllegalArgumentException();
            }
            else {
              return new DynamicHintFormula((Atom) h.p, fromNoun(forceCell(h.q)), k);
            }
          }
        }
        case 11: {
          Cell c = forceCell(arg);
          return EscapeFormulaGen.create(
            fromNoun(forceCell(c.p)),
            fromNoun(forceCell(c.q)));
        }
        default: {
          throw new IllegalArgumentException();
        }
      }
    }
  }
}
