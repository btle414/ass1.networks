package Server;

import Server.EBook.EBookDatabase;

/**
 * Created by Ben on 12/04/2014.
 */
public class ServerProtocol {

  private EBookDatabase ebd;
  private String lastKnownBook;

  public ServerProtocol(EBookDatabase ebd) {
    this.ebd = ebd;
    this.lastKnownBook = "";
  }

  //parse client message and return the reponse to send back to the client
  public String parse(String message) {
    String response = "";
    String[] parts = message.split(" ");
    if (parts[0].equals("display")) response = parseDisplay(parts[1], Integer.parseInt(parts[2]));
    else if (parts[0].equals("post_to_forum")) response = parsePostToForum(Integer.parseInt(parts[1]), parts[2]);
    //else if (parts[0].equals("read_post")) response = parseReadPost(message);

    return response;
  }

  private String parseDisplay(String book, int page) {
    lastKnownBook = book;
    return ebd.getBook(book).getPage(page).getText();
  }

  private String parsePostToForum(int lineNumber, String content) {
    ebd.postComment(lastKnownBook, lineNumber, content);
    return "Posted Comment.\n";
  }
}
