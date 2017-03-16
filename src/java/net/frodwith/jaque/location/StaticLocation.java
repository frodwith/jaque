package net.frodwith.jaque.location;

import java.util.Map;

import net.frodwith.jaque.data.Cell;

public class StaticLocation extends Location {
  private final Object noun;

  public StaticLocation(String name, Object noun, Map<String, Object> hooks) {
    super(name, hooks);
    this.noun = noun;
  }

  @Override
  public boolean matches(Cell core) {
    // possible to argue that this should be .equals(), but this is so
    // much faster that it's worth the possible false negatives.
    return core == noun;
  }

  @Override
  public String getLabel() {
    return name;
  }

}