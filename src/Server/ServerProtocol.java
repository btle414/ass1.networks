package Server;

/**
 * Created by Ben on 12/04/2014.
 */
public class ServerProtocol {

  private EBookDatabase ebd;

  public ServerProtocol(EBookDatabase ebd) {
    this.ebd = ebd;
  }

  //parse and return the message to sent back to the client
  public String parse(String message) {
    String[] parts = message.split(" ");
    if (parts[0].equals("display")) {
      String book = parts[1];
      int page = Integer.parseInt(parts[2]);

      return ebd.getBook(book).getPage(page).getText();
    }

    return "";
  }
}
