package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;
import net.frodwith.jaque.data.List;

public abstract class LossNode extends PairGateNode {

  @Specialization
  protected Object loss(Object a, Object b) {
    return List.loss(a, b);
  }

}