package net.frodwith.jaque.truffle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.BlockException;
import net.frodwith.jaque.Caller;
import net.frodwith.jaque.Fail;
import net.frodwith.jaque.Interrupt;
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

public class Context implements Serializable {
  
  private static class Invocation {
    public String name;
    public long begin;
    public long last;
  }
  
  private static class Stats {
    public long total;
    public long own;
  }
  
  private static final Object 
    INTR = Atom.mote("intr"),
    OVER = Atom.mote("over"),
    MEME = Atom.mote("meme"),
    EXIT = Atom.mote("exit");
  
  private static final Cell nullGul = new Trel(new Trel(1L, 0L, 0L).toCell(), 0L, 0L).toCell();
  
  // these can't be serialized
  public transient Map<KickLabel, CallTarget> kicks;
  public transient Map<Object, CallTarget> simpleKicks;
  public transient Map<Cell, CallTarget> nocks;
  public transient Caller caller;

  // these are per-run, though they could be serialized */
  public transient Map<String,Stats> times;
  public transient Stack<Invocation> calls;
  @CompilationFinal public transient boolean profile;

  // this is kind of a hack for soft, and should probably work differently
  // anyways we don't serialize it
  public transient Stack<Road> levels;

  // durable state
  public final HashMap<Cell, Location> locations;
  public final Map<String, Arm[]> drivers;

  // this being static doesn't need special serialization logic
  private final static Logger logger = Logger.getGlobal();
  
  private void initTransients() {
    caller = null;
    profile = false;
    kicks = new HashMap<KickLabel, CallTarget>();
    simpleKicks = new HashMap<Object, CallTarget>();
    nocks = new HashMap<Cell, CallTarget>();
    times = null;
    calls = new Stack<Invocation>();
    levels = new Stack<Road>();
    levels.push(new Road(null));
  }
  
  // anything that varies per run gets sent through here
  public void wake(Arm[] arms, Caller caller, boolean profile) {
    this.profile = profile;
    this.caller = caller;
    this.drivers.clear();

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
      this.drivers.put(e.getKey(), al.toArray(aa));
    }
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    initTransients();
  }
  
  public Context() {
    this.locations = new HashMap<Cell, Location>();
    this.drivers = new HashMap<String, Arm[]>();
    initTransients();
  }
  
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
  
  @TruffleBoundary
  public void err(String s) {
    logger.severe(s);
  }

  @TruffleBoundary
  public void print(String s) {
    logger.info(s);
  }
  
  private Cell mutateGate(Cell gate, Object sample) {
    Cell pay = Cell.expect(gate.tail),
         yap = new Cell(sample, pay.tail);
    return  new Cell(gate.head, yap);
  }
  
  public Object kick(Cell gate, Object axis) {
    CallTarget t = simpleKicks.get(axis);
    if ( null == t ) {
      t = compileTarget(parseCell(new Qual(9L, axis, 0L, 1L).toCell(), false));
      simpleKicks.put(axis, t);
    }
    return t.call(gate);
  }
  
  public Object slam(Cell gate, Object sample) {
    return kick(mutateGate(gate, sample), 2L);
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
    return wrap(() -> kick(mutateGate(gate, sample), 2L));
  }
  
  public Cell softSlam(Cell gate, Object sample) {
    return softRun(nullGul, () -> kick(mutateGate(gate, sample), 2L));
  }
  
  public Function<Object, Object> slammer(Cell gate) {
    return (sam) -> slam(gate, sam);
  }
  
  @TruffleBoundary
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
              return new SlogHintNode(this, dynF, parseCell(neck, tail));
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
  
  private CallTarget compileTarget(FormulaNode program) {
    JaqueRootNode root  = new JaqueRootNode(program);
    CallTarget inner    = Truffle.getRuntime().createCallTarget(root);
    TopRootNode top     = new TopRootNode(inner);

    return Truffle.getRuntime().createCallTarget(top);
  }
  
  private Object wrap(Supplier<Object> doer) {
    try {
      return doer.get();
    }
    catch (Bail e) {
      dumpHoonStack();
      throw e;
    }
  }
  
  @TruffleBoundary
  public void dumpHoonStack() {
    if ( null != caller ) {
      Cell tone = new Cell(2L, levels.peek().stacks);
      Cell toon = Cell.expect(caller.kernel("mook", tone));
      assert(Atom.equals(2L, toon.head));
      dumpStack(toon.tail);
    }
  }
  
  @TruffleBoundary
  public void dumpStack(Object trace) {
    StringBuilder buf = new StringBuilder();
    for ( Object tank : new List(trace) ) {
      Object wall = Tank.wash(0L, 80L, tank);
      for ( Object tape : new List(wall) ) {
        buf.append(Tape.toString(tape));
        buf.append('\n');
      }
    }
    err(buf.toString());
  }

  /* Top-level interpeter entry point */
  public Object nock(Object subject, Cell formula) {
    return wrap(() -> compileTarget(parseCell(formula, true)).call(subject));
  }
  
  @TruffleBoundary
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
  
  //Suppler<toon>
  public Cell softTop(Supplier<Cell> f) {
    Object cod, tax;
    try {
      Cell toon = f.get();
      switch ( Atom.expectInt(toon.head) ) {
      case 0:
        return toon;
      case 1:
        err("toplevel block");
        assert(false); // don't use toplevel .^!!
        return null;
      case 2:
        cod = EXIT;
        tax = toon.tail;
        break;
      default:
        err("bad toplevel toon" + Noun.toString(toon));
        assert(false);
        return null;
      }
    }
    catch (Fail e) {
      cod = e.mote;
      tax = e.trace;
    }
    Object mok = caller.kernel("mook", new Cell(2L, tax));
    return new Cell(cod, Cell.expect(mok).tail);
  }
  
  public Cell softRun(Cell escapeGate, Supplier<Object> fn) {
    Road r = new Road(escapeGate);
    levels.push(r);
    Object trace, fail;
    try {
      return new Cell(0L, fn.get());
    }
    catch (BlockException e) {
      return new Trel(1L, e.gof, 0L).toCell();
    }
    catch (Bail e) {
      return new Cell(2L, r.stacks);
    }
    catch (Interrupt e) {
      throw new Fail(INTR, r.stacks);
    }
    catch (StackOverflowError e) {
      trace = r.stacks;
      throw new Fail(OVER, r.stacks);
    }
    catch (OutOfMemoryError e) {
      throw new Fail(MEME, r.stacks);
    }
    catch (Fail e) {
      throw new Fail(e.mote, List.weld(e.trace, r.stacks));
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
  }
  
  @TruffleBoundary
  public CallTarget getKickTarget(KickLabel label, Supplier<Cell> getFormula) {
    CompilerAsserts.neverPartOfCompilation();
    CallTarget t = kicks.get(label);
    if ( null == t ) {
      Cell formula = getFormula.get();
      Location reg = locations.get(label.battery);
      FormulaNode f = parseCell(formula, true);
      RootNode root = (null == reg) ? new JaqueRootNode(f) : new JaqueRootNode(f, reg.label, label.axis);
      t = Truffle.getRuntime().createCallTarget(root);
      kicks.put(label, t);
    }
    return t;
  }

  @TruffleBoundary
  public Object hook(Cell cor, String name) {
    Cell bat = Cell.expect(cor.head);
    Location loc = locations.get(bat);
    if ( null == loc ) {
      return null;
    }
    Object axis = loc.nameToAxis.get(name);
    if ( null == axis ) {
      // the caller wants a deeper core
      Object inn = new Axis(loc.axisToParent).fragment(cor);
      return hook(Cell.expect(inn), name);
    }
    else {
      return kick(cor, axis);
    }
  }
}
