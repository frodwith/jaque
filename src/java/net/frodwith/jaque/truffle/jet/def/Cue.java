package net.frodwith.jaque.truffle.jet.def;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.ImplementationNode;
import net.frodwith.jaque.truffle.jet.Definition;
import net.frodwith.jaque.truffle.jet.GateNode;
import net.frodwith.jaque.truffle.jet.ops.CueNodeGen;

public final class Cue extends Definition {
  @Override
  public ImplementationNode createNode(Context context, CallTarget fallback) {
    return new GateNode(CueNodeGen.create());
  }
}
