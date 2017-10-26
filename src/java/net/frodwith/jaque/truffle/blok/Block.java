package net.frodwith.jaque.truffle.blok;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Noun;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.truffle.Context;
import net.frodwith.jaque.truffle.TypesGen;
import net.frodwith.jaque.truffle.bloc.BlockNode;
import net.frodwith.jaque.truffle.bloc.BlockRootNode;
import net.frodwith.jaque.truffle.bloc.FlowNode;
import net.frodwith.jaque.truffle.bloc.OpNode;

public class Block {
  public Op[] body;
  
  public Block(Op[] body) {
    this.body = body;
  }
  
  public void addTo(Queue<Op> q) {
    for ( Op o : body ) {
      q.add(o);
    }
  }
  
  // precondition: this block has 0 or 1 tailOnly nodes at the end.
  // k (which can be null) represents the instructions to run after that flow node
  public BlockNode afterCps(Context context, BlockNode continuation) {
    OpNode[] nodeBody;
    FlowNode flow;
    Op last = body[body.length-1];
    if ( last.tailOnly() ) {
      nodeBody = new OpNode[body.length-1];
      flow = (FlowNode) last.toNode(context);
      if ( null != continuation ) {
        flow.setAfter(Truffle.getRuntime().createCallTarget(new BlockRootNode(continuation)));
      }
    }
    else {
      nodeBody = new OpNode[body.length];
      flow = null;
    }
    
    for ( int i = 0; i < nodeBody.length; ++i ) {
      nodeBody[i] = (OpNode) body[i].toNode(context);
    }
    return new BlockNode(nodeBody, flow);
  }
  

  public CallTarget toTarget(Context context) {
    return Truffle.getRuntime().createCallTarget(new BlockRootNode(cps(context)));
  }
  
  public BlockNode cps(Context context) {
    for ( int i = 0; i < body.length; ++i ) {
      Op o = body[i];
      if ( o.tailOnly() ) {
        if ( body.length == (i + 1) ) {
          break;
        }
        else {
          Block b = new Block(Arrays.copyOfRange(body, 0, i)),
                k = new Block(Arrays.copyOfRange(body, i+1, body.length));
          return b.afterCps(context, k.cps(context));
        }
      }
    }
    return afterCps(context, null);
  }

  public static Block compile(Cell src) throws UnexpectedResultException {
    Object op = src.head, a = src.tail;
    Queue<Op> q = new LinkedList<Op>();
    if ( TypesGen.isCell(op) ) {
      q.add(new Dup());
      compile(TypesGen.expectCell(a)).addTo(q);
      q.add(new Swap());
      compile(TypesGen.asCell(op)).addTo(q);
      q.add(new Cons());
    }
    else {
      switch ( Atom.unsignedIntOrBail(op) ) {
        case 0:
          if ( Noun.equals(0L, a) ) {
            q.add(new Bail());
          }
          else if ( !Noun.equals(1L,  a) ) {
            q.add(new Frag(new Axis(a)));
          }
          break;
        case 1:
          q.add(new Quote(a));
          break;
        case 2: {
          Cell c = TypesGen.expectCell(a);
          q.add(new Dup());
          compile(TypesGen.expectCell(c.head)).addTo(q);
          q.add(new Swap());
          compile(TypesGen.expectCell(c.tail)).addTo(q);
          q.add(new Eval());
          break;
        }
        case 3:
          compile(TypesGen.expectCell(a)).addTo(q);
          q.add(new Deep());
          break;
        case 4:
          compile(TypesGen.expectCell(a)).addTo(q);
          q.add(new Bump());
          break;
        case 5: {
          Cell c = TypesGen.expectCell(a);
          q.add(new Dup());
          compile(TypesGen.expectCell(c.head)).addTo(q);
          q.add(new Swap());
          compile(TypesGen.expectCell(c.tail)).addTo(q);
          q.add(new Same());
          break;
        }
        case 6: {
          Trel tyn = Trel.expect(a);
          q.add(new Dup());
          compile(TypesGen.expectCell(tyn.p)).addTo(q);
          q.add(new If(compile(TypesGen.expectCell(tyn.q)), 
                       compile(TypesGen.expectCell(tyn.r))));
          break;
        }
        case 7: {
          Cell c = TypesGen.expectCell(a);
          compile(TypesGen.expectCell(c.head)).addTo(q);
          compile(TypesGen.expectCell(c.tail)).addTo(q);
          break;
        }
        case 8: {
          Cell c = TypesGen.expectCell(a);
          q.add(new Dup());
          compile(TypesGen.expectCell(c.head)).addTo(q);
          q.add(new Cons());
          compile(TypesGen.expectCell(c.tail)).addTo(q);
          break;
        }
        case 9: {
          Cell c = TypesGen.expectCell(a);
          compile(TypesGen.expectCell(c.tail)).addTo(q);
          q.add(new Call(new Axis(c.head)));
          break;
        }
        case 11: {
          Cell c = TypesGen.expectCell(a);
          q.add(new Dup());
          compile(TypesGen.expectCell(c.tail)).addTo(q);
          q.add(new Swap());
          compile(TypesGen.expectCell(c.head)).addTo(q);
          q.add(new Esc());
          break;
        }
        case 10: {
          Cell c = TypesGen.expectCell(a);
          Block k = compile(TypesGen.expectCell(c.tail));
          if ( !TypesGen.isCell(c.head) ) {
            // no currently recognized static hints, just ignore
            k.addTo(q);
          }
          else {
            Cell dyn = TypesGen.asCell(c.head);
            Object kind = dyn.head;
            if ( Atom.MEMO.equals(kind) ) {
              q.add(new Memo(k));
            }
            else if ( Atom.FAST.equals(kind) ) {
              q.add(new Dup());
              compile(TypesGen.expectCell(dyn.tail)).addTo(q);
              q.add(new Swap());
              k.addTo(q);
              q.add(new Fast());
            }
            else if ( Atom.SLOG.equals(kind) ) {
              q.add(new Dup());
              compile(TypesGen.expectCell(dyn.tail)).addTo(q);
              q.add(new Slog());
              k.addTo(q);
            }
            else if ( Atom.MEAN.equals(kind)
                   || Atom.LOSE.equals(kind)
                   || Atom.HUNK.equals(kind)
                   || Atom.SPOT.equals(kind) ) {
              q.add(new Dup());
              compile(TypesGen.expectCell(dyn.tail)).addTo(q);
              q.add(new PushPlace(kind));
              k.addTo(q);
              q.add(new PopPlace());
            }
            else {
              q.add(new Dup());
              compile(TypesGen.expectCell(dyn.tail)).addTo(q);
              q.add(new Toss());
              k.addTo(q);
            }
          }
        }
      }
    }
    Op[] body = new Op[q.size()];
    return new Block(q.toArray(body));
  }
}