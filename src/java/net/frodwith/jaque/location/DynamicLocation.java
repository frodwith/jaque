package net.frodwith.jaque.location;

import java.util.Map;

import net.frodwith.jaque.data.Atom;
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
    super(name, hooks, argumentAxes(toParent, parent.fragments));
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

  private static Object[] argumentAxes(Fragmenter toParent, Fragmenter[] parentAxes) {
    int i, j, plen = toParent.path.length - 1;
    Object[] arg = new Object[plen + parentAxes.length];
    Object rem = Atom.mas(toParent.axis),
           cur = 3L;
    
    for ( i = 0; i < plen; ++i, rem = Atom.mas(rem) ) {
      if ( Atom.cap(rem) == 2 ) {
        arg[i] = Atom.peg(cur, 3L);
        cur = Atom.peg(cur, 2L);
      }
      else {
        arg[i] = Atom.peg(cur, 2L);
        cur = Atom.peg(cur, 3L);
      }
    }
    
    for ( j = 0; j < parentAxes.length; ++j, ++i ) {
      arg[i] = Atom.peg(toParent.axis, parentAxes[j].axis);
    }

    return arg;
  }
  
  private Object reconstructToParent(Object[] arguments, boolean[] path, int pathIndex, int argIndex) {
    if ( pathIndex < path.length ) {
      Object arg = arguments[argIndex];
      Object next = reconstructToParent(arguments, path, pathIndex + 1, argIndex + 1);
      if ( path[pathIndex] ) {
        return new Cell(arg, next);
      }
      else {
        return new Cell(next, arg);
      }
    }
    else {
      return parent.reconstructInner(arguments, argIndex);
    }
  }
  
  @Override
  public Object reconstructInner(Object[] arguments, int index) {
    return new Cell(battery, reconstructToParent(arguments, toParent.path, 1, index));
  }

}