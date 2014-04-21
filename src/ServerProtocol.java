import Server.EBook.*;
import Server.ResponseComments;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Ben on 12/04/2014.
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

  //parse client message and return the response to send back to the client
  public synchronized TransferObject parse(String message) {
    TransferObject tf = new TransferObject();
    String[] parts = message.split(" ");

    if (parts[0].equals("display")) {
      tf = parseDisplay(parts[1], Integer.parseInt(parts[2]));

    } else if (parts[0].equals("post_to_forum")) {
      int lineNumber = Integer.parseInt(parts[1]);
      String content = "";
      for (int i = 2; i < parts.length; i++) {
        content += parts[i] + " ";
      }
      content.substring(0, content.length() - 1);
      tf = parsePostToForum(lineNumber, content);

    } else if (parts[0].equals("read_post")) {
      tf = parseReadPost(Integer.parseInt(parts[1]));

    } else if (parts[0].equals("check_new_posts")) {
      tf = parseDisplay(parts[1], Integer.parseInt(parts[2]));
      tf.setID(TransferObject.ID_CHECK_NEW_POSTS);


    } else if (parts[0].equals("setup")) {
      String name = "";
      for (int i = 2; i < parts.length; i++) {
        name += parts[i] + " ";
      }
      tf = parseSetup(parts[1], name);
    }

    return tf;
  }

  private TransferObject parseDisplay(String book, int page) {
    System.out.println(name + " requests book " + book + ", page " + page);

    state.setLastKnownBook(book);
    state.setLastKnownPage(page);

    EBookPage ebp = ebd.getBook(book).getPage(page);
    TransferObject to = new TransferObject(TransferObject.ID_TEXT, ebp.getText(), ebp.getForum().convertForumToStrMArray());
    return to;
  }

  private TransferObject parsePostToForum(int lineNumber, String content) {
    System.out.println("New post received from " + name + ".");
    ebd.postComment(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, content);
    System.out.println("Post added to the database and given serial number (" + state.getLastKnownBook() + ", " + state.getLastKnownPage() + ", " + lineNumber + ", " + ebd.getBook(state.getLastKnownBook()).getPage(state.getLastKnownPage()).getForum().getLineForum(lineNumber).getNumComments() + ").");

    TransferObject to = new TransferObject(TransferObject.ID_POST, null, ebd.getBook(state.getLastKnownBook()).getPage(state.getLastKnownPage()).getForum().convertForumToStrMArray());

    //now push the new comment to every possible person
    int index = 0;
    int numPushes = 0;
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

        numPushes++;
      }
      index++;
    }

    if (numPushes == 0) System.out.println("Push list empty. No action required.");
    else System.out.println("Pushing to " + numPushes + " clients");
    return to;
  }

  private TransferObject parseReadPost(int lineNumber) {
    System.out.println(name + " requests posts for book " + state.getLastKnownBook() + ", page " + state.getLastKnownPage() + ", line number " + lineNumber);
    //ResponseComments rc = ebd.getCommentsString(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, state.getLastFetchedComments());
    ResponseComments rc = ebd.getCommentsString(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, 0);
    state.setLastFetchedComments(rc.getIndex());

    LinkedList<String> listComments = rc.getComments();
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
          for (int j = 0; j < ebd.LINES_PER_PAGE; j++) {
            ResponseComments rc = eb.getCommentsString(i, j, 0);

            int counter = 0;
            for (String s : rc.getComments()) {
              System.out.println("Sending post (" + eb.getName() + ", " + i + ", " + j + ", " + counter + ") to user " + name);
              TransferObject outputObj = new TransferObject(TransferObject.ID_PUSH_POST, eb.getName(), i, j, s);

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
