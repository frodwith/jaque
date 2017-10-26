package net.frodwith.jaque.truffle.bloc;

import net.frodwith.jaque.truffle.NockLanguage;

public abstract class RootNode extends com.oracle.truffle.api.nodes.RootNode {
  
  public RootNode() {
    super(NockLanguage.class, null, BlocNode.DESCRIPTOR);
  }

}
