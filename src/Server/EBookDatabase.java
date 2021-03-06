package Server; /**
 * Created by ben on 29/03/14.
 */

import EBook.EBook;
import EBook.ResponseComments;

import java.io.*;
import java.util.ArrayList;

/**
 * Class which represents the server side database. It contains a series of EBook classes.
 */
public class EBookDatabase {

  public final static String LOCATION = "eBook-pages" + File.separatorChar;
  public final static int LINES_PER_PAGE = 9;

  private ArrayList<EBook> database;

  public EBookDatabase() {
    this.database = new ArrayList<EBook>();
  }

  public synchronized EBook getBook(String name) {
    EBook wrapper = new EBook(name);
    int i = database.indexOf(wrapper);
    if (i > -1) {
      return database.get(i);
    }
    return null;
  }

  public ArrayList<EBook> getDatabase() {
    return database;
  }

  public synchronized void postComment(String book, int page, int lineNumber, String author, String content) {
    getBook(book).postComment(page, lineNumber, author, content);
  }

  public synchronized ResponseComments getCommentsString(String book, int page, int lineNumber, int index) {
    return getBook(book).getCommentsString(page, lineNumber, index);
  }

  /**
   * Loads all files in a constant location and populates the database.
   */
  public synchronized void loadAll() {
    File folder = new File(LOCATION);
    for (File fileEntry : folder.listFiles()) {
      String fileName = fileEntry.getName();
      String[] info = fileName.split("_");

      //skip hidden files
      if (info[0] != null && info[0].charAt(0) == '.') continue;

      BufferedReader reader = null;
      String[] lines = new String[LINES_PER_PAGE+1];
      int start = 1;
      try {
        reader = new BufferedReader(new FileReader(fileEntry));
        String line;
        while ((line = reader.readLine()) != null) {
          lines[start] = line;
          start++;
        }
      } catch (IOException io) {
        System.out.println("Server.EBookDatabase: read file failed");
      }

      EBook book = getBook(info[0]);
      if (book == null) {
        book = new EBook(info[0]);
        database.add(book);
      }
      book.addPage(Integer.parseInt(info[1].replace("page", "")), lines);
    }
  }

}
