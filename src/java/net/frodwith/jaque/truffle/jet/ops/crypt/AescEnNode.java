package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class AescEnNode extends BinaryOpNode {
  @Specialization
  protected Object en(Object key, Object txt) {
    return Atom.aescEn(key, txt);
  }
}