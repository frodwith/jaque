package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class LessThanNode extends BinaryJetNode {

  @Specialization
  protected long lth(long a, long b) {
    return (Atom.compare(a, b) == -1) ? Atom.YES : Atom.NO;
  }

  @Specialization
  protected long lth(int[] a, int[] b) {
    return (Atom.compare(a, b) == -1) ? Atom.YES : Atom.NO;
  }

  @Specialization
  protected Object lth(Object a, Object b) {
    if ( !Noun.isAtom(a) || !Noun.isAtom(b) ) {
      throw new Bail();
    }
    return (Atom.compare(a, b) == -1) ? Atom.YES : Atom.NO;
  }
}