package net.frodwith.jaque.truffle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Layout;
import com.oracle.truffle.api.object.Shape;

import net.frodwith.jaque.KickLabel;
import net.frodwith.jaque.Registration;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragment;
import net.frodwith.jaque.data.JaqueObjectType;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.truffle.driver.Arm;
import net.frodwith.jaque.truffle.driver.AxisArm;
import net.frodwith.jaque.truffle.nodes.FragmentationNode;
import net.frodwith.jaque.truffle.nodes.JaqueRootNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNode;
import net.frodwith.jaque.truffle.nodes.NockDispatchNodeGen;
import net.frodwith.jaque.truffle.nodes.TopRootNode;
import net.frodwith.jaque.truffle.nodes.formula.BailNode;
import net.frodwith.jaque.truffle.nodes.formula.BumpNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.ComposeNode;
import net.frodwith.jaque.truffle.nodes.formula.ConsNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.DeepNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.EscapeNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.PushNode;
import net.frodwith.jaque.truffle.nodes.formula.SameNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.hint.DiscardHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.FastHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.MemoHintNode;
import net.frodwith.jaque.truffle.nodes.jet.AddNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.DecrementNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.LessThanNodeGen;
import net.frodwith.jaque.truffle.nodes.jet.SubtractNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.FormulaNode;
import net.frodwith.jaque.truffle.nodes.formula.FragmentNode;
import net.frodwith.jaque.truffle.nodes.formula.IdentityNode;
import net.frodwith.jaque.truffle.nodes.formula.IfNode;
import net.frodwith.jaque.truffle.nodes.formula.KickNodeGen;
import net.frodwith.jaque.truffle.nodes.formula.LiteralCellNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralIntArrayNode;
import net.frodwith.jaque.truffle.nodes.formula.LiteralLongNode;
import net.frodwith.jaque.truffle.nodes.formula.NockNode;

public class Context {
  private static final Layout LAYOUT = Layout.createLayout();
  private static final Shape INITIAL = LAYOUT.createShape(JaqueObjectType.INSTANCE).
        defineProperty("hashed", false, 0).
        defineProperty("hash", 0, 0);

  
  public final Map<KickLabel, CallTarget> kicks;
  public final Map<DynamicObject, CallTarget> nocks;
  public final Map<DynamicObject, Registration> locations;
  public final Map<String, Arm[]> drivers;
  
  public Context(Arm[] arms) {
    this.kicks = new HashMap<KickLabel, CallTarget>();
    this.nocks = new HashMap<DynamicObject, CallTarget>();
    this.locations = new HashMap<DynamicObject, Registration>();
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
  
  public static DynamicObject cons(Object head, Object tail) {
    return INITIAL.
        defineProperty(Fragment.HEAD, head, 0).
        defineProperty(Fragment.TAIL, tail, 0).
        newInstance();
  }
  
  /* copied from simplelanguage */
  public static boolean isJaqueObject(TruffleObject value) {
    return LAYOUT.getType().isInstance(value) 
        && LAYOUT.getType().cast(value).getShape().getObjectType()
        == JaqueObjectType.INSTANCE;
  }
  
  /* If there was a node for this, we could profile it, but it's a slow path operation
   * (in general, we cache formulas) so there's not(?) much benefit to making it a node.
   */
  public FormulaNode parseCell(DynamicObject src, boolean tail) {
    CompilerAsserts.neverPartOfCompilation();

    Object op  = Cell.head(src),
           arg = Cell.tail(src);

    if ( Noun.isCell(op) ) {
      return ConsNodeGen.create(
          parseCell(Noun.asCell(op), false),
          parseCell(Noun.asCell(arg), false));
    }
    else {
      switch ( (int) TypesGen.asLong(op) ) {
        case 0: {
          if ( Atom.isZero(arg) ) {
            return new BailNode();
          }
          else if ( Atom.equals(arg, 1L) ) {
            return new IdentityNode();
          }
          else {
            return new FragmentNode(arg);
          }
        }
        case 1: {
          if ( Noun.isCell(arg) ) {
            return new LiteralCellNode(Noun.asCell(arg));
          }
          else if ( TypesGen.isLong(arg) ) {
            return new LiteralLongNode(TypesGen.asLong(arg));
          }
          else {
            return new LiteralIntArrayNode(TypesGen.asIntArray(arg));
          }
        }
        case 2: {
          DynamicObject c = Noun.asCell(arg),
              h = Noun.asCell(Cell.head(c)),
              t = Noun.asCell(Cell.tail(c));

          FormulaNode left = parseCell(h, false),
                     right = parseCell(t, false);
          return new NockNode(left, right, this, tail);
        }
        case 3:
          return DeepNodeGen.create(parseCell(Noun.asCell(arg), false));
        case 4:
          return BumpNodeGen.create(parseCell(Noun.asCell(arg), false));
        case 5: {
          DynamicObject c = Noun.asCell(arg),
              h = Noun.asCell(Cell.head(c)),
              t = Noun.asCell(Cell.tail(c));

          return SameNodeGen.create(
              parseCell(h, false),
              parseCell(t, false));
        }
        case 6: {
          DynamicObject trel = Noun.asCell(arg),
              pair = Noun.asCell(Cell.tail(trel)),
              one  = Noun.asCell(Cell.head(trel)),
              two  = Noun.asCell(Cell.head(pair)),
              tre  = Noun.asCell(Cell.tail(pair));

          return new IfNode(
              parseCell(one, false),
              parseCell(two, tail),
              parseCell(tre, tail));
        }
        case 7: {
          DynamicObject c = Noun.asCell(arg),
              h = Noun.asCell(Cell.head(c)),
              t = Noun.asCell(Cell.tail(c));

          return new ComposeNode(
              parseCell(h, false), 
              parseCell(t, tail));
        }
        case 8: {
          DynamicObject c = Noun.asCell(arg),
              h = Noun.asCell(Cell.head(c)),
              t = Noun.asCell(Cell.tail(c));

          return new PushNode(
              parseCell(h, false), 
              parseCell(t, tail));
        }
        case 9: {
          DynamicObject c = Noun.asCell(arg),
              t = Noun.asCell(Cell.tail(c));

          Object axis = Cell.head(c);
          FormulaNode core = parseCell(t, false);

          return KickNodeGen.create(core, this, tail, Atom.cap(axis) == 2, axis);
        }
        case 10: {
          DynamicObject c = Noun.asCell(arg);
          FormulaNode next = parseCell(Noun.asCell(Cell.tail(c)), tail);
          Object head = Cell.head(c);
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
            DynamicObject c2 = Noun.asCell(head);
            FormulaNode dynF = parseCell(Noun.asCell(Cell.tail(c2)), false);
            Object kind  = Cell.head(c2);
            if ( Atom.FAST.equals(kind) ) {
              return new FastHintNode(this, dynF, next);
            }
            else {
              return new DiscardHintNode(dynF, next);
            }
          }
        }
        case 11: {
          DynamicObject c = Noun.asCell(arg);
          return EscapeNodeGen.create(
              parseCell(Noun.asCell(Cell.head(c)), false),
              parseCell(Noun.asCell(Cell.tail(c)), false),
              this);
        }
        default: {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  /* Top-level interpeter entry point */
  public Object nock(Object subject, DynamicObject formula) {
    FormulaNode program = parseCell(formula, true);
    JaqueRootNode root  = new JaqueRootNode(program);
    CallTarget target   = Truffle.getRuntime().createCallTarget(root);
    TopRootNode top     = new TopRootNode(target);
    return Truffle.getRuntime().createCallTarget(top).call(subject);
  }

  public static void main(String[] args) {
    Arm[] drivers = new Arm[] {
      new AxisArm("kmat/math/dec", 2L, DecrementNodeGen.class),
      new AxisArm("kmat/math/add", 2L, AddNodeGen.class),
      new AxisArm("kmat/math/sub", 2L, SubtractNodeGen.class),
      new AxisArm("kmat/math/lth", 2L, LessThanNodeGen.class),
    };
    Context c = new Context(drivers);
    try {
      byte[] bytes = Files.readAllBytes(Paths.get("/home/pdriver/math-kernel.nock"));
      String fos   = new String(bytes, "UTF-8").trim();
      DynamicObject formula = Noun.asCell(Noun.parse(fos));
      DynamicObject kernel  = Noun.asCell(c.nock(0L, formula));
      String calls = "[8 [9 22 0 1] 9 2 [0 4] [1 15] 0 11]";
      DynamicObject call    = Noun.asCell(Noun.parse(calls));
      System.out.println(Atom.cordToString(c.nock(kernel, call)));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
