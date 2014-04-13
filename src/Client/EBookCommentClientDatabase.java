package Client;

import Server.EBook.EBookDatabase;
import Server.EBook.EBookForum;
import Server.EBook.EBookLineForum;

import java.util.HashMap;

/**
 * Created by Ben on 13/04/2014.
 */
public class EBookCommentClientDatabase {

  private final static char PREFIX_NO_COMMENTS = ' ';
  private final static char PREFIX_NEW_COMMENTS = 'n';
  private final static char PREFIX_SAME_COMMENTS = 'm';

  HashMap<String, EBookForum> db;

  //map book->page->list of comments (state given by length)
  public EBookCommentClientDatabase() {
    this.db = new HashMap<String, EBookForum>();
  }

  public boolean exists(String book) {
    return db.containsKey(book);
  }

  public void createForum(String book) {
    db.put(book, new EBookForum());
  }

  public char[] hasNewPosts(String book, String[][] newForum) {
    char[] hasLineNewPosts = new char[EBookDatabase.LINES_PER_PAGE+1];
    if (!exists(book)) createForum(book);
    EBookForum oldForum = db.get(book);

    //TODO should be i=1? or i=0?
    for (int i = 1; i < EBookDatabase.LINES_PER_PAGE+1; i++) {
      int numCurrComments = newForum[i].length;
      int numOldComments = oldForum.getLineForum(i).getNumComments();
      if (numCurrComments > numOldComments ) hasLineNewPosts[i] = PREFIX_NEW_COMMENTS;
      else if (numCurrComments == 0) hasLineNewPosts[i] = PREFIX_NO_COMMENTS;
      else hasLineNewPosts[i] = PREFIX_SAME_COMMENTS;
    }

    return hasLineNewPosts;
  }

}
