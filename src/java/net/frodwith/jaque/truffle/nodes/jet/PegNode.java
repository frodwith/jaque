package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Noun;

public abstract class PegNode extends PairGateNode {

  @Specialization
  protected Object peg(Object a, Object b) {
    Object pro = Atom.peg(Atom.expect(a), Atom.expect(b));
    System.err.println("peg(" + Noun.toString(a) + "," + Noun.toString(b) + ") = " + pro);
    return pro;
  }

}