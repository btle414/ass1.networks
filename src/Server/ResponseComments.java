package Server;

import java.util.LinkedList;

/**
 * Created by Ben on 12/04/14.
 */
public class ResponseComments {

  private int index;
  private LinkedList<String> comments;

  public ResponseComments() {
    index = 0;
    comments = new LinkedList<String>();
  }

  public void addMessage(String comment) {
    comments.add(comment);
  }

  public int getIndex() {
    return index;
  }

  public LinkedList<String> getComments() {
    return comments;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public String toString() {
    String str = "";
    int numComments = comments.size();
    int i = 1;
    for (String s : comments) {
      str += (index-numComments+i) + " " + s + '\n';
      i++;
    }

    return str;
  }
}
