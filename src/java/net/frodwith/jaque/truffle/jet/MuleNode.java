package net.frodwith.jaque.truffle.jet;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.truffle.Context;

public abstract class MuleNode extends SampleContextNode {
  public abstract Object executeMule(VirtualFrame frame, Object tap, Object con);
  
  public Object doSampleContext(VirtualFrame frame, Object sample, Object context) {
    return executeMule(frame, sample, context);
  }
  
  @Specialization
  protected Object mule(Object tap, Object con) {
    /* to paraphrase the u3 jet: this takes advantage of the fact that mute's
     * result, at the typeless (nock) level, is identical to what a typed
     * mule would produce, without running the formula twice.
    */
    //return getContext().kernel("mute", tap); cheating
    Context context = getContext();
    return context.slam(Cell.orBail(context.hook(Cell.orBail(con), "mute")), tap);
  }

}