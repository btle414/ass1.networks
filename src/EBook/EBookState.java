package EBook;

/**
 * Created by Ben on 12/04/14.
 * Class which represents a definitive state which can define a book, page and line number for which the last fetched comments was queried.
 */
public class EBookState {

  private String lastKnownBook;
  private int lastKnownPage;
  private int lastFetchedComments;

  public EBookState() {
    lastKnownBook = "";
    lastKnownPage = 0;
    lastFetchedComments = 0;
  }

  public String getLastKnownBook() {
    return lastKnownBook;
  }

  public void setLastKnownBook(String lastKnownBook) {
    this.lastKnownBook = lastKnownBook;
  }

  public int getLastKnownPage() {
    return lastKnownPage;
  }

  public void setLastKnownPage(int lastKnownPage) {
    this.lastKnownPage = lastKnownPage;
  }

  public int getLastFetchedComments() {
    return lastFetchedComments;
  }

  public void setLastFetchedComments(int lastFetchedComments) {
    this.lastFetchedComments = lastFetchedComments;
  }
}
