package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.truffle.Context;

public abstract class Definition {
  public abstract ImplementationNode createNode(Context context, CallTarget fallback);
}
