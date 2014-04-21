package EBook;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Ben on 12/04/2014.
 */
public class EBookLineForum implements Serializable {

  private LinkedList<EBookComment> comments;

  private int index; //for use on client end

  public EBookLineForum() {
    comments = new LinkedList<EBookComment>();
    index = 0;
  }

  public void postComment(String author, String content) {
    comments.add(new EBookComment(comments.size(), author, content));
  }

  public void postCommentWithUpdate(String author, String content) {
    postComment(author, content);
    index++;
  }

  public int getNumComments() {
    return comments.size();
  }

  public ResponseComments getCommentsString(int index) {
    ResponseComments rc = new ResponseComments();
    int i = -1;
    for (EBookComment ebc : comments) {
      i++;
      if (i < index) continue;
      rc.addMessage(i, ebc.author, ebc.message);
    }
    rc.setIndex(comments.size());

    return rc;
  }

  public String[][] getSerializableComments() {
    String[][] strs = new String[comments.size()][2];
    int i = 0;
    for (EBookComment ebc : comments) {
      strs[i][0] = ebc.author;
      strs[i][1] = ebc.message;
      i++;
    }

    return strs;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

}
