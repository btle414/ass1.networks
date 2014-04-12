package Server.EBook;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Ben on 12/04/2014.
 */
public class EBookLineForum {

  private LinkedList<EBookComment> comments;

  public EBookLineForum() {
    comments = new LinkedList<EBookComment>();
  }

  public void postComment(String content) {
    comments.add(new EBookComment(content));
  }

  public ResponseComments getCommentsString(int index) {
    ResponseComments rc = new ResponseComments();
    int i = -1;
    for (EBookComment ebc : comments) {
      i++;
      if (i < index) continue;
      rc.addMessage(ebc.message);
    }
    rc.setIndex(comments.size());

    return rc;
  }

}
