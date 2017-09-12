package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.List;

public abstract class WeldNode extends PairGateNode {

  @Specialization
  protected Object weld(Object a, Object b) {
    return List.weld(a, b);
  }

}