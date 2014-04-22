package EBook;

import java.util.HashMap;

/**
 * Created by ben on 29/03/14.
 * Class which holds the representation of an EBook.
 */
public class EBook {

  private String name;
  //private ArrayList<EBookPage> pages;
  private HashMap<Integer, EBookPage> pages;
  private int numPages;

  public EBook(String name) {
    this.name = name;
    this.pages = new HashMap<Integer, EBookPage>();
    this.numPages = 0;
    //this.pages = new ArrayList<EBookPage>();
  }

  public String getName() {
    return name;
  }

  public void addPage(int pageNum, String[] text) {
    if (pages.containsKey(pageNum)) return;
    pages.put(pageNum, new EBookPage(pageNum, text));
    numPages++;
    //this.pages.add(new EBookPage(pageNum, text));
  }

  public int getNumPages() {
    return pages.size();
  }

  public EBookPage getPage(int pageNum) {
    if (!pages.containsKey(pageNum)) return null;
    return pages.get(pageNum);
  }

  public void postComment(int page, int pageLineNumber, String author, String content) {
    pages.get(page).postComment(pageLineNumber, author, content);
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
