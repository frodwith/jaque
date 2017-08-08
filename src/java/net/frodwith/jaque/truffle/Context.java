package net.frodwith.jaque.truffle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.Caller;
import net.frodwith.jaque.KickLabel;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragment;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Tank;
import net.frodwith.jaque.data.Tape;
import net.frodwith.jaque.truffle.driver.Arm;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;
import net.frodwith.jaque.truffle.nodes.TopRootNode;
import net.frodwith.jaque.truffle.nodes.formula.BailNode;
import net.frodwith.jaque.truffle.nodes.formula.BumpNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.ComposeNode;
import net.frodwith.jaque.truffle.nodes.formula.ConsNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.DeepNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.EscapeNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;
import net.frodwith.jaque.truffle.nodes.formula.FragmentNode;
import net.frodwith.jaque.truffle.nodes.formula.IdentityNode;
import net.frodwith.jaque.truffle.nodes.formula.IfNode;
import net.frodwith.jaque.truffle.nodes.formula.KickNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.LiteralCellNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralIntArrayNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralLongNode;
import net.frodwith.jaque.truffle.nodes.formula.NockNode;
import net.frodwith.jaque.truffle.nodes.formula.PushNode;
import net.frodwith.jaque.truffle.nodes.formula.SameNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.hint.BawkHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.DiscardHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.FastHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.MemoHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.SlogHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.StackHintNode;

public class Context {
  
  public final Map<KickLabel, CallTarget> kicks;
  public final Map<Cell, CallTarget> nocks;
  public final Map<Cell, Location> locations;
  public final Map<String, Arm[]> drivers;
  
  public final Stack<Object> tax;
  public Caller caller = null;
  
  public Context(Arm[] arms) {
    this.kicks = new HashMap<KickLabel, CallTarget>();
    this.nocks = new HashMap<Cell, CallTarget>();
    this.locations = new HashMap<Cell, Location>();
    this.drivers = new HashMap<String, Arm[]>();
    
    this.tax = new Stack<Object>();

    Map<String, LinkedList<Arm>> temp = new HashMap<String, LinkedList<Arm>>();
    if ( null != arms ) {
      for ( Arm a : arms ) {
        LinkedList<Arm> push = temp.get(a.label);
        if ( null == push ) {
          push = new LinkedList<Arm>();
          temp.put(a.label, push);
        }
        push.add(a);
      }
    }
    
    for ( Map.Entry<String, LinkedList<Arm>> e : temp.entrySet() ) {
      LinkedList<Arm> al = e.getValue();
      Arm[] aa = new Arm[al.size()];
      drivers.put(e.getKey(), al.toArray(aa));
    }
  }
  
  /* If there was a node for this, we could profile it, but it's a slow path operation
   * (in general, we cache formulas) so there's not(?) much benefit to making it a node.
   */
  public FormulaNode parseCell(Cell src, boolean tail) {
    Object op  = src.head,
           arg = src.tail;

    if ( TypesGen.isCell(op) ) {
      return ConsNodeGen.create(
          parseCell(TypesGen.asCell(op), false),
          parseCell(TypesGen.asCell(arg), false));
    }
    else {
      switch ( (int) TypesGen.asLong(op) ) {
        case 0: {
          if ( Atom.isZero(arg) ) {
            return new BailNode();
          }
          if ( Noun.equals(1L, arg) ) {
            return new IdentityNode();
          }
          else {
            return new FragmentNode(Atom.expect(arg));
          }
        }
        case 1: {
          if ( TypesGen.isCell(arg) ) {
            return new LiteralCellNode(TypesGen.asCell(arg));
          }
          else if ( TypesGen.isLong(arg) ) {
            return new LiteralLongNode(TypesGen.asLong(arg));
          }
          else {
            return new LiteralIntArrayNode(TypesGen.asIntArray(arg));
          }
        }
        case 2: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          FormulaNode left = parseCell(h, false),
                     right = parseCell(t, false);
          return new NockNode(left, right, this, tail);
        }
        case 3:
          return DeepNodeGen.create(parseCell(TypesGen.asCell(arg), false));
        case 4:
          return BumpNodeGen.create(parseCell(TypesGen.asCell(arg), false));
        case 5: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return SameNodeGen.create(
              parseCell(h, false),
              parseCell(t, false));
        }
        case 6: {
          Cell trel = TypesGen.asCell(arg),
               pair = TypesGen.asCell(trel.tail),
               one  = TypesGen.asCell(trel.head),
               two  = TypesGen.asCell(pair.head),
               tre  = TypesGen.asCell(pair.tail);

          return new IfNode(
              parseCell(one, false),
              parseCell(two, tail),
              parseCell(tre, tail));
        }
        case 7: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);

          return new ComposeNode(
              parseCell(h, false), 
              parseCell(t, tail));
        }
        case 8: {
          Cell c = TypesGen.asCell(arg),
               h = TypesGen.asCell(c.head),
               t = TypesGen.asCell(c.tail);
          return new PushNode(
              parseCell(h, false), 
              parseCell(t, tail));
        }
        case 9: {
          Cell c = TypesGen.asCell(arg),
               t = TypesGen.asCell(c.tail);
          Axis a = new Axis(Atom.expect(c.head));
          Fragment first = a.iterator().next();
          FormulaNode core = parseCell(t, false);

          return KickNodeGen.create(core, this, tail, first == Fragment.HEAD, a.atom);
        }
        case 10: {
          Cell    cell = TypesGen.asCell(arg);
          FormulaNode next = parseCell(TypesGen.asCell(cell.tail), tail);
          Object  head = cell.head;
          if ( Noun.isAtom(head) ) {
            // What do you do with static hints you don't recognize? Nothing...
            System.err.println("unrecognized static hint: " + Atom.toString(head));
            return next;
          }
          else {
            Cell dyn     = TypesGen.asCell(head);
            FormulaNode dynF = parseCell(TypesGen.asCell(dyn.tail), false);
            Object kind  = dyn.head;

            if ( Atom.MEMO.equals(kind) ) {
              return new MemoHintNode(next);
            }
            else if ( kind.equals(Atom.mote("bawk")) ) {
              return new BawkHintNode(dynF, next);
            }
            else if ( Atom.FAST.equals(kind) ) {
              return new FastHintNode(this, dynF, next);
            }
            else if ( Atom.SLOG.equals(kind) ) {
              return new SlogHintNode(dynF, next);
            }
            else if ( Atom.SPOT.equals(kind) ) {
              return new StackHintNode(tax, Atom.SPOT, dynF, next);
            }
            else if ( Atom.MEAN.equals(kind) ) {
              return new StackHintNode(tax, Atom.MEAN, dynF, next);
            }
            else if ( Atom.LOSE.equals(kind) ) {
              return new StackHintNode(tax, Atom.LOSE, dynF, next);
            }
            else if ( Atom.HUNK.equals(kind) ) {
              return new StackHintNode(tax, Atom.HUNK, dynF, next);
            }
            else {
              System.err.println("unrecognized dynamic hint: " + Atom.toString(kind));
              return new DiscardHintNode(dynF, next);
            }
          }
        }
        case 11: {
          Cell c = TypesGen.asCell(arg);
          return EscapeNodeGen.create(
              parseCell(TypesGen.asCell(c.head), false),
              parseCell(TypesGen.asCell(c.tail), false),
              this);
        }
        default: {
          printStack();
          throw new IllegalArgumentException();
        }
      }
    }
  }

  /* Top-level interpeter entry point */
  public Object nock(Object subject, Cell formula) {
    FormulaNode program = parseCell(formula, true);
    JaqueRootNode root  = new JaqueRootNode(program);
    CallTarget inner    = Truffle.getRuntime().createCallTarget(root);
    TopRootNode top     = new TopRootNode(inner);
    CallTarget outer    = Truffle.getRuntime().createCallTarget(top);
    
    try {
      return outer.call(subject);
    }
    catch (Bail e) {
      printStack();
      throw e;
    }
  }
  
  private void printStack() {
    if ( null == caller ) {
      System.err.println("Cannot print stack: no kernel caller");
      return;
    }
    
    // this all needs cleaning up.
    Object tan = 0L;
    while ( !tax.isEmpty() ) {
      tan = new Cell(tax.pop(), tan);
    }

    // it's in reverse order now (think about it... stacks...)
    Object ton = 0L;
    for ( Object o : new List(tan) ) {
      ton = new Cell(o, ton);
    }
    
    Object toon = caller.kernel("mook", new Cell(2L, ton)),
           tang = Cell.expect(toon).tail;
    for ( Object tank : new List(tang) ) {
      Object wall = Tank.wash(0L, 80L, tank);
      for ( Object tape : new List(wall) ) {
        System.err.println(Tape.toString(tape));
      }
    }
  }

}
