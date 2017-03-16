package net.frodwith.jaque.location;

import java.util.Map;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragmenter;
import net.frodwith.jaque.truffle.TypesGen;

public class DynamicLocation extends Location {
  private final Cell battery;
  private final Fragmenter toParent;
  private final Location parent;
  private final String label;
  
  public DynamicLocation(Cell battery, String name,
      Fragmenter toParent, Location parent, Map<String, Object> hooks) {
    super(name, hooks);
    this.battery = battery;
    this.label = parent.getLabel() + "/" + name;
    this.toParent = toParent;
    this.parent = parent;
  }

  @Override
  public boolean matches(Cell core) {
    // again, maybe should use cell equality but object identity is faster
    // and sufficient in practice
    return core.head == battery
      && parent.matches(TypesGen.asCell(toParent.fragment(core)));
  }

  @Override
  public String getLabel() {
    return label;
  }

}