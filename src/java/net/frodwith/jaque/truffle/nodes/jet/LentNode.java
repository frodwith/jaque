package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.List;

public abstract class LentNode extends GateNode {

  @Specialization
  protected Object lent(Object list) {
    return List.lent(list);
  }

}