package net.frodwith.jaque;

public class Fail extends RuntimeException {
  public Object mote;
  public Object trace;

  public Fail(Object mote, Object trace) {
    this.mote = mote;
    this.trace = trace;
  }
}
