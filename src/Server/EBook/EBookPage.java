package Server.EBook;

/**
 * Created by ben on 29/03/14.
 */
public class EBookPage {

  private int pageNum;
  private String text;
  private EBookForum forum;

  public EBookPage(int pageNum, String text) {
    this.pageNum = pageNum;
    this.text = text;
    this.forum = new EBookForum();
  }

  public int getPageNum() {
    return pageNum;
  }

  public String getText() {
    return text;
  }

  public void postComment(int lineNumber, String content) {
    forum.postComment(lineNumber, content);
  }

  public ResponseComments getCommentsString(int lineNumber, int index) {
    return forum.getCommentsString(lineNumber, index);
  }

  @Override
  public boolean equals(Object a) {
    if (a instanceof EBookPage) {
      EBookPage ebp = (EBookPage) a;
      return pageNum == ebp.pageNum;
    }
    return false;
  }

}
