package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.dsl.Specialization;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.Bail;

public abstract class DecrementNode extends JetNode {
  protected abstract Object executeDecrement(Object atom);

  @Override
  public Object executeJet(Cell core) {
    return this.executeDecrement(Noun.fragment(17L, core));
  }
  
  @Specialization
  protected long decrement(long atom) {
    if ( atom == 0 ) {
      System.err.print("decrement underflow");
      throw new Bail();
    }
    return atom - 1L;
  }
  
  @Specialization
  protected Object decrement(int[] atom) {
    return Atom.decrement(atom);
  }

}
