package net.frodwith.jaque.truffle.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Location;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.object.Shape;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Fragment;

@NodeField(name="fragment", type=Fragment.class)
public abstract class ReadNode extends JaqueNode {
  public abstract Object executeRead(Object cell);
  public abstract Fragment getFragment();
  
  @Specialization(guards = { "shapeCheck(shape, cell)" })
  protected static Object readCached(DynamicObject cell, 
      @Cached("lookupShape(cell)") Shape shape,
      @Cached("lookupLocation(shape)") Location location) {
    return location.get(cell, shape);
  }
  
  @Specialization
  protected Object readSlow(DynamicObject cell) {
    return cell.get(getFragment());
  }
  
  @Specialization
  protected Object badRead(Object something) {
    throw new Bail();
  }
  
  protected static boolean shapeCheck(Shape shape, DynamicObject cell) {
    return shape != null && shape.check(cell);
  }
  
  protected static Shape lookupShape(DynamicObject cell) {
    CompilerAsserts.neverPartOfCompilation();
    return cell.getShape();
  }
  
  protected Location lookupLocation(Shape shape) {
    CompilerAsserts.neverPartOfCompilation();
    Property property = shape.getProperty(getFragment());
    assert property != null;
    return property.getLocation();
  }
}
