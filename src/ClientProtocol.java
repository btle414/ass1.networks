import Client.EBookCommentClientDatabase;
import Server.EBook.EBookDatabase;
import Server.EBook.EBookForum;
import Server.EBook.EBookLineForum;

/**
 * Created by Ben on 13/04/2014.
 */
public class ClientProtocol {

  private int mostRecentQuery;
  private String currentBook;
  private int currentPage;
  private int reqLine;
  private EBookCommentClientDatabase ebccd;

  public ClientProtocol() {
    mostRecentQuery = TransferObject.ID_DEFAULT;
    currentBook = "";
    currentPage = 0;
    ebccd = new EBookCommentClientDatabase();
  }

  public synchronized String parsePre(String userInput) {
    String[] parts = userInput.split(" ");

    if (parts[0].equals("display")) {
      currentBook = parts[1];
      currentPage = Integer.parseInt(parts[2]);
      mostRecentQuery = TransferObject.ID_TEXT;
    } else if (parts[0].equals("post_to_forum")) {
      mostRecentQuery = TransferObject.ID_POST;
    } else if (parts[0].equals("read_post")) {
      mostRecentQuery = TransferObject.ID_READ;
      reqLine = Integer.parseInt(parts[1]);
    } else if (parts[0].equals("setup")) {
      mostRecentQuery = TransferObject.ID_SETUP;
    //this query below never gets called
    } else if (parts[0].equals("check_new_posts")) {
      mostRecentQuery = TransferObject.ID_CHECK_NEW_POSTS;
    }

    return userInput;
  }

  public synchronized String parsePost(TransferObject to) {
    String response = "";

    switch(to.getID()) {
      //find diff with database but do not update
      case TransferObject.ID_TEXT:
        response = to.getLines(ebccd.hasNewPosts(currentBook, currentPage, to.getForum()));
        break;

      //do nothing
      case TransferObject.ID_POST:
        response = "Posted comment.";
        break;

      //update database when this is called
      case TransferObject.ID_READ:
        response = "-----Comments-----\n";
        EBookLineForum eblf = ebccd.getForum(currentBook, currentPage).getLineForum(reqLine);
        for (int i = eblf.getNumComments(); i < to.getForum()[reqLine].length; i++) {
          String str = to.getForum()[reqLine][i];
          eblf.postComment(str);
          response += str + '\n';
        }
        break;

      //display if new posts
      case TransferObject.ID_CHECK_NEW_POSTS:
        int totalNewPosts = 0;
        String[][] forum = to.getForum();
        for (int i = 0; i < forum.length; i++) { totalNewPosts += forum[i].length; }
        int totalOldPosts = ebccd.getForum(currentBook, currentPage).getTotalPosts();
        response = (totalOldPosts == totalNewPosts) ? "There are no new posts" : "There are new posts.";
        break;

      case TransferObject.ID_SETUP:
        break;

      default:
        break;
    }
    return response;
  }

  public boolean isPollSentence(String sentence) {
    return sentence.split(" ")[0].equals("display");
  }

  public String getPollSentence() {
    return "check_new_posts " + currentBook + " " + currentPage;
  }

}
