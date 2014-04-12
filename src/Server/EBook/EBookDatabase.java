package Server.EBook; /**
 * Created by ben on 29/03/14.
 */

import java.io.*;
import java.util.ArrayList;

public class EBookDatabase {

  public final static String LOCATION = "C:\\Users\\Ben\\IdeaProjects\\ass1.networks\\eBook-pages";
  public final static int LINES_PER_PAGE = 9;

  private ArrayList<EBook> database;

  public EBookDatabase() {
    this.database = new ArrayList<EBook>();
  }

  public EBook getBook(String name) {
    EBook wrapper = new EBook(name);
    int i = database.indexOf(wrapper);
    if (i > -1) {
      return database.get(i);
    }
    return null;
  }

  public void postComment(String book, int lineNumber, String content) {
    int page = (int) Math.floor((lineNumber-1)/LINES_PER_PAGE);
    int pageLineNumber = lineNumber - page*LINES_PER_PAGE;
    page += 1;

    System.out.println("Posting comment in book " + book + " on page " + page + " at line number " + pageLineNumber);
    getBook(book).postComment(page, pageLineNumber, content);

  }

  public void loadAll() {
    File folder = new File(LOCATION);
    for (File fileEntry : folder.listFiles()) {
      String fileName = fileEntry.getName();
      String[] info = fileName.split("_");

      BufferedReader reader = null;
      String fileText = "";
      try {
        reader = new BufferedReader(new FileReader(fileEntry));
        String line = "";
        while ((line = reader.readLine()) != null) {
          fileText += line + "\n";
        }
      } catch (IOException io) {
        System.out.println("Server.EBook.EBookDatabase: read file failed");
      }

      EBook book = getBook(info[0]);
      if (book == null) {
        book = new EBook(info[0]);
        database.add(book);
      }
      book.addPage(Integer.parseInt(info[1].replace("page", "")), fileText);
    }
  }

}
