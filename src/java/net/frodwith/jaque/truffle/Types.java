package net.frodwith.jaque.truffle;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

import net.frodwith.jaque.data.Cell;

@TypeSystem({long.class, int[].class, Cell.class})
public class Types {
  @ImplicitCast
  public static int[] castIntArray(long v) {
    int low = (int) v;
    int high = (int) (v >>> 32);
    if ( high == 0 ) {
      return new int[] { low };
    }
    else {
      return new int[] { low, high };
    }
  }
}
