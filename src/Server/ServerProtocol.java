package Server;

import EBook.*;
import EBook.ResponseComments;
import Shared.TransferObject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Ben on 12/04/2014.
 * Class responsible for parsing requests and returning (if required) appropriate responses.
 */
public class ServerProtocol {

  private int threadIndex;
  private EBookDatabase ebd;
  private EBookState state;
  private boolean isPushMode;
  private String name;
  private ObjectOutputStream outToClient;

  public ServerProtocol(int threadIndex, EBookDatabase ebd, ObjectOutputStream outToClient) {
    this.threadIndex = threadIndex;
    this.ebd = ebd;
    this.state = new EBookState();
    this.isPushMode = false;
    this.name = "";
    this.outToClient = outToClient;
  }

  public String getName() {
    return name;
  }

  /**
   * Parses client message and return the response to send back to the client.
   * @param message
   * @return
   */
  public synchronized TransferObject parse(String message) {
    TransferObject tf = new TransferObject();
    String[] parts = message.split(" ");

    if (parts[0].equals("display")) {
      tf = parseDisplay(parts[1], Integer.parseInt(parts[2]), false);

    } else if (parts[0].equals("post_to_forum")) {
      int lineNumber = Integer.parseInt(parts[1]);
      String content = "";
      int i = -1;
      for (i = 2; i < parts.length; i++) {
        content += parts[i] + " ";
      }
      if (i > 2) content = content.substring(0, content.length() - 1);

      tf = parsePostToForum(lineNumber, content);

    } else if (parts[0].equals("read_post")) {
      tf = parseReadPost(Integer.parseInt(parts[1]));

    } else if (parts[0].equals("check_new_posts")) {
      tf = parseDisplay(parts[1], Integer.parseInt(parts[2]), true);
      tf.setID(TransferObject.ID_CHECK_NEW_POSTS);


    } else if (parts[0].equals("setup")) {
      String name = "";
      int i = -1;
      for (i = 2; i < parts.length; i++) {
        name += parts[i] + " ";
      }
      if (i > 2) name = name.substring(0, name.length() - 1);
      this.name = name;

      tf = parseSetup(parts[1], name);
    }

    return tf;
  }

  //Below are functions to parse the requests from the server, and generate the response object.

  private TransferObject parseDisplay(String book, int page, boolean isCheckPosts) {
    if (isCheckPosts) System.out.println(name + " wants to check new posts.");
    else {
      if (isPushMode) System.out.println(name + " requests book " + book + ", page " + page + ". Push mode active so no posts will be transferred.");
      else System.out.println(name + " requests book " + book + ", page " + page + ". Push mode not active so posts will be transferred.");
    }

    state.setLastKnownBook(book);
    state.setLastKnownPage(page);

    EBookPage ebp = ebd.getBook(book).getPage(page);
    TransferObject to = new TransferObject(TransferObject.ID_TEXT, ebp.getText(), ebp.getForum().convertForumToStrMArray());
    return to;
  }

  private TransferObject parsePostToForum(int lineNumber, String content) {
    System.out.println("New post received from " + name + ".");
    ebd.postComment(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, name, content);
    System.out.println("Post added to the database and given serial number (" + state.getLastKnownBook() + ", " + state.getLastKnownPage() + ", " + lineNumber + ", " + ebd.getBook(state.getLastKnownBook()).getPage(state.getLastKnownPage()).getForum().getLineForum(lineNumber).getNumComments() + ").");

    TransferObject to = new TransferObject(TransferObject.ID_POST, null, ebd.getBook(state.getLastKnownBook()).getPage(state.getLastKnownPage()).getForum().convertForumToStrMArray());

    //now push the new comment to every possible person
    int index = 0;
    int numPushes = 0;
    for (ObjectOutputStream oos : TCPServer.objectStreams) {
      if (TCPServer.pushList.get(index)) {
        TransferObject outputObj = new TransferObject(TransferObject.ID_PUSH_POST, state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, name, content);

        try {
          oos.writeObject(outputObj);
          oos.flush();
        } catch (IOException ioe) {
          System.out.println("Failed to push to client.");
        }

        numPushes++;
      }
      index++;
    }

    if (numPushes == 0) System.out.println("Push list empty. No action required.");
    else System.out.println("Pushing to " + numPushes + " clients.");
    return to;
  }

  private TransferObject parseReadPost(int lineNumber) {
    System.out.println(name + " requests posts for book " + state.getLastKnownBook() + ", page " + state.getLastKnownPage() + ", line number " + lineNumber);
    //ResponseComments rc = ebd.getCommentsString(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, state.getLastFetchedComments());
    ResponseComments rc = ebd.getCommentsString(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, 0);
    state.setLastFetchedComments(rc.getIndex());

    TransferObject to = new TransferObject(TransferObject.ID_READ, null, ebd.getBook(state.getLastKnownBook()).getPage(state.getLastKnownPage()).getForum().convertForumToStrMArray());
    return to;
  }

  private TransferObject parseSetup(String mode, String name) {
    System.out.println("User " + name + " connecting with mode " + mode + ".");

    TransferObject to = new TransferObject(TransferObject.ID_SETUP);
    this.name = name;
    if (mode.equals("push")) {
      System.out.println("User " + name + " added to the push list.");

      TCPServer.pushList.set(threadIndex, true);
      isPushMode = true;

      //push all psots
      ArrayList<EBook> allBooks = ebd.getDatabase();
      for (EBook eb : allBooks) {
        int pages = eb.getNumPages();
        for (int i = 0; i < pages; i++) {
          if (eb.getPage(i) == null) continue;
          for (int j = 0; j < ebd.LINES_PER_PAGE; j++) {
            ResponseComments rc = eb.getCommentsString(i, j, 0);

            int counter = 1;
            for (EBookComment ebc : rc.getComments()) {
              System.out.println("Sending post (" + eb.getName() + ", " + i + ", " + j + ", " + counter + ") to user " + name);
              TransferObject outputObj = new TransferObject(TransferObject.ID_PUSH_POST, eb.getName(), i, j, ebc.author, ebc.message);

              try {
                outToClient.writeObject(outputObj);
                outToClient.flush();
              } catch (IOException ioe) {
                System.out.println("Failed to push to client.");
              }

              counter++;
            }

          }
        }
      }
    }

    return to;
  }
}
