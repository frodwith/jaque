package jaque.truffle;

import jaque.interpreter.Result;
import jaque.noun.*;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.frame.VirtualFrame;

@TypeSystemReference(NockTypes.class)
public abstract class Formula extends Node {
  private final Cell src;

  public abstract Cell toNoun();
  public abstract Object execute(VirtualFrame frame);

  public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.NOCKTYPES.expectLong(execute(frame));
  }

  public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.NOCKTYPES.expectBoolean(execute(frame));
  }

  public Atom executeAtom(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.NOCKTYPES.expectAtom(execute(frame));
  }

  public Cell executeCell(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.NOCKTYPES.expectCell(execute(frame));
  }

  public Noun executeNoun(VirtualFrame frame) throws UnexpectedResultException {
    NockTypesGen.NOCKTYPES.expectNoun(execute(frame));
  }

  public Cell source() {
    if ( null == src ) {
      src = toNoun();
    }
    return src;
  }

  public Noun getSubject(VirtualFrame frame) {
     return (Noun) frame.getArguments()[0];
  }

  @ExplodeLoop
  public static Noun fragment(Atom axis, Noun r) {
    for ( boolean b : axis.fragments() ) {
      if ( !(r instanceof Cell) ) {
        throw new Bail();
      }
      else {
        Cell c = r;
        if ( b ) {
          r = c.q;
        }
        else {
          r = c.p;
        }
      }
    }
    return r;
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
      return new ConsNode(fromNoun((Cell) op), fromNoun(forceCell(arg)));
    }
    else {
      if ( !(op instanceof DirectAtom) ) {
        throw new IllegalArgumentException();
      }
      switch ( ((DirectAtom) op).val ) {
        case 0: {
          return new FragmentNode((Atom) arg);
        }
        case 1:
          return new ConstantNode(arg);
        case 2: {
          Cell c = forceCell(arg);
          return new NockNode(
            fromNoun(forceCell(c.p)),
            fromNoun(forceCell(c.q)));
        }
        case 3:
          return new DeepNode(fromNoun(forceCell(arg)));
        case 4:
          assert arg instanceof Cell;
          return new BumpNode(fromNoun(forceCell(arg)));
        case 5: {
          Cell c = forceCell(arg);
          return new SameNode(
            fromNoun(forceCell(c.p)),
            fromNoun(forceCell(c.q)));
        }
        case 6: {
          Cell trel = forceCell(arg);
          Cell pair = forceCell(trel.q);

          return new IfNode(
            fromNoun(forceCell(trel.p)),
            fromNoun(forceCell(pair.p)),
            fromNoun(forceCell(pair.q)));
        }
        case 7: {
          Cell c = forceCell(arg);
          return new ComposeNode(
            fromNoun(forceCell(c.p)),
            fromNoun(forceCell(c.q)));
        }
        case 8: {
          Cell c = forceCell(arg);
          return new PushNode(
            fromNoun(forceCell(c.p)),
            fromNoun(forceCell(c.q)));
        }
        case 9: {
          Cell c = forceCell(arg);
          if ( !(c.p instanceof Atom) ) {
            throw new IllegalArgumentException();
          }
          return new KickNode((Atom) c.p, fromNoun(forceCell(c.q)));
        }
        case 10: {
          Cell    c = forceCell(arg);
          Formula k = fromNoun(forceCell(c.q));
          if ( c.p instanceof Atom ) {
            return new StaticHintNode((Atom) c.p, k);
          }
          else {
            Cell h = forceCell(c.p);
            if ( !(h.p instanceof Atom) ) {
              throw new IllegalArgumentException();
            }
            else {
              return new DynamicHintNode((Atom) h.p, fromNoun(forceCell(h.q)), k);
            }
          }
        }
        case 11: {
          Cell c = forceCell(arg);
          return new EscapeNode(
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
