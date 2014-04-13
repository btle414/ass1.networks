import Client.EBookCommentClientDatabase;
import Server.EBook.EBookDatabase;

/**
 * Created by Ben on 13/04/2014.
 */
public class ClientProtocol {

  private int mostRecentQuery;
  private String currentBook;
  private int currentPage;
  private EBookCommentClientDatabase ebccd;

  public ClientProtocol() {
    mostRecentQuery = TransferObject.ID_DEFAULT;
    currentBook = "";
    currentPage = 0;
    ebccd = new EBookCommentClientDatabase();
  }

  public String parsePre(String userInput) {
    String[] parts = userInput.split(" ");

    if (parts[0].equals("display")) {
      currentBook = parts[1];
      currentPage = Integer.parseInt(parts[2]);
      mostRecentQuery = TransferObject.ID_TEXT;
    } else if (parts[0].equals("post_to_forum")) {
      mostRecentQuery = TransferObject.ID_POST;
    } else if (parts[0].equals("read_post")) {
      mostRecentQuery = TransferObject.ID_READ;
    }

    return userInput;
  }

  public String parsePost(TransferObject to) {
    String response = "";

    switch(mostRecentQuery) {
      //find diff with database but do not update
      case TransferObject.ID_TEXT:
        response = to.getLines(ebccd.hasNewPosts(currentBook, to.getForum()));
        break;

      //do nothing
      case TransferObject.ID_POST:
        response = "Posted comment.";
        break;

      //update database when this is called
      case TransferObject.ID_READ:
        response = "-----Comments-----";
        break;

      default:
        break;
    }
    return response;
  }

}
