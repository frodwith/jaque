package net.frodwith.jaque.truffle.jet.def;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.ImplementationNode;
import net.frodwith.jaque.truffle.jet.Definition;
import net.frodwith.jaque.truffle.jet.GateNode;
import net.frodwith.jaque.truffle.jet.ops.DecNodeGen;

public final class Dec extends Definition {
  @Override
  public ImplementationNode createNode(Context context, CallTarget fallback) {
    return new GateNode(DecNodeGen.create(context));
  }
}
