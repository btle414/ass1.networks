package Server;

import Server.EBook.EBookDatabase;
import Server.EBook.EBookState;
import Server.EBook.ResponseComments;

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
  public String parse(String message) {
    String response = "";
    String[] parts = message.split(" ");

    if (parts[0].equals("display")) response = parseDisplay(parts[1], Integer.parseInt(parts[2]));

    else if (parts[0].equals("post_to_forum")) {
      int lineNumber = Integer.parseInt(parts[1]);
      String content = "";
      for (int i = 2; i < parts.length; i++) { content += parts[i] + " "; };
      content.substring(0, content.length()-1);
      response = parsePostToForum(lineNumber, content);

    } else if (parts[0].equals("read_post")) response = parseReadPost(Integer.parseInt(parts[1]));

    return response;
  }

  private String parseDisplay(String book, int page) {
    state.setLastKnownBook(book);
    state.setLastKnownPage(page);
    return ebd.getBook(book).getPage(page).getText();
  }

  private String parsePostToForum(int lineNumber, String content) {
    ebd.postComment(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, content);
    return "Posted\n";
  }

  private String parseReadPost(int lineNumber) {
    ResponseComments rc = ebd.getCommentsString(state.getLastKnownBook(), state.getLastKnownPage(), lineNumber, state.getLastFetchedComments());
    state.setLastFetchedComments(rc.getIndex());
    return rc.toString();
  }
}
