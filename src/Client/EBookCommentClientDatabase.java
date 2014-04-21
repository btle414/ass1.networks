package Client;

import Server.EBookDatabase;
import EBook.EBookForum;

import java.util.HashMap;

/**
 * Created by Ben on 13/04/2014.
 */
public class EBookCommentClientDatabase {

  private final static char PREFIX_NO_COMMENTS = ' ';
  private final static char PREFIX_NEW_COMMENTS = 'n';
  private final static char PREFIX_SAME_COMMENTS = 'm';

  HashMap<String, EBookForum[]> db;

  //map book->page->list of comments (state given by length)
  public EBookCommentClientDatabase() {
    this.db = new HashMap<String, EBookForum[]>();
  }

  public boolean exists(String book) {
    return db.containsKey(book);
  }

  public void createForum(String book) {
    EBookForum[] ebfa = new EBookForum[EBookDatabase.LINES_PER_PAGE+1];
    for (int i = 0; i < EBookDatabase.LINES_PER_PAGE+1; i++) {
      ebfa[i] = new EBookForum();
    }
    db.put(book, ebfa);
  }

  public EBookForum getForum(String book, int page) {
    return db.get(book)[page];
  }

  public char[] hasNewPosts(String book, int page, String[][][] newForum) {
    char[] hasLineNewPosts = new char[EBookDatabase.LINES_PER_PAGE+1];
    if (!exists(book)) createForum(book);
    EBookForum oldForum = db.get(book)[page];

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

  public char[] pushHasNewPosts(String book, int page) {
    char[] hasLineNewPosts = new char[EBookDatabase.LINES_PER_PAGE+1];
    if (!exists(book)) createForum(book);
    EBookForum oldForum = db.get(book)[page];

    //TODO should be i=1? or i=0?
    for (int i = 1; i < EBookDatabase.LINES_PER_PAGE+1; i++) {
      int numCurrComments = oldForum.getLineForum(i).getNumComments();
      int numOldComments = oldForum.getLineForum(i).getIndex();
      if (numCurrComments > numOldComments ) hasLineNewPosts[i] = PREFIX_NEW_COMMENTS;
      else if (numCurrComments == 0) hasLineNewPosts[i] = PREFIX_NO_COMMENTS;
      else hasLineNewPosts[i] = PREFIX_SAME_COMMENTS;
    }

    return hasLineNewPosts;
  }

}
