package net.frodwith.jaque.truffle.nodes.formula.hint;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;

import net.frodwith.jaque.Registration;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;

/* Fast hints are semantically only executed once, then rewritten
 * to a discard hint.
 */
public class FastHintNode extends DynamicHintFormula {
  private final Context context;

  public FastHintNode(Context context, FormulaNode hint, FormulaNode next) {
    super(hint, next);
    this.context = context;
  }
  
  private Registration register(DynamicObject core, Clue clue) {
    if ( Atom.isZero(clue.parentAxis) ) {
      return new Registration(clue.name, clue.name, 0L, clue.hooks,
          core, null, context.drivers.get(clue.name));
    }
    else {
      CompilerAsserts.neverPartOfCompilation();
      DynamicObject parentCore = Noun.asCell(Noun.fragment(clue.parentAxis, core));
      DynamicObject parentBattery = Noun.asCell(Cell.head(parentCore));
      Registration parent = context.locations.get(parentBattery);
      if ( null == parent ) {
        System.err.println("register: invalid parent");
        return null;
      }
      String label = parent.label + "/" + clue.name;
      return new Registration(clue.name, label, clue.parentAxis, clue.hooks, 
          Cell.head(core), parent, context.drivers.get(label));
    }
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object rawClue = hint.executeGeneric(frame);
    Object product = next.executeGeneric(frame);

    // We're on the slow path either way from here
    CompilerDirectives.transferToInterpreter();
    Clue clue = Clue.parse(rawClue);
    if ( Noun.isCell(product) && null != clue ) {
      DynamicObject core = Noun.asCell(product);
      context.locations.put(Noun.asCell(Cell.head(core)), register(core, clue));
    }

    // possibly we could discard to next, but if hint is constant
    // truffle will optimize it away anyway and this more exactly
    // first the semantics if it is not
    FormulaNode discard = new DiscardHintNode(hint, next);
    replace(discard);
    return product;
  }
  
  
  /* It should be noted that clue parsing is always done in the interpreter, so
   * slow-path operations (like Cell.head() instead of a ReadNode) are used freely.
   */
  
  private static class ClueParsingException extends Exception {
  }
  
  private static class Clue {
    public final String name;
    public final Object parentAxis;
    public final Map<String, Object> hooks;
    public static final DynamicObject CONSTANT_ZERO = Context.cons(1L, 0L);
    public static final DynamicObject CONSTANT_FRAG = Context.cons(0L, 1L);

    private Clue(String name, Object parentAxis, Map<String, Object> hooks) {
      this.name = name;
      this.parentAxis = parentAxis;
      this.hooks = hooks;
    }

    private static String chum(Object noun) throws ClueParsingException {
      if ( Noun.isCell(noun) ) {
        DynamicObject c = Noun.asCell(noun);
        Object h = Cell.head(c),
               t = Cell.tail(c);

        if ( Noun.isCell(t) || !TypesGen.isLong(t) ) {
          throw new ClueParsingException();
        }

        long atom = TypesGen.asLong(noun);
        int  small = (int) atom;
        if ( small != atom ) {
          throw new ClueParsingException();
        }

        String cord = Atom.cordToString(h);
        if ( null == cord ) {
          throw new ClueParsingException();
        }

        return String.format("%s%d", cord, small);
      }
      else {
        return Atom.cordToString(noun);
      }
    }
    
    private static Object skipHints(Object formula) {
      while ( true ) {
        if ( Noun.isCell(formula) ) {
          DynamicObject c = Noun.asCell(formula);
          if ( Atom.equals(10L, Cell.head(c)) ) {
            formula = Cell.tail(Noun.asCell(Cell.tail(c)));
            continue;
          }
        }
        return formula;
      }
    }

    private static Object parseParentAxis(Object noun) throws ClueParsingException {
      Object o = skipHints(noun);
      if ( !Noun.isCell(o) ) {
        throw new ClueParsingException();
      }

      DynamicObject f = Noun.asCell(o);
      Object h = Cell.head(f),
             t = Cell.tail(f);

      if ( Cell.equals(CONSTANT_ZERO, f) ) {
        return 0L;
      }
      if ( !Noun.isAtom(t) || !Atom.isZero(h) || Atom.cap(t) != 3) {
        throw new ClueParsingException();
      }
      return t;
    }
    
    private static Object parseHookAxis(Object nock) throws ClueParsingException {
      DynamicObject f = Noun.asCell(skipHints(nock));
      Object op = Cell.head(f),
           tail = Cell.tail(f);
      if ( Noun.isAtom(op) ) {
        if ( Atom.equals(0L, op) ) {
          if ( Noun.isAtom(tail) ) {
            return tail;
          }
        }
        else if ( Atom.equals(9L, op) ) {
          DynamicObject rest = Noun.asCell(tail);
          Object h = Cell.head(rest),
                 t = Cell.tail(rest);

          if ( Noun.isAtom(h) 
              && Cell.equals(CONSTANT_FRAG, Noun.asCell(t)) ) {
            return h;
          }
        }
      }
      throw new ClueParsingException();
    }

    private static Map<String,Object> parseHooks(Object noun) throws ClueParsingException {
      Object list = noun;
      Map<String, Object> map = new HashMap<String, Object>();
      while ( !Atom.isZero(list) ) {
        DynamicObject pair = Noun.asCell(list);
        DynamicObject i = Noun.asCell(Cell.head(pair));
        Object t = Cell.head(i);
        if ( !Noun.isAtom(t) ) {
          throw new ClueParsingException();
        }
        String term = Atom.cordToString(t);
        if ( null == term ) {
          throw new ClueParsingException();
        }
        DynamicObject nock = Noun.asCell(Cell.tail(i));
        map.put(term, parseHookAxis(nock));
        list = Cell.tail(pair);
      }

      return map;
    }
    
    public static Clue parse(Object raw) {
      try {
        DynamicObject trel = Noun.asCell(raw);
        DynamicObject pair = Noun.asCell(Cell.tail(trel));
        String name = chum(Cell.head(trel));
        Object parentAxis = parseParentAxis(Cell.head(pair));
        Map<String, Object> hooks = parseHooks(Cell.tail(pair));
        return new Clue(name, parentAxis, hooks);
      }
      catch (ClassCastException e) {
        System.err.println("Bad noun shape while parsing clue");
        e.printStackTrace();
      }
      catch (ClueParsingException e) {
        System.err.println("Invalid clue");
        e.printStackTrace();
      }
      return null;
    }
  }
}
