@TypeSystemReference(NockTypes.class)
public abstract class KickDispatchNode extends Node {

  public abstract Noun executeKick(VirtualFrame frame, Cell core, Atom axis);

  @Specialization(limit  = 1,
                  guards = {"null != jet",
                            "core.p == cachedBattery",
                            "NockLanguage.findContext(contextNode).fineCore(core)"})
  protected static Noun doFast(VirtualFrame frame, Cell core, Atom axis,
    @Cached("core.p") Noun cachedBattery,
    @Cached("NockLanguage.createFindContextNode()") Node contextNode,
    @Cached("NockLanguage.findContext(contextNode).findJet(core, axis)") Jet jet)
  {
    return NockLanguage.findContext(contextNode).applyJet(jet, core);
  }

  @Specialization(limit    = 1,
                  replaces = "doFast",
                  guards   = {"core.p == cachedBattery"})
  protected static Noun doArm(VirtualFrame frame, Cell core, Atom axis,
    @Cached("core.p") Noun cachedBattery,
    @Cached("getArm(core, axis)") Cell arm,
    @Cached("create()") NockDispatchNode dispatch)
  {
    return dispatch.executeNock(frame, core, arm);
  }

  @Specialization(replaces = {"doFast", "doArm"})
  protected static Noun doSlow(VirtualFrame frame, Cell core, Atom axis,
    @Cached("create()") NockDispatchNode dispatch)
  {
    return dispatch.executeNock(frame, core, getArm(core, axis));
  }

  protected static Cell getArm(Cell core, Atom axis) throws UnexpectedResultException {
    Noun f = Formula.fragment(axis, core);
    if ( arm instanceof Cell ) {
      return (Cell) f;
    }
    else {
      throw new UnexpectedResultException();
    }
  }

}
