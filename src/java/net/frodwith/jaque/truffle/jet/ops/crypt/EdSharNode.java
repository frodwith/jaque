package net.frodwith.jaque.truffle.jet.ops.crypt;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

public abstract class EdSharNode extends BinaryOpNode {
  @Specialization
  protected Object shar(Object pub, Object sek) {
    return Atom.edShar(pub, sek);
  }
}