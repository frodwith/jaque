package net.frodwith.jaque.truffle;

import com.oracle.truffle.api.TruffleLanguage;

import clojure.asm.Type;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.nodes.formula.BumpNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.ComposeNode;
import net.frodwith.jaque.truffle.nodes.formula.ConsNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.DeepNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.Formula;
import net.frodwith.jaque.truffle.nodes.formula.FragmentNode;
import net.frodwith.jaque.truffle.nodes.formula.IfNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralCellNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralIntArrayNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralLongNode;
import net.frodwith.jaque.truffle.nodes.formula.NockNode;
import net.frodwith.jaque.truffle.nodes.formula.PushNode;
import net.frodwith.jaque.truffle.nodes.formula.SameNodeGen;

public class NockLanguage extends TruffleLanguage<Context> {

  @Override
  protected Context createContext(com.oracle.truffle.api.TruffleLanguage.Env env) {
    return new Context();
  }

  @Override
  protected Object findExportedSymbol(Context context, String globalName, boolean onlyExplicit) {
    // nock doesn't have this concept
    return null;
  }

  @Override
  protected Object getLanguageGlobal(Context context) {
    // nock doesn't have this concept
    return null;
  }

  @Override
  protected boolean isObjectOfLanguage(Object object) {
    return TypesGen.isCell(object) || TypesGen.isImplicitIntArray(object);
  }
  
  public static Formula parseCell(Cell src) {
    Object op  = src.head,
           arg = src.tail;

    if ( TypesGen.isCell(op) ) {
      return ConsNodeGen.create(
          parseCell(TypesGen.asCell(op)),
          parseCell(TypesGen.asCell(arg)));
    }
    else {
      switch ( (int) TypesGen.asLong(op) ) {
        case 0: {
          return new FragmentNode(arg);
        }
        case 1: {
          if ( TypesGen.isCell(arg) ) {
            return new LiteralCellNode(TypesGen.asCell(arg));
          }
          else if ( TypesGen.isLong(arg) ) {
            return new LiteralLongNode(TypesGen.asLong(arg));
          }
          else {
            return new LiteralIntArrayNode(TypesGen.asIntArray(arg));
          }
        }
        case 2: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return new NockNode(parseCell(h), parseCell(t));
        }
        case 3:
          return DeepNodeGen.create(parseCell(TypesGen.asCell(arg)));
        case 4:
          return BumpNodeGen.create(parseCell(TypesGen.asCell(arg)));
        case 5: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return SameNodeGen.create(parseCell(h), parseCell(t));
        }
        case 6: {
          Cell trel = TypesGen.asCell(arg),
               pair = TypesGen.asCell(trel.tail),
               one  = TypesGen.asCell(trel.head),
               two  = TypesGen.asCell(pair.head),
               tre  = TypesGen.asCell(pair.tail);

          return new IfNode(parseCell(one), parseCell(two), parseCell(tre));
        }
        case 7: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);

          return new ComposeNode(parseCell(h), parseCell(t));
        }
        case 8: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return new PushNode(parseCell(h), parseCell(t));
        }
        case 9: {
          Cell c = TypesGen.asCell(arg),
               t = TypesGen.asCell(c.tail);
          return new KickNode(c.head, parseCell(t));
        }
        case 10: {
          Cell    cell = (Cell) arg;
          Formula next = readFormula((Cell) cell.getTail());
          Object  head = cell.getHead();
          if ( head instanceof Atom ) {
            if ( Atom.MEMO.equals(head) ) {
              return new MemoHintFormula(next);
            }
            else {
              return new StaticHintFormula(Atom.coerceAtom(head), next);
            }
          }
          else {
            Cell dyn  = (Cell) head;
            Formula dynF = readFormula((Cell) dyn.getTail());
            Object kind = dyn.getHead();
            if ( Atom.FAST.equals(kind) ) {
              return new FastHintFormula(dynF, next);
            }
            else {
              return new DynamicHintFormula(Atom.coerceAtom(kind), dynF, next);
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

}
