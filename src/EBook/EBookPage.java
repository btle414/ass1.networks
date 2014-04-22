package EBook;

/**
 * Created by ben on 29/03/14.
 * Class which represents an page of an EBook.
 */
public class EBookPage {

  private int pageNum;
  private String[] text;
  private EBookForum forum;

  public EBookPage(int pageNum, String[] text) {
    this.pageNum = pageNum;
    this.text = text;
    this.forum = new EBookForum();
  }

  public String[] getText() {
    return text;
  }

  public EBookForum getForum() {
    return forum;
  }

  public void postComment(int lineNumber, String author, String content) {
    forum.postComment(lineNumber, author, content);
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
