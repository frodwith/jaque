package net.frodwith.jaque.nose;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.truffle.TypesGen;

public class Compiler {
  private static class Statement {
    public String target;
    public String op;
    public String[] args;

    public Statement(String target, String op, String...args) {
      this.target = target;
      this.op = op;
      this.args = args;
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder();
      if ( null != target ) {
        sb.append(target);
        sb.append(" = ");
      }
      sb.append(op);
      sb.append("(");
      for ( String a : args ) {
        sb.append(a);
        sb.append(", ");
      }
      sb.replace(sb.length() - 2, sb.length(), ")");
      return sb.toString();
    }
  }
  
  private static class Fresh implements Supplier<String> {
    private int i = 0;

    @Override
    public String get() {
      return String.format("v%d", ++i);
    }
  }
  
  private static class Block {
    private Statement[] body;

    public Block(Statement[] body) {
      this.body = body;
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("(subject) -> {\n");
      for ( Statement st : body ) {
        sb.append("  ");
        sb.append(st.toString());
        sb.append(";\n");
      }
      sb.append("  return product;\n}\n");
      return sb.toString();
    }
  }
  
  public static Block compile(Cell src) throws UnexpectedResultException {
    Deque<Statement> statements = new ArrayDeque<Statement>();
    int len = emitExpr(src, "product", new Fresh(), (st) -> statements.add(st));
    return new Block(statements.toArray(new Statement[len]));
  }
  
  public static int
  emitExpr(Cell src, String result, Supplier<String> nextVar, Consumer<Statement> emit)
  throws UnexpectedResultException {
    int count = 0;
    if ( TypesGen.isCell(src.head) ) {
      String headVar = nextVar.get(),
             tailVar = nextVar.get();
      count += emitExpr(Cell.expect(src.head), headVar, nextVar, emit);
      count += emitExpr(Cell.expect(src.tail), tailVar, nextVar, emit);
      emit.accept(new Statement(result, "cons", headVar, tailVar));
      ++count;
    }
    else switch ( Atom.expectUnsignedInt(src.head) ) {
    case 0:
      emit.accept(new Statement(result, "frag", "subject", Atom.toString(src.tail)));
      ++count;
      break;
    case 1:
      emit.accept(new Statement(result, "quot", Noun.toString(src.tail)));
      ++ count;
      break;
    case 3: {
      String argVar = nextVar.get();
      count += emitExpr(Cell.expect(src.tail), argVar, nextVar, emit);
      emit.accept(new Statement(result, "deep", argVar));
      ++count;
      break;
    }
    case 4: {
      String argVar = nextVar.get();
      count += emitExpr(Cell.expect(src.tail), argVar, nextVar, emit);
      emit.accept(new Statement(result, "bump", argVar));
      ++count;
      break;
    }
    case 5: {
      String aVar = nextVar.get(),
             bVar = nextVar.get();
      Cell arg = Cell.expect(src.tail);
      count += emitExpr(Cell.expect(arg.head), aVar, nextVar, emit);
      count += emitExpr(Cell.expect(arg.tail), bVar, nextVar, emit);
      emit.accept(new Statement(result, "same", aVar, bVar));
      ++count;
      break;
    }
    case 11: {
      Cell arg = Cell.expect(src.tail);
      String refVar = nextVar.get(),
             gofVar = nextVar.get();
      count += emitExpr(Cell.expect(arg.head), refVar, nextVar, emit);
      count += emitExpr(Cell.expect(arg.tail), gofVar, nextVar, emit);
      emit.accept(new Statement(result, "esc", refVar, gofVar));
      ++count;
      break;
    }
    case 7: {
      Cell arg = Cell.expect(src.tail);
      String oldVar = nextVar.get();
      emit.accept(new Statement(oldVar, "load", "subject"));
      ++count;
      count += emitExpr(Cell.expect(arg.head), "subject", nextVar, emit);
      count += emitExpr(Cell.expect(arg.tail), result, nextVar, emit);
      emit.accept(new Statement("subject", "load", oldVar));
      ++count;
      break;
    }
    case 8: {
      Cell arg = Cell.expect(src.tail);
      String oldVar  = nextVar.get(),
             headVar = nextVar.get();
      emit.accept(new Statement(oldVar, "load", "subject"));
      ++count;
      count += emitExpr(Cell.expect(arg.head), headVar, nextVar, emit);
      emit.accept(new Statement("subject", "cons", headVar, oldVar));
      ++count;
      count += emitExpr(Cell.expect(arg.tail), result, nextVar, emit);
      emit.accept(new Statement("subject", "load",oldVar));
      ++count;
      break;
    }
    case 6: {
      String tVar = nextVar.get();
      Trel arg = Trel.expect(src.tail);
      Deque<Statement> yes = new ArrayDeque<Statement>();
      Deque<Statement> no = new ArrayDeque<Statement>();
      int yesCount, noCount;
      count += emitExpr(Cell.expect(arg.p), tVar, nextVar, emit);
      yesCount = emitExpr(Cell.expect(arg.q), result, nextVar, (st) -> yes.add(st));
      noCount  = emitExpr(Cell.expect(arg.r), result, nextVar, (st) -> no.add(st));
      emit.accept(new Statement(null, "skin", tVar, Integer.toString(yesCount + 1)));
      for ( Statement st : yes ) {
        emit.accept(st);
      }
      count += noCount + 1;
      emit.accept(new Statement(null, "skip", Integer.toString(noCount)));
      for ( Statement st : no ) {
        emit.accept(st);
      }
      count += yesCount + 1;
      break;
    }
    case 2: {
      String subVar = nextVar.get(),
             folVar = nextVar.get();
      Cell arg = Cell.expect(src.tail);
      count += emitExpr(Cell.expect(arg.head), subVar, nextVar, emit);
      count += emitExpr(Cell.expect(arg.tail), folVar, nextVar, emit);
      emit.accept(new Statement(result, "eval", subVar, folVar));
      ++count;
      break;
    }
    case 9: {
      Cell arg = Cell.expect(src.tail);
      String coreVar = nextVar.get();
      count += emitExpr(Cell.expect(arg.tail), coreVar, nextVar, emit);
      if ( 2 == Atom.cap(arg.head) ) {
        emit.accept(new Statement(result, "call", coreVar, Noun.toString(arg.head)));
        ++count;
      }
      else {
        // kicking outside of the battery doesn't get optimized the same way,
        // so we just treat it as an eval.
        String folVar = nextVar.get();
        emit.accept(new Statement(folVar, "frag", Noun.toString(arg.head)));
        ++count;
        emit.accept(new Statement(result, "eval", coreVar, folVar));
        ++count;
      }
      break;
    }
    case 10: {
      Cell arg = Cell.expect(src.tail);
      Cell cof = Cell.expect(arg.tail);
      if ( TypesGen.isCell(arg.head) ) {
        Cell dyn    = Cell.expect(arg.head);
        Cell hinf   = Cell.expect(dyn.tail);
        Object kind = dyn.head;
        if ( Atom.MEMO.equals(kind) ) {
          String memoVar = nextVar.get(),
                 folVar  = nextVar.get(),
                 keyVar  = nextVar.get(),
                 gotVar  = nextVar.get();
          emit.accept(new Statement(folVar, "quot", Noun.toString(cof)));
          ++count;
          emit.accept(new Statement(keyVar, "cons", "subject", folVar));
          ++count;
          emit.accept(new Statement(memoVar, "readMemo", keyVar));
          ++count;
          emit.accept(new Statement(gotVar, "deep", memoVar));
          ++count;
          Deque<Statement> statements  = new ArrayDeque<Statement>();
          int cofCount = emitExpr(cof, result, nextVar, (st) -> statements.add(st));
          emit.accept(new Statement(null, "skin", gotVar, "2"));
          ++count;
          emit.accept(new Statement(result, "frag", memoVar, "3"));
          ++count;
          emit.accept(new Statement(null, "skip", Integer.toString(cofCount + 1)));
          ++count;
          for ( Statement st : statements ) {
            emit.accept(st);
          }
          emit.accept(new Statement(null, "putMemo", result));
          ++count;
          count += cofCount;
        }
        else if ( Atom.FAST.equals(kind) ) {
          String hintVar = nextVar.get();
          count += emitExpr(hinf, hintVar, nextVar, emit);
          count += emitExpr(cof, result, nextVar, emit);
          emit.accept(new Statement(null, "fast", hintVar, result));
          ++count;
        }
        else if ( Atom.SLOG.equals(kind) ) {
          String hintVar = nextVar.get();
          count += emitExpr(hinf, hintVar, nextVar, emit);
          emit.accept(new Statement(null, "slog", hintVar));
          ++count;
          count += emitExpr(cof, result, nextVar, emit);
        }
        else if ( Atom.MEAN.equals(kind)
               || Atom.LOSE.equals(kind)
               || Atom.HUNK.equals(kind)
               || Atom.SPOT.equals(kind) ) {
          String hintVar = nextVar.get();
          count += emitExpr(hinf, hintVar, nextVar, emit);
          emit.accept(new Statement(null, "push", hintVar));
          ++count;
          count += emitExpr(cof, result, nextVar, emit);
          emit.accept(new Statement(null, "pop"));
          ++count;
        }
        else {
          // compute the hint, but don't use it
          String hintVar = nextVar.get();
          count += emitExpr(hinf, hintVar, nextVar, emit);
          count += emitExpr(cof, result, nextVar, emit);
        }
      }
      else {
        // no currently recognized static hints
      }
    }
    default:
      throw new UnexpectedResultException(src);
    }
    return count;
  }
  
  public static void main(String[] args) {
    Cell formula = Cell.orBail(Noun.parse(
        "[6 [8 [9 1.567.418 0 31] 9 2 [0 4] [[7 [0 3] 1 2] 7 [0 3] 1 1] 0 11] [1 7.303.014 42] 1 8.020.322 20]"
    /* decrement?
    "[10 [1.851.876.717 [1 8 [9 5.818 0 15] 9 2 [0 4] [7 [0 3] 8 [9 93.108 0 15] 9 2 [0 4] [7 [0 3] " +
    "[1 1.836.020.833 7.561.588 0 2.663.495.029.034.430.894.880.199.458.246.708.989.383.632.228] " +
    "7 [0 3] 1 2.663.495.029.034.430.894.880.199.458.246.708.989.383.632.228] 0 11] 0 11] 0 1] " +
    "6 [5 [1 0] 0 6] [0 0] 8 [1 0] 8 [1 6 [5 [0 30] 4 0 6] [0 6] 9 2 [0 2] [4 0 6] 0 7] 9 2 0 1]"
    */
    ));
    try {
      System.out.println(Noun.toString(formula));
      System.out.println(compile(formula).toString());
    }
    catch ( UnexpectedResultException e) {
      e.printStackTrace();
      System.err.println("oof");
    }
  }
}
