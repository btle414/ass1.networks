package Server.EBook;

import java.util.ArrayList;

/**
 * Created by ben on 29/03/14.
 */
public class EBook {

  private String name;
  private ArrayList<EBookPage> pages;

  public EBook(String name) {
    this.name = name;
    this.pages = new ArrayList<EBookPage>();
  }

  public String getName() {
    return name;
  }

  public void addPage(int pageNum, String[] text) {
    this.pages.add(new EBookPage(pageNum, text));
  }

  public int getNumPages() {
    return pages.size();
  }

  public EBookPage getPage(int pageNum) {
    if (pageNum < 0 || pageNum >= this.pages.size()) return null;
    return pages.get(pageNum);
  }

  public void postComment(int page, int pageLineNumber, String content) {
    pages.get(page).postComment(pageLineNumber, content);
  }

  public ResponseComments getCommentsString(int page, int lineNumber, int index) {
    return pages.get(page).getCommentsString(lineNumber, index);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof EBook) {
      EBook ae = (EBook) o;
      return name.equals(ae.name);
    }
    return false;
  }

}
