package Server;

/**
 * Created by ben on 29/03/14.
 */
public class EBookPage {

  private int pageNum;
  private String text;

  public EBookPage(int pageNum, String text) {
    this.pageNum = pageNum;
    this.text = text;
  }

  public int getPageNum() {
    return pageNum;
  }

  public String getText() {
    return text;
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
