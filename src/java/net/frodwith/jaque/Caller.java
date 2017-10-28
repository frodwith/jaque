package net.frodwith.jaque;

public interface Caller {
  public Object kernel(String gateName, Object sample);
  public void slog(Object tank);
}
