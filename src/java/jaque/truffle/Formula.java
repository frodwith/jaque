package jaque.truffle;

import jaque.noun.*;

import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class Formula extends NockNode {
  
  public abstract Cell toCell();
  public abstract Object execute(VirtualFrame frame);
  private static final Atom maxLongAtom = Atom.fromLong(Long.MAX_VALUE);
  
  protected static final Atom FAST = Atom.mote("fast");
  protected static final Atom MEMO = Atom.mote("memo");
  
  private Cell cellCache;
  
  public Cell source() {
    if ( null == cellCache ) {
      cellCache = toCell();
    }
    return cellCache;
  }

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
  
  public static final Formula fromCell(Cell formula) {
    Object op  = formula.getHead(),
           arg = formula.getTail();

    if ( op instanceof Cell ) {
      return ConsFormulaNodeGen.create(fromCell((Cell) op), fromCell((Cell) arg));
    }
    else {
      switch ( (int) NockTypesGen.asLong(op) ) {
        case 0: {
          return new FragFormula(NockTypesGen.asAtom(arg));
        }
        case 1: {
          if ( arg instanceof Cell ) {
            return LiteralCellFormulaNodeGen.create((Cell) arg);
          }
          else if ( arg instanceof Boolean ) {
            return LiteralBooleanFormulaNodeGen.create((boolean) arg);
          }
          else if ( arg instanceof Long) {
            return LiteralLongFormulaNodeGen.create((long) arg); 
          }
          else {
            return LiteralAtomFormulaNodeGen.create((Atom) arg);
          }
        }
        case 2: {
          Cell c = (Cell) arg;
          return NockFormulaNodeGen.create(
            fromCell((Cell) c.getHead()),
            fromCell((Cell) c.getTail()));
        }
        case 3:
          return DeepFormulaNodeGen.create(fromCell((Cell) arg));
        case 4:
          return BumpFormulaNodeGen.create(fromCell((Cell) arg));
        case 5: {
          Cell c = (Cell) arg;
          return SameFormulaNodeGen.create(
            fromCell((Cell) c.getHead()),
            fromCell((Cell) c.getTail()));
        }
        case 6: {
          Cell trel = (Cell) arg;
          Cell pair = (Cell) trel.getTail();

          return new CondFormula(
            fromCell((Cell) trel.getHead()),
            fromCell((Cell) pair.getHead()),
            fromCell((Cell) pair.getTail()));
        }
        case 7: {
          Cell c = (Cell) arg;
          return new ComposeFormula(
            fromCell((Cell) c.getHead()),
            fromCell((Cell) c.getTail()));
        }
        case 8: {
          Cell c = (Cell) arg;
          return new PushFormula(
            fromCell((Cell) c.getHead()),
            fromCell((Cell) c.getTail()));
        }
        case 9: {
          Cell c = (Cell) arg;
          return new KickFormula((Atom) c.getHead(), fromCell((Cell)c.getTail()));
        }
        case 10: {
          Cell    cell = (Cell) arg;
          Formula next = fromCell((Cell) cell.getTail());
          Object  head = cell.getHead();
          if ( head instanceof Atom ) {
            if ( head.equals(MEMO) ) {
              return new MemoHintFormula(next);
            }
            else {
              return new StaticHintFormula(Atom.coerceAtom(head), next);
            }
          }
          else {
            Cell dyn  = (Cell) head;
            Atom kind = Atom.coerceAtom(dyn.getHead());
            Formula dynF = fromCell((Cell) dyn.getTail());
            if ( kind.equals(FAST) ) {
              return new FastHintFormula(dynF, next);
            }
            else {
              return new DynamicHintFormula(kind, dynF, next);
            }
          }
        }
        case 11: {
          Cell c = (Cell) arg;
          return EscapeFormulaNodeGen.create(
            fromCell((Cell) c.getHead()),
            fromCell((Cell) c.getTail()));
        }
        default: {
          throw new IllegalArgumentException();
        }
      }
    }
  }
}
