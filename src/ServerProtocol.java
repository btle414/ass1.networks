import Server.EBook.EBookDatabase;
import Server.EBook.EBookPage;
import Server.EBook.EBookState;
import Server.EBook.ResponseComments;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

/**
 * Created by Ben on 12/04/2014.
 */
public class ServerProtocol {

  private int threadIndex;
  private EBookDatabase ebd;
  private EBookState state;
  private boolean isPushMode;

  public ServerProtocol(int threadIndex, EBookDatabase ebd) {
    this.threadIndex = threadIndex;
    this.ebd = ebd;
    this.state = new EBookState();
    isPushMode = false;
  }

  //parse client message and return the response to send back to the client
  public synchronized TransferObject parse(String message) {
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


    } else if (parts[0].equals("setup")) {
      tf = parseSetup(parts[1]);
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

    //now push the new comment to every possible person
    int index = 0;
    for (ObjectOutputStream oos : TCPServer.objectStreams) {
      //TODO THIS IS VERY SLOW
      //change to iterator
      //also this is quite hacky abstraction
      if (TCPServer.pushList.get(index)) {
        TransferObject outputObj = new TransferObject(TransferObject.ID_PUSH_POST, state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, content);

        try {
          oos.writeObject(outputObj);
          oos.flush();
        } catch (IOException ioe) {
          System.out.println("Failed to push to client.");
        }
      }
      index++;
    }
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

  private TransferObject parseSetup(String mode) {
    if (mode.equals("push")) {
      System.out.println("PUSH MODE ENGAGED");
      TCPServer.pushList.set(threadIndex, true);
      isPushMode = true;
    }
    TransferObject to = new TransferObject(TransferObject.ID_SETUP);
    return to;
  }
}
