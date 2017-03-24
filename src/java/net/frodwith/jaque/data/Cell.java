package net.frodwith.jaque.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.object.DynamicObject;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TypesGen;

/* Because we must use Object fields for the head and the tail to accomodate the atom
 * types that we are using, it is unfortunately possible to construct a cell of any
 * arbitrary Java objects (including, sometimes frustratingly, cells of ints instead of
 * longs etc.). In particular, suffix literal atoms with L (1L, etc) religiously to avoid
 * this. No real checking is done at runtime.
 */

public class Cell {
  
  private static int mug_both(int lef, int rit) {
    int bot, out;
    while ( true ) {
      bot = Noun.mug_fnv(lef ^ Noun.mug_fnv(rit));
      out = Noun.mug_out(bot);
      if ( 0 != out ) {
        return out;
      }
      else {
        ++rit;
      }
    }
  }
  
  public static boolean equals(DynamicObject a, DynamicObject b) {
    if (a == b) {
      return true;
    }
    if ( (boolean) a.get("hashed") 
        && (boolean) b.get("hashed") 
        && a.get("hash") != b.get("hash") ) {
      return false;
    }
    else {
      return Noun.equals(head(a), head(b)) && Noun.equals(tail(a), tail(b));
    }
  }
  
  public static int mug(DynamicObject c) {
    if ( !((boolean) c.get("hashed")) ) {
      int hash = mug_both(Noun.mug(head(c)), Noun.mug(tail(c)));

      c.set("hash", hash);
      c.set("hashed", true);
      return hash;
    }
    return (int) c.get("hash");
  }
  
  public static Object head(DynamicObject c) {
    CompilerAsserts.neverPartOfCompilation();
    return c.get(Fragment.HEAD);
  }
  
  public static Object tail(DynamicObject c) {
    CompilerAsserts.neverPartOfCompilation();
    return c.get(Fragment.TAIL);
  }
}