package net.frodwith.jaque.truffle.bloc;

import com.oracle.truffle.api.CallTarget;

public class Continuation {
  public CallTarget target;
  public CallTarget after;
  
  private Continuation(CallTarget target, CallTarget after) {
    this.target = target;
    this.after = after;
  }
  
  public static Continuation ret() {
    return new Continuation(null, null);
  }
  
  public static Continuation jump(CallTarget to) {
    return new Continuation(to, null);
  }
  
  public static Continuation call(CallTarget target, CallTarget after) {
    return new Continuation(target, after);
  }
}
