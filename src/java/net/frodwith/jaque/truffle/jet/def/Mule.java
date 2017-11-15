package net.frodwith.jaque.truffle.jet.def;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.Definition;
import net.frodwith.jaque.truffle.jet.ImplementationNode;
import net.frodwith.jaque.truffle.jet.SampleContextNode;
import net.frodwith.jaque.truffle.jet.ops.MuleNodeGen;

public final class Mule extends Definition {
  @Override
  public ImplementationNode createNode(Context context, CallTarget fallback) {
    return new SampleContextNode(MuleNodeGen.create(context));
  }
}
