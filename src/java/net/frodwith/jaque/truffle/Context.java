package net.frodwith.jaque.truffle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kenai.jffi.Array;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.BlockException;
import net.frodwith.jaque.Caller;
import net.frodwith.jaque.KickLabel;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Fragment;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.data.Tank;
import net.frodwith.jaque.data.Tape;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.truffle.driver.Arm;
import net.frodwith.jaque.truffle.nodes.FunctionNode;
import net.frodwith.jaque.truffle.nodes.JaqueNode;
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
import net.frodwith.jaque.truffle.nodes.formula.hint.SlogHintNode;
import net.frodwith.jaque.truffle.nodes.formula.hint.StackHintNode;
import net.frodwith.jaque.truffle.nodes.jet.ImplementationNode;

public class Context {
  
  private static class Invocation {
    public String name;
    public long begin;
    public long last;
  }
  
  private static class Stats {
    public long total;
    public long own;
  }
  
  public final Map<KickLabel, CallTarget> kicks;
  public final Map<Cell, CallTarget> nocks;
  public final Map<String, Arm[]> drivers;
  public final HashMap<Cell, Location> locations;

  public Map<String,Stats> times = null;
  public Stack<Invocation> calls = new Stack<Invocation>();
  public final boolean profile;
  
  public final CallTarget kickTarget;
  private final static Logger logger = Logger.getGlobal();
  
  public void come(String name) {
    Stats st;

    Invocation i = new Invocation();
    i.name = name;

    long now = i.last = i.begin = System.nanoTime();

    if ( times.containsKey(name) ) {
      st = times.get(name);
    }
    else {
      st = new Stats();
      times.put(name, st);
    }
    if ( !calls.isEmpty() ) {
      Invocation up = calls.peek();
      Stats stu = times.get(up.name);
      stu.own += now - up.last;
    }
    calls.push(i);
  }
  
  // returns string because there's no Thunk in java.util.function...
  public String flee() {
    long now = System.nanoTime();
    Invocation done = calls.pop();
    Stats st = times.get(done.name);
    st.total += now - done.begin;
    st.own   += now - done.last;

    if ( !calls.isEmpty() ) {
      Invocation up = calls.peek();
      up.last = now;
    }

    return null;
  }
  
  public Stack<Road> levels;
  public Caller caller = null;
  
  public Context(Arm[] arms) {
    this(arms, false);
  }
  
  public Context(Arm[] arms, boolean profile) {
    this.kicks = new HashMap<KickLabel, CallTarget>();
    this.nocks = new HashMap<Cell, CallTarget>();
    this.locations = new HashMap<Cell, Location>();
    this.drivers = new HashMap<String, Arm[]>();
    this.times = new HashMap<String,Stats>();
    this.profile = profile;
    
    levels = new Stack<Road>();
    levels.push(new Road(null));
    
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

    this.kickTarget = compileTarget(new Qual(9L, 2L, 0L, 1L).toCell());
  }
  
  @TruffleBoundary
  public void err(String s) {
    logger.severe(s);
  }

  @TruffleBoundary
  public void print(String s) {
    System.out.println(s);
  }
  
  private Cell mutateGate(Cell gate, Object sample) {
    Cell pay = Cell.expect(gate.tail),
         yap = new Cell(sample, pay.tail);
    return  new Cell(gate.head, yap);
  }
  
  public Object kick(Cell gate) {
    return kickTarget.call(gate);
  }
  
  public Object slam(Cell gate, Object sample) {
    return kick(mutateGate(gate, sample));
  }
  
  public Function<Object,Object> internalSlam(VirtualFrame frame, JaqueNode holder, Cell core) {
    Cell bat = Cell.expect(core.head);
    Cell pay = Cell.expect(core.tail);
    ImplementationNode jet = gateJet(bat);
    
    if ( null != jet ) {
      holder.replace(jet);
      return (sam) -> jet.doJet(frame, new Cell(bat, new Cell(sam, pay.tail)));
    }
    else {
      FormulaNode fn = parseCell(bat, false);
      holder.replace(fn);
      return (sam) -> {
        Object old = FunctionNode.getSubject(frame);
        FunctionNode.setSubject(frame, new Cell(bat, new Cell(sam, pay.tail)));
        Object pro = fn.executeGeneric(frame);
        FunctionNode.setSubject(frame, old);
        return pro;
      };
    }
    
  }
  
  @TruffleBoundary
  public ImplementationNode findImplementation(Location loc, Object axis, Cell formula) {
    CompilerAsserts.neverPartOfCompilation();
    if ( null == loc ) {
      return null;
    }
    Class<? extends ImplementationNode> klass = getDriver(loc, axis);
    if ( null == klass ) {
      return null;
    }
    try {
      FormulaNode fallback = parseCell(formula, false);
      Method cons = klass.getMethod("create", Context.class, FormulaNode.class);
      return (ImplementationNode) cons.invoke(null, this, fallback);
    }
    catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;   
  }

  @TruffleBoundary
  private ImplementationNode gateJet(Cell battery) {
    return findImplementation(locations.get(battery), 2L, battery);
  }

  public Object wrapSlam(Cell gate, Object sample) {
    return wrapCall(kickTarget, mutateGate(gate, sample));
  }
  
  public Function<Object, Object> slammer(Cell gate) {
    return (sam) -> slam(gate, sam);
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
          Cell cell = TypesGen.asCell(arg);
          Cell neck = TypesGen.asCell(cell.tail);
          // a lot of these hints do something with the product, so aren't in tail
          //FormulaNode next = parseCell(TypesGen.asCell(cell.tail), tail);
          Object  head = cell.head;
          if ( Noun.isAtom(head) ) {
            // What do you do with static hints you don't recognize? Nothing...
            err("unrecognized static hint: " + Atom.toString(head));
            return parseCell(neck, tail);
          }
          else {
            Cell dyn     = TypesGen.asCell(head);
            FormulaNode dynF = parseCell(TypesGen.asCell(dyn.tail), false);
            Object kind  = dyn.head;

            if ( Atom.MEMO.equals(kind) ) {
              return new MemoHintNode(parseCell(neck, false));
            }
            else if ( Atom.FAST.equals(kind) ) {
              return new FastHintNode(this, dynF, parseCell(neck, false));
            }
            else if ( Atom.SLOG.equals(kind) ) {
              return new SlogHintNode(this, dynF, parseCell(neck, true));
            }
            else if ( Atom.SPOT.equals(kind) ) {
              return new StackHintNode(this, Atom.SPOT, dynF, parseCell(neck, false));
            }
            else if ( Atom.MEAN.equals(kind) ) {
              return new StackHintNode(this, Atom.MEAN, dynF, parseCell(neck, false));
            }
            else if ( Atom.LOSE.equals(kind) ) {
              return new StackHintNode(this, Atom.LOSE, dynF, parseCell(neck, false));
            }
            else if ( Atom.HUNK.equals(kind) ) {
              return new StackHintNode(this, Atom.HUNK, dynF, parseCell(neck, false));
            }
            else {
              err("unrecognized dynamic hint: " + Atom.toString(kind));
              return new DiscardHintNode(dynF, parseCell(neck, tail));
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
          throw new Bail();
        }
      }
    }
  }
  
  private CallTarget compileTarget(Cell formula) {
    FormulaNode program = parseCell(formula, true);
    JaqueRootNode root  = new JaqueRootNode(program);
    CallTarget inner    = Truffle.getRuntime().createCallTarget(root);
    TopRootNode top     = new TopRootNode(inner);

    return Truffle.getRuntime().createCallTarget(top);
  }
  
  private Object wrapCall(CallTarget tgt, Object subject) {
    try {
      return tgt.call(subject);
    }
    catch (Bail e) {
      dumpHoonStack();
      throw e;
    }
  }
  
  public void dumpHoonStack() {
    if ( null != caller ) {
      Cell tone = new Cell(2L, levels.peek().stacks);
      Cell toon = Cell.expect(caller.kernel("mook", tone));
      assert(Atom.equals(2L, toon.head));
      StringBuilder buf = new StringBuilder();
      for ( Object tank : new List(toon.tail) ) {
        Object wall = Tank.wash(0L, 80L, tank);
        for ( Object tape : new List(wall) ) {
          buf.append(Tape.toString(tape));
          buf.append('\n');
        }
      }
      err(buf.toString());
    }
  }

  /* Top-level interpeter entry point */
  public Object nock(Object subject, Cell formula) {
    return wrapCall(compileTarget(formula), subject);
  }
  
  public void dumpProfile() {
    for ( Map.Entry<String, Stats> kv : times.entrySet() ) {
      Stats st = kv.getValue();
      System.out.format("%s\t%s\t%s\n", kv.getKey(), st.own, st.total);
    }
  }
  
  public Object softEscape(Object ref, Object gof) {
    Road r = new Road(levels.peek().escapeGate);
    levels.push(r);
    try {
      return slam(r.escapeGate, new Cell(ref, gof));
    }
    finally {
      levels.pop();
    }
  }
  
  public Cell softRun(Cell escapeGate, Supplier<Object> fn) {
    Road r = new Road(escapeGate);
    levels.push(r);
    try {
      return new Cell(0L, fn.get());
    }
    // XX: Ctrl-C is not handled yet
    catch (BlockException e) {
      return new Trel(1L, e.gof, 0L).toCell();
    }
    catch (Bail e) {
      return new Cell(2L, r.stacks);
    }
    catch (StackOverflowError e) {
      return new Trel(3L, Atom.mote("over"), r.stacks).toCell();
    }
    catch (OutOfMemoryError e) {
      return new Trel(3L, Atom.mote("meme"), r.stacks).toCell();
    }
    finally {
      levels.pop();
    }
  }
  
  @TruffleBoundary
  public void stackPush(Cell item) {
    Road r = levels.peek();
    r.stacks = new Cell(item, r.stacks);
  }
  
  @TruffleBoundary
  public void stackPop() {
    Road r = levels.peek();
    r.stacks = Cell.expect(r.stacks).tail;
  }
  
  public class Road {
    public Cell escapeGate = null;
    public Object stacks = 0L;
    
    public Road(Cell escapeGate) {
      this.escapeGate = escapeGate;
    }
  }

  @TruffleBoundary
  public Object kernel(String name, Object sample) {
    return caller.kernel(name, sample);
  }
  
  @TruffleBoundary
  public void slog(Object tank) {
    caller.slog(tank);
  }

  @TruffleBoundary
  public Class<? extends ImplementationNode> getDriver(Location loc, Object axis) {
    String k = loc.label;
    if ( !drivers.containsKey(k) ) {
      return null;
    }

    for ( Arm a : drivers.get(k) ) {
      if ( a.matches(loc, axis) ) {
        return a.driver;
      }
    }

    return null;
  }

  @TruffleBoundary
  public void register(Cell battery, Location location) {
    locations.put(battery, location);
    caller.register(battery, location);
  }
}
