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
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.BlockException;
import net.frodwith.jaque.Caller;
import net.frodwith.jaque.Fail;
import net.frodwith.jaque.Interrupt;
import net.frodwith.jaque.Location;
import net.frodwith.jaque.blok.Block;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.List;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Tank;
import net.frodwith.jaque.data.Tape;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.truffle.bloc.BlockNode;
import net.frodwith.jaque.truffle.bloc.BlockRootNode;
import net.frodwith.jaque.truffle.driver.Arm;
import net.frodwith.jaque.truffle.jet.ImplementationNode;

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
  public transient Map<Cell, CallTarget> evalBlocks;
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
    evalBlocks = new HashMap<Cell, CallTarget>();
    times = null;
    calls = new Stack<Invocation>();
    levels = new Stack<Road>();
    levels.push(new Road(null));
    print("running on " + Truffle.getRuntime().getName());
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
    Cell pay = Cell.orBail(gate.tail),
         yap = new Cell(sample, pay.tail);
    return  new Cell(gate.head, yap);
  }
  
  public Object kick(Object gate, Object axis) {
    try {
      Axis a = new Axis(axis);
      Cell formula = Cell.expect(a.fragment(gate));
      return nock(gate, formula);
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
  }
  
  public Object slam(Cell gate, Object sample) {
    return kick(mutateGate(gate, sample), 2L);
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
  
  // nock: expression language, represented as cells
  // blok: stack-language with nock fundamental ops
  // bloc: cps-transformed blok, tail calls only, truffle nodes
  @TruffleBoundary
  public CallTarget evalByCell(Cell label) {
    CompilerAsserts.neverPartOfCompilation();
    CallTarget t = evalBlocks.get(label);
    if ( null == t ) {
      try {
        t = Truffle.getRuntime().createCallTarget(new BlockRootNode(Block.compile(label).cps(this)));
        evalBlocks.put(label, t);
      }
      catch ( UnexpectedResultException e ) {
        throw new Bail();
      }
    }
    return t;
  }
  
  @TruffleBoundary
  public Object nock(Object subject, Cell formula) {
    try {
      BlockNode main = Block.compile(formula).cps(this);
      net.frodwith.jaque.truffle.bloc.TopRootNode root = new net.frodwith.jaque.truffle.bloc.TopRootNode(main);
      return Truffle.getRuntime().createCallTarget(root).call(subject);
    }
    catch ( UnexpectedResultException e ) {
      throw new Bail();
    }
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
      Cell toon = Cell.orBail(caller.kernel("mook", tone));
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
      switch ( Atom.intOrBail(toon.head) ) {
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
    try {
      Object mok = caller.kernel("mook", new Cell(2L, tax));
      return new Cell(cod, Cell.orBail(mok).tail);
    }
    catch ( Exception e ) {
      err("mook failure!");
      return new Cell(cod, 0L);
    }
  }
  
  public Cell softRun(Cell escapeGate, Supplier<Object> fn) {
    Road r = new Road(escapeGate);
    levels.push(r);
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
      throw new Fail(OVER, r.stacks);
    }
    catch (OutOfMemoryError e) {
      throw new Fail(MEME, r.stacks);
    }
    catch (Fail e) {
      err(Atom.cordToString(e.mote));
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
    r.stacks = Cell.orBail(r.stacks).tail;
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
  public Object hook(Cell cor, String name) {
    Cell bat = Cell.orBail(cor.head);
    Location loc = locations.get(bat);
    if ( null == loc ) {
      return null;
    }
    Object axis = loc.nameToAxis.get(name);
    if ( null == axis ) {
      // the caller wants a deeper core
      Object inn = new Axis(loc.axisToParent).fragOrBail(cor);
      return hook(Cell.orBail(inn), name);
    }
    else {
      return kick(cor, axis);
    }
  }

  public CallTarget targetByCell(Cell expectCell) {
    // TODO Auto-generated method stub
    return null;
  }
}
