import Server.EBook.EBookForum;

import java.io.Serializable;

/**
 * Created by Ben on 13/04/2014.
 */
public class TransferObject implements Serializable {

  public final static int ID_DEFAULT = -1;
  public final static int ID_TEXT = 0;
  public final static int ID_POST = 1;
  public final static int ID_READ = 2;

  private int id;

  //associated with text
  private String[] lines;
  private String[][] commentsPerLine;

  public TransferObject() {
    this.id = ID_DEFAULT;
    this.lines = null;
    this.commentsPerLine = null;
  }

  public TransferObject(int id) {
    this();
    this.id = id;
  }

  public TransferObject(int id, String[] lines, String[][] commentsPerLine) {
    this();
    this.lines = lines;
    this.commentsPerLine = commentsPerLine;
  }

  public String[][] getForum() {
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

}
