package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.CallTarget;

/* represents a call and what to do after the call
 * target must not be null (calling nothing is a semantic fail),
 * but a null "after" is the magic "return" continuation.
 * null continuations are also treated this way.
 */
public class Continuation {
  public CallTarget target, after;
  
  public Continuation(CallTarget target, CallTarget after) {
    this.target = target;
    this.after = after;
  }
}
