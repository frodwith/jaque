package net.frodwith.jaque.truffle.jet.def;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.Definition;
import net.frodwith.jaque.truffle.jet.ImplementationNode;
import net.frodwith.jaque.truffle.jet.TrelGateNode;
import net.frodwith.jaque.truffle.jet.ops.EndNodeGen;

public final class End extends Definition {
  @Override
  public ImplementationNode createNode(Context context, CallTarget fallback) {
    return new TrelGateNode(EndNodeGen.create());
  }
}
