package net.frodwith.jaque.truffle.bloc;

import java.util.Stack;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Clue;
import net.frodwith.jaque.truffle.Context;

public final class FastNode extends OpNode {
  private final Context context;

  public FastNode (Context context) {
    this.context = context;
  }
  
  @TruffleBoundary
  private Location find(Cell core, Cell battery, Object rawClue) throws UnexpectedResultException {
    if ( context.locations.containsKey(battery) ) {
      // we do this rather than replacing with toss after register because technically the same call site
      // can produce different fast-hinted cores with dynamically produced clues, although afaik this is
      // never done in hoon (maybe some of the wet-gate parsing stuff?)
      return null;
    }
    Clue clue = Clue.parse(rawClue);
    if ( Atom.isZero(clue.parentAxis) ) {
      return new Location(clue.name, clue.name, 0L, clue.hooks, core, null);
    }
    else {
      Axis axis = new Axis(clue.parentAxis);
      Cell parentCore = Cell.expect(axis.fragment(core));
      Cell parentBattery = Cell.expect(parentCore.head);
      Location parentLoc = context.locations.get(parentBattery);
      if ( null == parentLoc ) {
        context.err("register: invalid parent for " + clue.name);
        return null;
      }
      String label = parentLoc.label + "/" + clue.name;
      return new Location(clue.name, label, clue.parentAxis, clue.hooks, core, parentLoc);
    }
  }
    
  public void execute(VirtualFrame frame) {
    Stack<Object> s = getStack(frame);
    try {
      Object rawCore = s.pop();
      Object rawClue = s.pop();
      s.push(rawCore);
      Cell core    = Cell.expect(rawCore),
           battery = Cell.expect(core.head);
      Location loc = find(core, battery, rawClue);
      if ( null != loc ) {
        context.register(battery, loc);
      }
    }
    catch ( UnexpectedResultException e ) {
    }
  }
}
