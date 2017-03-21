package net.frodwith.jaque.truffle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;

import net.frodwith.jaque.KickLabel;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragmenter;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.driver.Arm;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.TopRootNode;
import net.frodwith.jaque.truffle.nodes.formula.BumpNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.ComposeNode;
import net.frodwith.jaque.truffle.nodes.formula.ConsNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.DeepNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.EscapeNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.NockNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.PushNode;
import net.frodwith.jaque.truffle.nodes.formula.SameNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.hint.DiscardHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.FastHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.MemoHintNode;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;
import net.frodwith.jaque.truffle.nodes.formula.FragmentNode;
import net.frodwith.jaque.truffle.nodes.formula.IfNode;
import net.frodwith.jaque.truffle.nodes.formula.KickNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.LiteralCellNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralIntArrayNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralLongNode;

public class Context {
  
  public final Map<KickLabel, CallTarget> kicks;
  public final Map<Cell, CallTarget> nocks;
  public final Map<Cell, Location> locations;
  public final Map<String, Arm[]> drivers;
  
  public Context(Arm[] arms) {
    this.kicks = new HashMap<KickLabel, CallTarget>();
    this.nocks = new HashMap<Cell, CallTarget>();
    this.locations = new HashMap<Cell, Location>();
    this.drivers = new HashMap<String, Arm[]>();
    
    Map<String, List<Arm>> temp = new HashMap<String, List<Arm>>();
    if ( null != arms ) {
      for ( Arm a : arms ) {
        List<Arm> push = temp.get(a.label);
        if ( null == push ) {
          push = new LinkedList<Arm>();
          temp.put(a.label, push);
        }
        push.add(a);
      }
    }
    
    for ( Map.Entry<String, List<Arm>> e : temp.entrySet() ) {
      drivers.put(e.getKey(), e.getValue().toArray(new Arm[0]));
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
          Fragmenter fragmenter = new Fragmenter(arg);
          return new FragmentNode(fragmenter);
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
          NockDispatchNode dispatch = NockDispatchNodeGen.create(this, tail);
          return NockNodeGen.create(left, right, dispatch);
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
          Object axis = c.head;
          FormulaNode core = parseCell(t, false);
          FragmentationNode fragment = new FragmentationNode(axis);

          return KickNodeGen.create(core, this, tail, Atom.cap(axis) == 2, axis, fragment);
        }
        case 10: {
          Cell    cell = TypesGen.asCell(arg);
          FormulaNode next = parseCell(TypesGen.asCell(cell.tail), tail);
          Object  head = cell.head;
          if ( Noun.isAtom(head) ) {
            if ( Atom.MEMO.equals(head) ) {
              return new MemoHintNode(next);
            }
            else {
              // What do you do with static hints you don't recognize? Nothing...
              return next;
            }
          }
          else {
            Cell dyn     = TypesGen.asCell(head);
            FormulaNode dynF = parseCell(TypesGen.asCell(dyn.tail), false);
            Object kind  = dyn.head;
            if ( Atom.FAST.equals(kind) ) {
              return new FastHintNode(this, dynF, next);
            }
            else {
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
          throw new IllegalArgumentException();
        }
      }
    }
  }

  /* Top-level interpeter entry point */
  public Object nock(Object subject, Cell formula) {
    FormulaNode program = parseCell(formula, true);
    JaqueRootNode root  = new JaqueRootNode(program);
    CallTarget target   = Truffle.getRuntime().createCallTarget(root);
    TopRootNode top     = new TopRootNode(target);
    return Truffle.getRuntime().createCallTarget(top).call(subject);
  }
  
}
