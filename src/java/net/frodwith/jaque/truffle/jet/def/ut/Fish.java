package net.frodwith.jaque.truffle.jet.def.ut;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.Definition;
import net.frodwith.jaque.truffle.jet.ImplementationNode;
import net.frodwith.jaque.truffle.jet.ut.FishNode;

public final class Fish extends Definition {
  @Override
  public ImplementationNode createNode(Context context, CallTarget fallback) {
    return new FishNode(context, fallback);
  }
}
