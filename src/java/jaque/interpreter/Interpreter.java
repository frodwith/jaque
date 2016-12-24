package jaque.interpreter;

import jaque.noun.*;
import gnu.math.MPN;

public final class Interpreter {
  public static final class Bail extends Exception {
  }

  private static Noun frag_word(int a, Noun b) {
    int dep = 31 - MPN.count_leading_zeros(a);

    while ( dep > 0 ) {
      if ( !(b instanceof Cell) ) {
        return null;
      }
      else {
        Cell c = (Cell) b;
        b = ( 0 == (1 & (a >>> --dep)) )
          ? c.p
          : c.q;
      }
    }

    return b;
  }

  private static Noun frag_deep(int a, Noun b) {
    int dep = 32;

    while ( dep > 0 ) {
      if ( !(b instanceof Cell) ) {
        return null;
      }
      else {
        Cell c = (Cell) b;
        b = ( 0 == (1 & (a >>> --dep)) )
          ? c.p
          : c.q;
      }
    }

    return b;
  }

  public final static Noun fragment(Atom a, Noun b) {
    if ( a instanceof DirectAtom ) {
      int val = ((DirectAtom) a).val;
      if (val == 0 ) {
        return null;
      }
      else {
        return frag_word(val, b);
      }
    }
    else {
      int[] w = ((IndirectAtom) a).words;
      int len = w.length;

      b = frag_word(w[--len], b);
      while ( len > 0 ) {
        b = frag_deep(w[--len], b);
        if ( null == b ) {
          return null;
        }
      }
      return b;
    }
  }

  private static Cell forceCell(Noun n) throws Bail {
    if (n instanceof Cell) {
      return (Cell) n;
    }
    else {
      throw new Bail();
    }
  }

  private static Atom forceAtom(Noun n) throws Bail {
    if (n instanceof Atom) {
      return (Atom) n;
    }
    else {
      throw new Bail();
    }
  }

  public final static Result nock(Machine machine, Noun subject, Noun formula) throws Bail {
    while (true) {
      Cell fom       = forceCell(formula);
      Noun operator  = fom.p;
      Noun arguments = fom.q;

      if ( operator instanceof Cell ) {
        Result h = nock(machine, subject, operator),
               t = nock(h.m, subject, arguments);
        Cell c;

        try {
          c = new Cell(h.r, t.r);
        }
        catch (Exception e) {
          throw new Bail();
        }

        return new Result(t.m, c);
      }
      else if ( operator instanceof IndirectAtom ) {
        throw new Bail();
      }
      else switch ( ((DirectAtom) operator).val ) {
        case 0: {
          Atom axis = forceAtom(arguments);
          Noun part = fragment(axis, subject);
          if ( null == part ) {
            throw new Bail();
          }
          else {
            return new Result(machine, part);
          }
        }
        case 1: {
          return new Result(machine, arguments);
        }
        case 2: {
          Cell   a = forceCell(arguments);
          Result s = nock(machine, subject, a.p),
                 f = nock(s.m, subject, a.q);
          subject = s.r;
          formula = f.r;
          machine = f.m;
          continue;
        }
        case 3: {
          Result x = nock(machine, subject, arguments);
          return new Result(x.m, (x.r instanceof Cell) ? Atom.YES : Atom.NO);
        }
        case 4: {
          Result x = nock(machine, subject, arguments);
          Atom   y = forceAtom(x.r);
          return new Result(machine, y.add(Atom.ONE));
        }
        case 5: {
          Result x = nock(machine, subject, arguments);
          Cell   c = forceCell(x.r);
          return new Result(machine, c.p.equals(c.q) ? Atom.YES : Atom.NO);
        }
        case 6: {
          Cell   c = forceCell(arguments);
          Cell  tb = forceCell(c.q);
          Result t = nock(machine, subject, c.p);
          if ( t.r.equals(Atom.YES) ) {
            formula = tb.p;
          }
          else if ( t.r.equals(Atom.NO) ) {
            formula = tb.q;
          }
          else {
            throw new Bail();
          }
          machine  = t.m;
          continue;
        }
        case 7: {
          Cell   c = forceCell(arguments);
          Result x = nock(machine, subject, c.p);
          subject  = x.r;
          machine  = x.m;
          formula  = c.q;
          continue;
        }
        case 8: {
          Cell   c = forceCell(arguments);
          Result x = nock(machine, subject, c.p);
          try {
            subject = new Cell(x.r, subject);
          }
          catch (Exception e) {
            throw new Bail();
          }
          machine  = x.m;
          formula  = c.q;
          continue;
        }
        case 9: {
          Cell   c = forceCell(arguments);
          Atom   a = forceAtom(c.p);
          Result x = nock(machine, subject, c.q);
          Jet    j = x.m.dashboard().find(forceCell(x.r), a);
          if ( null == j ) {
            machine = x.m;
            subject = x.r;
            formula = fragment(a, subject);
            continue;
          }
          else {
            return j.applyCore(x.m, forceCell(x.r));
          }
        }
        case 10: {
          Cell c     = forceCell(arguments);
          boolean hc = c.p instanceof Cell;
          Atom kind  = forceAtom(hc ? ((Cell) c.p).p : c.p);
          Result x   = hc
                     ? nock(machine, subject, ((Cell) c.p).q)
                     : new Result(machine, Atom.ZERO);
          Cell   f   = forceCell(c.q);
          Hint     i = new Hint(kind, x.r, subject, f);
          Result y   = x.m.startHint(i);
          if ( null != y.r ) {
            return y;
          }
          else {
            Result z = nock(y.m, subject, f);
            return new Result(z.m.endHint(i, z.r), z.r);
          }
        }
        case 11: {
          Result x = nock(machine, subject, arguments);
          return x.m.escape(forceCell(x.r));
        }
        default:
          throw new Bail();
      }
    }
  }
}
