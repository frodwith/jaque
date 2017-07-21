package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Noun;

public abstract class MugNode extends GateNode {

  @Specialization
  protected long mug(Object noun) {
    return (long) Noun.mug(noun);
  }

}