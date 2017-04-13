package net.frodwith.jaque.truffle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;

import net.frodwith.jaque.KickLabel;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.driver.Arm;
import net.frodwith.jaque.truffle.driver.AxisArm;
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
import net.frodwith.jaque.truffle.nodes.formula.hint.DiscardHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.FastHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.MemoHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.StackHintNode;
import net.frodwith.jaque.truffle.nodes.jet.AddNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.BexNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.CanNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.CapNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.CatNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.ConNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.CutNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.DecNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.DisNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.DivNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.DvrNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.EndNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.GteNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.GthNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.LshNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.LteNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.LthNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.MasNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.MetNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.MixNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.ModNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.MugNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.MulNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.PegNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.RapNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.RepNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.RipNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.RshNodeGen;

public class Context {
  
  public final Map<KickLabel, CallTarget> kicks;
  public final Map<Cell, CallTarget> nocks;
  public final Map<Cell, Location> locations;
  public final Map<String, Arm[]> drivers;
  
  public final Stack<Object> spot;
  public final Stack<Object> mean;
  public final Stack<Object> hunk;
  public final Stack<Object> lose;

  public Context(Arm[] arms) {
    this.kicks = new HashMap<KickLabel, CallTarget>();
    this.nocks = new HashMap<Cell, CallTarget>();
    this.locations = new HashMap<Cell, Location>();
    this.drivers = new HashMap<String, Arm[]>();
    
    this.spot = new Stack<Object>();
    this.mean = new Stack<Object>();
    this.hunk = new Stack<Object>();
    this.lose = new Stack<Object>();

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
          if ( Atom.isZero(arg) ) {
            return new BailNode();
          }
          if ( Noun.equals(1L, arg) ) {
            return new IdentityNode();
          }
          else {
            return new FragmentNode(arg);
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
          Object axis = c.head;
          FormulaNode core = parseCell(t, false);

          return KickNodeGen.create(core, this, tail, Atom.cap(axis) == 2, axis);
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
            else if ( Atom.SPOT.equals(kind) ) {
              return new StackHintNode(spot, dynF, next);
            }
            else if ( Atom.MEAN.equals(kind) ) {
              return new StackHintNode(mean, dynF, next);
            }
            else if ( Atom.LOSE.equals(kind) ) {
              return new StackHintNode(lose, dynF, next);
            }
            else if ( Atom.HUNK.equals(kind) ) {
              return new StackHintNode(hunk, dynF, next);
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
