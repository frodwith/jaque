package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class EndNode extends TrelGateNode {

  @Specialization
  protected Object end(long a, Object b, Object c) {
    return Atom.end(Atom.expectBloq(a), Atom.expect(b), Atom.expect(c));
  }

}