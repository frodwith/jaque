package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class AddNode extends BinaryJetNode {

  @Specialization(rewriteOn = ArithmeticException.class)
  protected long add(long a, long b) {
    return Atom.add(a, b);
  }

  @Specialization
  protected int[] add(int[] a, int[] b) {
    return Atom.add(a, b);
  }

  @Specialization
  protected Object add(Object a, Object b) {
    if ( !Noun.isAtom(a) || !Noun.isAtom(b) ) {
      throw new Bail();
    }
    return Atom.add(a, b);
  }
}