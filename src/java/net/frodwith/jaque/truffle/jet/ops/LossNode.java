package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class LossNode extends BinaryOpNode {
  @Specialization
  protected Object loss(Object a, Object b) {
    return List.loss(a, b);
  }
}