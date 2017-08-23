package net.frodwith.jaque;

public class BlockException extends RuntimeException {
  public Object gof;

  public BlockException(Object gof) {
    this.gof = gof;
  }
}
