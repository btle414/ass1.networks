package Server.EBook;

import java.io.Serializable;

/**
 * Created by Ben on 12/04/2014.
 */
public class EBookComment implements Serializable {

  public String message;

  public EBookComment(String message) {
    this.message = message;
  }

}
