package net.frodwith.jaque.data;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.truffle.TypesGen;

public class Clue {
  public final static Map<Object,Clue> memo = new HashMap<Object,Clue>();
  public final String name;
  public final Object parentAxis;
  public final HashMap<String, Object> hooks;
  public static final Cell CONSTANT_ZERO = new Cell(1L, 0L);
  public static final Cell CONSTANT_FRAG = new Cell(0L, 1L);

  private Clue(String name, Object parentAxis, HashMap<String, Object> hooks) {
    this.name = name;
    this.parentAxis = parentAxis;
    this.hooks = hooks;
  }

  private static String chum(Object noun) throws UnexpectedResultException {
    if ( TypesGen.isCell(noun) ) {
      Cell c = TypesGen.asCell(noun);
      Object h = c.head,
             t = c.tail;

      if ( TypesGen.isCell(t) || !TypesGen.isLong(t) ) {
        throw new UnexpectedResultException(t);
      }

      int small = Atom.expectInt(t);
      String cord = Atom.cordToString(h);
      if ( null == cord ) {
        throw new UnexpectedResultException(h);
      }

      return String.format("%s%d", cord, small);
    }
    else {
      return Atom.cordToString(noun);
    }
  }
  
  private static Object skipHints(Object formula) {
    while ( true ) {
      if ( TypesGen.isCell(formula) ) {
        Cell c = TypesGen.asCell(formula);
        if ( Atom.equals(10L, c.head) ) {
          formula = TypesGen.asCell(c.tail).tail;
          continue;
        }
      }
      return formula;
    }
  }

  private static Object parseParentAxis(Object noun) throws UnexpectedResultException {
    Cell f = Cell.expect(skipHints(noun));
    if ( Cell.equals(CONSTANT_ZERO, f) ) {
      return 0L;
    }
    if ( !Atom.isZero(f.head) ) {
      throw new UnexpectedResultException(f.head);
    }
    if ( 3 != Atom.cap(Atom.expect(f.tail)) ) {
      throw new UnexpectedResultException(f.tail);
    }
    return f.tail;
  }
  
  private static Object parseHookAxis(Object nock) throws UnexpectedResultException {
    Cell f = Cell.expect(skipHints(nock));
    Object op = f.head;
    if ( Noun.isAtom(op) ) {
      if ( Atom.isZero(op) ) {
        if ( Noun.isAtom(f.tail) ) {
          return f.tail;
        }
      }
      else if ( Atom.equals(9L, op) ) {
        Cell rest = Cell.expect(f.tail);
        if ( Noun.isAtom(rest.head) 
            && Cell.equals(CONSTANT_FRAG, Cell.expect(rest.tail)) )
        {
          return rest.head;
        }
      }
    }
    return null;
  }

  private static HashMap<String,Object> parseHooks(Object noun) throws UnexpectedResultException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    for ( Object i : new List(noun) ) {
      Cell c = Cell.expect(i);
      Object t = Atom.expect(c.head);
      String term = Atom.cordToString(t);
      if ( null == term ) {
        throw new UnexpectedResultException(t);
      }
      Cell nock = Cell.expect(c.tail);
      Object axis = parseHookAxis(nock);
      if ( null != axis ) {
        map.put(term, axis);
      }
    }
    return map;
  }
  
  public static Clue parse(Object raw) throws UnexpectedResultException {
    Clue pro = memo.get(raw);
    if ( null == pro ) {
      Trel trel = Trel.expect(raw);
      String name = chum(trel.p);
      Object parentAxis = parseParentAxis(trel.q);
      HashMap<String,Object> hooks = parseHooks(trel.r);
      pro = new Clue(name, parentAxis, hooks);
      memo.put(raw, pro);
    }
    return pro;
  }
}
