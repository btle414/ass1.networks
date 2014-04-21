import Client.EBookCommentClientDatabase;
import Server.EBook.EBookDatabase;
import Server.EBook.EBookForum;
import Server.EBook.EBookLineForum;
import Server.EBook.ResponseComments;

import java.util.LinkedList;

/**
 * Created by Ben on 13/04/2014.
 */
public class ClientProtocol {

  private int mostRecentQuery;
  private String currentBook;
  private int currentPage;
  private int reqLine;
  private EBookCommentClientDatabase ebccd;
  private boolean isPush;

  public ClientProtocol() {
    mostRecentQuery = TransferObject.ID_DEFAULT;
    currentBook = "";
    currentPage = 0;
    ebccd = new EBookCommentClientDatabase();
    isPush = false;
  }

  public boolean isPush() {
    return isPush;
  }

  public synchronized String parsePre(String userInput) {
    String[] parts = userInput.split(" ");
    String parsed = userInput;

    if (parts[0].equals("display")) {
      currentBook = parts[1];
      currentPage = Integer.parseInt(parts[2]);
      mostRecentQuery = TransferObject.ID_TEXT;
    } else if (parts[0].equals("post_to_forum")) {
      mostRecentQuery = TransferObject.ID_POST;
    } else if (parts[0].equals("read_post")) {
      mostRecentQuery = TransferObject.ID_READ;
      reqLine = Integer.parseInt(parts[1]);
      if (isPush) {
        parsed = "";
        printLocalPosts();
      }
    } else if (parts[0].equals("setup")) {
      mostRecentQuery = TransferObject.ID_SETUP;
      if (parts[1].equals("push")) {
        isPush = true;
      }
    //this query below never gets called
    } else if (parts[0].equals("check_new_posts")) {
      mostRecentQuery = TransferObject.ID_CHECK_NEW_POSTS;
    }

    return parsed;
  }

  public synchronized String parsePost(TransferObject to) {
    String response = "";

    switch(to.getID()) {
      //find diff with database but do not update
      case TransferObject.ID_TEXT:
        response = (isPush) ? to.getLines(ebccd.pushHasNewPosts(currentBook, currentPage)) : to.getLines(ebccd.hasNewPosts(currentBook, currentPage, to.getForum()));
        break;

      //do nothing
      case TransferObject.ID_POST:
        response = "Posted comment.";
        break;

      //update database when this is called
      case TransferObject.ID_READ:
        response = "-----Comments-----\n";
        EBookLineForum eblf = ebccd.getForum(currentBook, currentPage).getLineForum(reqLine);
        int allCommentLen = to.getForum()[reqLine].length;
        for (int i = (isPush) ? eblf.getIndex() : eblf.getNumComments(); i < allCommentLen; i++) {
          String str = to.getForum()[reqLine][i];
          if (!isPush) eblf.postComment(str);
          response += str + '\n';
        }
        ebccd.getForum(currentBook, currentPage).getLineForum(reqLine).setIndex(allCommentLen);
        break;

      //display if new posts
      case TransferObject.ID_CHECK_NEW_POSTS:
        if (!isPush) {
          int totalNewPosts = 0;
          String[][] forum = to.getForum();
          for (int i = 0; i < forum.length; i++) {
            totalNewPosts += forum[i].length;
          }
          int totalOldPosts = ebccd.getForum(currentBook, currentPage).getTotalPosts();
          response = (totalOldPosts == totalNewPosts) ? "" : "There are new posts.";
        }
        break;

      case TransferObject.ID_SETUP:
        break;

      case TransferObject.ID_PUSH_POST:
        if (isPush) {
          String book = to.getPushBook();
          int page = to.getPushPage();
          int line = to.getPushLineNumber();
          String comment = to.getComment();

          if (!ebccd.exists(book)) ebccd.createForum(book);
          ebccd.getForum(book, page).postComment(line, comment);
          response = (mostRecentQuery == TransferObject.ID_TEXT && currentBook.equals(book) && currentPage == page) ? "There are new posts." : "";
          System.out.println("Received post (" + book + ", " + page + ", " + line + ", " + comment + ")");
        }

      default:
        break;
    }
    return response;
  }

  public void printLocalPosts() {
    String response = "-----Comments-----\n";
    EBookLineForum eblf = ebccd.getForum(currentBook, currentPage).getLineForum(reqLine);
    int allCommentLen = eblf.getNumComments();
    int readIndex = eblf.getIndex();
    ResponseComments rc = eblf.getCommentsString(readIndex);
    LinkedList<String> comments = rc.getComments();
    for (String comment : comments) {
      response += comment + '\n';
    }
    eblf.setIndex(allCommentLen);
    System.out.println(response);
  }

  public boolean isPollSentence(String sentence) {
    return sentence.split(" ")[0].equals("display");
  }

  public String getPollSentence() {
    return "check_new_posts " + currentBook + " " + currentPage;
  }

}
