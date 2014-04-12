package Server.EBook;

/**
 * Created by Ben on 12/04/2014.
 */
public class EBookForum {

  private EBookLineForum[] lines;

  public EBookForum() {
    lines = new EBookLineForum[EBookDatabase.LINES_PER_PAGE+1];
    for (int i = 0; i < EBookDatabase.LINES_PER_PAGE+1; i++) {
      lines[i] = new EBookLineForum();
    }
  }

  public void postComment(int lineNumber, String content) {
    lines[lineNumber].postComment(content);
  }
}
