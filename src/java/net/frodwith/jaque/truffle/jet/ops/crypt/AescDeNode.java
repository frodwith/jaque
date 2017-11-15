package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class AescDeNode extends BinaryOpNode {
  @Specialization
  protected Object de(Object key, Object txt) {
    return Atom.aescDe(key, txt);
  }
}