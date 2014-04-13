package Server.EBook;

import java.io.Serializable;

/**
 * Created by Ben on 12/04/2014.
 */
public class EBookForum implements Serializable {

  private EBookLineForum[] lines;

  public EBookForum() {
    lines = new EBookLineForum[EBookDatabase.LINES_PER_PAGE+1];
    for (int i = 0; i < EBookDatabase.LINES_PER_PAGE+1; i++) {
      lines[i] = new EBookLineForum();
    }
  }

  public EBookLineForum getLineForum(int line) {
    return lines[line];
  }

  public ResponseComments getCommentsString(int lineNumber, int index) {
    return lines[lineNumber].getCommentsString(index);
  }

  public void postComment(int lineNumber, String content) {
    lines[lineNumber].postComment(content);
  }

  public String[][] convertForumToStrMArray() {
    String[][] forum = new String[lines.length][];
    for (int i = 0; i < lines.length; i++) {
      forum[i] = lines[i].getSerializableComments();
    }
    return forum;
  }

  public int getTotalPosts() {
    int count = 0;
    for (int i = 0; i < lines.length; i++) {
      count += lines[i].getNumComments();
    }
    return count;
  }
}
