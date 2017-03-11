package net.frodwith.jaque.truffle;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.ExecutionContext;
import com.oracle.truffle.api.Truffle;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;

public class Context extends ExecutionContext {

  private class KickLabel {
    public final Cell battery;
    public final Object axis;
    
    public KickLabel(Cell battery, Object axis) {
      this.battery = battery;
      this.axis = axis;
    }
    
    public int hashCode() {
      return Cell.mug(battery) ^ Atom.mug(axis);
    }
    
    public boolean equals(Object o) {
      if ( !(o instanceof KickLabel) ) {
        return false;
      }
      else {
        KickLabel k = (KickLabel) o;
        return Cell.equals(battery, k.battery) && Atom.equals(axis, k.axis);
      }
    }
  }
  
  private Map<KickLabel, CallTarget> kicks;
  private Map<Cell, CallTarget> nocks;
  
  public Context() {
    this.kicks = new HashMap<KickLabel, CallTarget>();
    this.nocks = new HashMap<Cell, CallTarget>();
  }

  private static CallTarget makeTarget(Cell formula) {
    CompilerDirectives.transferToInterpreter();
    return Truffle.getRuntime().createCallTarget(new JaqueRootNode(NockLanguage.parseCell(formula)));
  }
  
  public CallTarget getNock(Cell c) {
    CallTarget t = nocks.get(c);
    if ( null == t ) {
      t = makeTarget(c);
      nocks.put(c, t);
    }
    return t;
  }

  public CallTarget getKick(Cell core, Object axis) {
    Cell battery    = TypesGen.asCell(core.head);
    KickLabel label = new KickLabel(battery, axis);
    CallTarget t    = kicks.get(label);
    if ( null == t ) {
      Object obj = Noun.fragment(axis, core);
      if ( !TypesGen.isCell(obj) ) {
        throw new Bail();
      }
      else {
        t = makeTarget(TypesGen.asCell(obj));
        kicks.put(label, t);
      }
    }
    return t;
  }
}

/*
 * 
  public CallTarget getNockTarget(Cell c) {
    CallTarget t;
    if ( targets.containsKey(c) ) {
      t = targets.get(c);
    }
    else {
      CompilerDirectives.transferToInterpreter();
      t = Truffle.getRuntime().createCallTarget(new NockRootNode(readFormula(c)));
      targets.put(c, t);
    }
    return t;
  }

  public CallTarget getKickTarget(Cell core, Atom axis) {
    KickLabel label = new KickLabel((Cell) core.getHead(), axis);
    CallTarget t;
    if ( kicks.containsKey(label) ) {
      t = kicks.get(label);
    }
    else {
      Cell c = (Cell) new Fragmenter(axis).fragment(core);
      CompilerDirectives.transferToInterpreter();
      t = Truffle.getRuntime().createCallTarget(new NockRootNode(readFormula(c)));
      kicks.put(label, t);
    }
    return t;
  }

*/