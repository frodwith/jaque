package net.frodwith.jaque.truffle.nodes.jet;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ListItem;
import org.commonmark.parser.Parser;

import net.frodwith.jaque.Bail;
import net.frodwith.jaque.data.Atom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.data.Tape;
import net.frodwith.jaque.data.Trel;

public abstract class DownNode extends GateNode {
  private static final Parser parser = Parser.builder().build();
  private static final Object
    BLOT = Atom.mote("blot"),
    BLOQ = Atom.mote("bloq"),
    CODE = Atom.mote("code"),
    EMPH = Atom.mote("emph"),
    HEAD = Atom.mote("head"),
    HRUL = Atom.mote("hrul"),
    ITEM = Atom.mote("item"),
    HTML = Atom.mote("html"),
    HTMT = Atom.mote("htmt"),
    LINE = Atom.mote("line"),
    LINK = Atom.mote("link"),
    LIST = Atom.mote("list"),
    PARA = Atom.mote("para");
  
  @TruffleBoundary
  private Node parse(String s) {
    return parser.parse(s);
  }

  @Specialization
  protected Object down(Object a) {
    return documentToNoun(parse(Atom.cordToString(a)));
  }

  private Object documentToNoun(Node node) {
    return listElemsToNoun(node);
  }

  @TruffleBoundary
  private Object listElemsToNoun(Node node) {
    Object elems = 0L;
    for ( Node child = node.getLastChild(); null != child; child = node.getPrevious() ) {
      elems = new Cell(nodeToNoun(child), elems);
    }
    return elems;
  }

  private Object nodeToNoun(Node nod) {
    if ( null == nod ) {
      getContext().err("markdown null node");
      throw new Bail();
    }
    if ( nod instanceof Document ) {
      return documentToNoun(nod);
    }
    else if ( nod instanceof BlockQuote ) {
      return blockQuoteToNoun(nod);
   }
    else if ( nod instanceof BulletList ) {
      return bulletListToNoun((BulletList) nod);
    }
    else if ( nod instanceof OrderedList ) {
      return orderedListToNoun((OrderedList) nod);
    }
    else if ( nod instanceof ListItem ) {
      return listItemToNoun(nod);
    }
    else if ( nod instanceof FencedCodeBlock ) {
      return fencedCodeBlockToNoun((FencedCodeBlock) nod);
    }
    else if ( nod instanceof IndentedCodeBlock ) {
      return indentedCodeBlockToNoun((IndentedCodeBlock) nod);
    }
    else if ( nod instanceof HtmlBlock ) {
      return htmlToNoun((HtmlBlock) nod);
    }
    else if ( nod instanceof Paragraph ) {
      return paragraphToNoun(nod);
    }
    else if ( nod instanceof Heading ) {
      return headerToNoun((Heading) nod);
    }
    else if ( nod instanceof ThematicBreak ) {
      return hruleToNoun(nod);
    }
    else if ( nod instanceof Text ) {
      return textToNoun((Text) nod);
    }
    else if ( nod instanceof SoftLineBreak ) {
      return softbreakToNoun(nod);
    }
    else if ( nod instanceof HardLineBreak ) {
      return linebreakToNoun(nod);
    }
    else if ( nod instanceof Code ) {
      return inlineCodeToNoun((Code) nod);
    }
    else if ( nod instanceof HtmlInline ) {
      return inlineHtmlToNoun((HtmlInline) nod);
    }
    else if ( nod instanceof Emphasis ) {
      return emphToNoun(nod);
    }
    else if ( nod instanceof StrongEmphasis ) {
      return strongToNoun(nod);
    }
    else if ( nod instanceof Link ) {
      return linkToNoun((Link) nod);
    }
    else if ( nod instanceof Image ) {
      return imageToNoun((Image) nod);
    }
    else {
      getContext().err("bad markdown parsing");
      throw new Bail();
    }
  }
  
  private Cell textToNoun(Text nod) {
    return new Cell(0L, Tape.fromString(nod.getLiteral()));
  }

  private Cell strongToNoun(Node nod) {
    return new Cell(new Cell(EMPH, Atom.YES), listElemsToNoun(nod));
  }

  private Cell softbreakToNoun(Node nod) {
    return new Trel(0L, 10L, 0L).toCell();
  }

  private Cell paragraphToNoun(Node nod) {
    return new Cell(PARA, listElemsToNoun(nod));
  }

  private Cell listItemToNoun(Node nod) {
    return new Cell(new Cell(ITEM, 0L), listElemsToNoun(nod));
  }

  private Cell linkToNoun(Link nod) {
    String t = nod.getTitle(), u = nod.getDestination();
    Object url = (null == u || u.isEmpty()) ? 0L : Tape.fromString(u);
    Object title = (null == t || t.isEmpty()) ? 0L : new Cell(0L, Atom.stringToCord(t));
    Trel head = new Trel(LINK, url, title);
    return new Cell(head.toCell(), listElemsToNoun(nod));
  }

  private Cell linebreakToNoun(Node nod) {
    return new Cell(LINE, 0L);
  }

  private Cell inlineHtmlToNoun(HtmlInline nod) {
    return new Cell(HTMT, Tape.fromString(nod.getLiteral()));
  }

  private Cell inlineCodeToNoun(Code nod) {
    return new Cell(CODE, Tape.fromString(nod.getLiteral()));
  }

  private Cell listToNoun(ListBlock nod, long delim) {
    Trel head = new Trel(LIST, nod.isTight() ? Atom.YES : Atom.NO, delim);
    return new Cell(head.toCell(), listElemsToNoun(nod));
  }

  private Cell orderedListToNoun(OrderedList nod) {
    return listToNoun(nod, (nod.getDelimiter() == '.') ? '.' : ')');
  }

  private Cell bulletListToNoun(BulletList nod) {
    return listToNoun(nod, nod.getBulletMarker());
  }

  private Cell indentedCodeBlockToNoun(IndentedCodeBlock nod) {
    Object r = Atom.lore(Atom.stringToCord(nod.getLiteral()));
    return new Trel(CODE, 0L, r).toCell();
  }

  private Cell imageToNoun(Image nod) {
    String t = nod.getTitle();
    Object title = (null == t || t.isEmpty()) ? 0L : new Cell(0L, Tape.fromString(t) );
    Trel head = new Trel(BLOT, Tape.fromString(nod.getDestination()), title);
    return new Cell(head.toCell(), listElemsToNoun(nod));
  }

  private Cell htmlToNoun(HtmlBlock nod) {
    Object str = Atom.stringToCord(nod.getLiteral());
    return new Cell(HTML, Atom.lore(str));
  }

  private Cell hruleToNoun(Node nod) {
    return new Cell(HRUL, 0L);
  }

  private Cell headerToNoun(Heading nod) {
    return new Trel(HEAD, nod.getLevel(), listElemsToNoun(nod)).toCell();
  }

  private Cell emphToNoun(Node nod) {
    return new Cell(new Cell(EMPH, Atom.NO), listElemsToNoun(nod));
  }

  private Cell fencedCodeBlockToNoun(FencedCodeBlock nod) {
    Object r = Atom.lore(Atom.stringToCord(nod.getLiteral()));
    Cell q = new Qual(0L,
      (long) nod.getFenceChar(),
      (long) nod.getFenceLength(),
      Atom.stringToCord(nod.getInfo())).toCell();
    
    return new Trel(CODE, q, r).toCell();
  }

  private Cell blockQuoteToNoun(Node nod) {
    return new Cell(new Cell(BLOQ, 0L), listElemsToNoun(nod));
  }
}