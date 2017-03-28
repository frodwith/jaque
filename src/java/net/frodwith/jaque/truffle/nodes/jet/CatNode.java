package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class CatNode extends TrelGateNode {

  @Specialization
  protected Object cat(long a, Object b, Object c) {
    if ( a >= 32 || a < 0 || !Noun.isAtom(b) || !Noun.isAtom(c) ) {
      throw new Bail();
    }
    return Atom.cat((byte) a, b, c);
  }

}