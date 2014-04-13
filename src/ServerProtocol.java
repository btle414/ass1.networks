import Server.EBook.EBookDatabase;
import Server.EBook.EBookPage;
import Server.EBook.EBookState;
import Server.EBook.ResponseComments;

import java.util.LinkedList;

/**
 * Created by Ben on 12/04/2014.
 */
public class ServerProtocol {

  private EBookDatabase ebd;
  private EBookState state;

  public ServerProtocol(EBookDatabase ebd) {
    this.ebd = ebd;
    this.state = new EBookState();
  }

  //parse client message and return the response to send back to the client
  public TransferObject parse(String message) {
    TransferObject tf = new TransferObject();
    String[] parts = message.split(" ");

    if (parts[0].equals("display")) tf = parseDisplay(parts[1], Integer.parseInt(parts[2]));

    else if (parts[0].equals("post_to_forum")) {
      int lineNumber = Integer.parseInt(parts[1]);
      String content = "";
      for (int i = 2; i < parts.length; i++) {
        content += parts[i] + " ";
      }
      ;
      content.substring(0, content.length() - 1);
      tf = parsePostToForum(lineNumber, content);

    } else if (parts[0].equals("read_post")) tf = parseReadPost(Integer.parseInt(parts[1]));

    else if (parts[0].equals("check_new_posts")) {
      tf = parseDisplay(parts[1], Integer.parseInt(parts[2]));
      tf.setID(TransferObject.ID_CHECK_NEW_POSTS);
    }

    System.out.println("Returning ID: " + tf.getID());
    return tf;
  }

  private TransferObject parseDisplay(String book, int page) {
    state.setLastKnownBook(book);
    state.setLastKnownPage(page);

    EBookPage ebp = ebd.getBook(book).getPage(page);
    TransferObject to = new TransferObject(TransferObject.ID_TEXT, ebp.getText(), ebp.getForum().convertForumToStrMArray());
    return to;
  }

  private TransferObject parsePostToForum(int lineNumber, String content) {
    ebd.postComment(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, content);

    TransferObject to = new TransferObject(TransferObject.ID_POST, null, ebd.getBook(state.getLastKnownBook()).getPage(state.getLastKnownPage()).getForum().convertForumToStrMArray());
    return to;
  }

  private TransferObject parseReadPost(int lineNumber) {
    //ResponseComments rc = ebd.getCommentsString(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, state.getLastFetchedComments());
    ResponseComments rc = ebd.getCommentsString(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, 0);
    state.setLastFetchedComments(rc.getIndex());

    LinkedList<String> listComments = rc.getComments();
    TransferObject to = new TransferObject(TransferObject.ID_READ, null, ebd.getBook(state.getLastKnownBook()).getPage(state.getLastKnownPage()).getForum().convertForumToStrMArray());
    return to;
  }
}
