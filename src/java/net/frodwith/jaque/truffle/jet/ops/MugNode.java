package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.jet.UnaryOpNode;

public abstract class MugNode extends UnaryOpNode {
  @Specialization
  protected long mug(Object noun) {
    return (long) Noun.mug(noun);
  }
}