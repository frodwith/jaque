package net.frodwith.jaque.truffle.jet.def.ut;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.Definition;
import net.frodwith.jaque.truffle.jet.ImplementationNode;
import net.frodwith.jaque.truffle.jet.ut.CropNode;

public final class Crop extends Definition {
  @Override
  public ImplementationNode createNode(Context context, CallTarget fallback) {
    return new CropNode(context, fallback);
  }
}
