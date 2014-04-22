package EBook;

import java.util.LinkedList;

/**
 * Created by Ben on 12/04/14.
 * Class which holds an abstraction over just a list of comments. Contains an index which holds the state of the comments to be compared with the server's index.
 */
public class ResponseComments {

  private int index;
  private LinkedList<EBookComment> comments;

  public ResponseComments() {
    index = 0;
    comments = new LinkedList<EBookComment>();
  }

  public void addMessage(int index, String author, String comment) {
    comments.add(new EBookComment(index, author, comment));
  }

  public int getIndex() {
    return index;
  }

  public LinkedList<EBookComment> getComments() {
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
    for (EBookComment s : comments) {
      //str += (index - numComments + i) + " " + s + '\n';
      str += (s.index+1) + " " + s.author + ": " + s.message + '\n';
      i++;
    }

    return str;
  }
}
