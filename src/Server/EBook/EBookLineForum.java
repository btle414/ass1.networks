package Server.EBook;

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
    comments.push(new EBookComment(content));
  }

}
