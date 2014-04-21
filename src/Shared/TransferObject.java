package Shared;

import java.io.Serializable;

/**
 * Created by Ben on 13/04/2014.
 */
public class TransferObject implements Serializable {

  public final static int ID_DEFAULT = -1;
  public final static int ID_TEXT = 0;
  public final static int ID_POST = 1;
  public final static int ID_READ = 2;
  public final static int ID_CHECK_NEW_POSTS = 3;
  public final static int ID_SETUP = 4;
  public final static int ID_PUSH_POST = 5;

  private int id;

  //associated with text
  private String[] lines;
  private String[][][] commentsPerLine; //[page][line][0=author, 1=comment]

  //associated with push post
  private String pushBook;
  private int pushPage;
  private int pushLineNumber;
  private String pushAuthor;
  //private int commentIndex; //we can assume because of TCP, there is order
  private String pushComment;

  public TransferObject() {
    this.id = ID_DEFAULT;
    this.lines = null;
    this.commentsPerLine = null;
  }

  public TransferObject(int id) {
    this();
    this.id = id;
  }

  public TransferObject(int id, String[] lines, String[][][] commentsPerLine) {
    this();
    this.id = id;
    this.lines = lines;
    this.commentsPerLine = commentsPerLine;
  }

  //push constructor
  public TransferObject(int id, String pushBook, int pushPage, int pushLineNumber, String pushAuthor, String comment) {
    this();
    this.id = id;
    this.pushBook = pushBook;
    this.pushPage = pushPage;
    this.pushLineNumber = pushLineNumber;
    this.pushAuthor = pushAuthor;
    this.pushComment = comment;
  }

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }

  public String[][][] getForum() {
    return commentsPerLine;
  }

  public String getLines(char[] prepend) {
    String to = "";
    if (lines != null) {
      for (int i = 0; i < lines.length; i++) {
        if (lines[i] != null) to += prepend[i] + lines[i] + "\n";
      }
    }
    return to;
  }

  public String getPushAuthor() { return pushAuthor; }

  public String getPushComment() {
    return pushComment;
  }

  public String getPushBook() {
    return pushBook;
  }

  public int getPushPage() {
    return pushPage;
  }

  public int getPushLineNumber() {
    return pushLineNumber;
  }

}
