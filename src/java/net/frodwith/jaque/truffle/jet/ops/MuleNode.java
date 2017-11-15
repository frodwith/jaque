package net.frodwith.jaque.truffle.jet.ops;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.jet.BinaryOpNode;

@NodeField(name="context", type=Context.class)
public abstract class MuleNode extends BinaryOpNode {
  public abstract Context getContext();
  
  @Specialization
  protected Object mule(Object tap, Object con) {
    /* to paraphrase the u3 jet: this takes advantage of the fact that mute's
     * result, at the typeless (nock) level, is identical to what a typed
     * mule would produce, without running the formula twice.
    */
    //return getContext().kernel("mute", tap); cheating
    Context context = getContext();
    try {
      return context.slam(Cell.expect(context.hook(Cell.expect(con), "mute")), tap);
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }

}