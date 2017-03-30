package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class MugNode extends UnaryGateNode {

  @Specialization
  protected int mug(Object noun) {
    return Noun.mug(noun);
  }

}