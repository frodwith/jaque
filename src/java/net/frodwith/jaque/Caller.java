package net.frodwith.jaque;

import net.frodwith.jaque.data.Cell;

public interface Caller {
  public Object kernel(String gateName, Object sample);
  public void slog(Object tank);
}
