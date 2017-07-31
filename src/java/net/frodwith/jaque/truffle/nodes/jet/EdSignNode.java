package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;

public abstract class EdSignNode extends PairGateNode {

  @Specialization
  protected Object sign(Object msg, Object sed) {
    return Atom.edSign(msg, sed);
  }

}