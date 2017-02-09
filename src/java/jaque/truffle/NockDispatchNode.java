
/* Continuing evaluation of a 2 or 9 involves nocking on an unknown (at
 * compile time) formula. However, in many (probably all optimizable) cases,
 * the formula to be evaluated doesn't change on subsequent invocations, i.e.
 * it is constant. If we know it's constant, we can inline through it - we
 * have a tree of nodes for it. We can thus profitably assume that for any
 * given jumpNode, the first thing it is called with is the thing it will
 * always be called with - and if it is not, we bail out and do the slow path.
 * */

@TypeSystemReference(NockTypes.class)
public abstract class NockDispatchNode extends Node {

  public abstract Noun executeNock(VirtualFrame frame, Noun subject, Cell formula);

  @Specialization(guards = {"formula.equals(cachedFormula)"})
  protected static Noun doStatic(VirtualFrame frame, Noun subject, Cell formula,
    @Cached("formula") Cell cachedFormula,
    @Cached("create(targetFromCell(frame, cachedFormula))") DirectCallNode callNode)
  {
    Object[] args = { subject, frame.getArguments()[1] };
    callNode.call(frame, args);
  }

  @Specialization(replaces = {"doStatic"})
  protected static Noun doDynamic(VirtualFrame frame, Noun subject, Formula formulaF,
     @Cached("create()") IndirectCallNode callNode)
  {
    Object[] args = { subject, frame.getArguments()[1] };
    callNode.call(frame, targetFromCell(frame, formula), args);
  }

  protected static CallTarget targetFromCell(VirtualFrame frame, Cell cell) {
    Map<Cell,CallTarget> map = frame.getArguments()[1];
    CallTarget target = map.get(cell);

    if ( null == target ) {
      target = Truffle.getRuntime().createCallTarget(Formula.fromNoun(cell));
      map.put(cell, target);
    }

    return target;
  }
}
