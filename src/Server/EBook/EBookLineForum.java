package Server.EBook;

import Server.ResponseComments;

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

  public void postComment(String content) {
    comments.add(new EBookComment(content));
  }

  public void postCommentWithUpdate(String content) {
    postComment(content);
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
      rc.addMessage(ebc.message);
    }
    rc.setIndex(comments.size());

    return rc;
  }

  public String[] getSerializableComments() {
    String[] strs = new String[comments.size()];
    int i = 0;
    for (EBookComment ebc : comments) {
      strs[i] = ebc.message;
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
