package net.frodwith.jaque.truffle;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

import net.frodwith.jaque.data.Cell;

@TypeSystem({long.class, int[].class, Cell.class})
public class Types {
  @ImplicitCast
  public static int[] castIntArray(long v) {
    return new int[] { (int) v, (int) v >>> 32 };
  }
}
