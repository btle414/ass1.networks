package EBook;

import java.io.Serializable;

/**
 * Created by Ben on 12/04/2014.
 * Class which represents an EBook comment.
 */
public class EBookComment implements Serializable {

  public int index;
  public String message;
  public String author;

  public EBookComment(int index, String author, String message) {
    this.index = index; this.message = message; this.author = author;
  }

}
