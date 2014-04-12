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

  public void addPage(int pageNum, String text) {
    this.pages.add(new EBookPage(pageNum, text));
  }

  public EBookPage getPage(int pageNum) {
    EBookPage wrapper = new EBookPage(pageNum, "");
    int i = pages.indexOf(wrapper);
    if (i > -1) {
      return pages.get(i);
    }
    return null;
  }

  public void postComment(int page, int pageLineNumber, String content) {
    pages.get(page).postComment(pageLineNumber, content);
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
