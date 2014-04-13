import java.io.*;
import java.net.Socket;

/**
 * Created by Ben on 13/04/2014.
 */
public class ClientThread {

  private Socket clientSocket;
  private ClientProtocol protocol;

  public ClientThread(Socket socket) {
    clientSocket = socket;
    protocol = new ClientProtocol();
  }

  public void execute() {
    BufferedReader inFromUser = null;
    DataOutputStream outToServer = null;
    ObjectInputStream inFromServer = null;

    try {
      inFromUser = new BufferedReader(new InputStreamReader(System.in));
      outToServer = new DataOutputStream(clientSocket.getOutputStream());
      inFromServer = new ObjectInputStream(clientSocket.getInputStream());
    } catch (IOException ioe) {
      System.out.println("Failed to instantiate required buffers.");
    }

    while (true) {
      // get input from keyboard
      String sentence = "";
      try {
        sentence = inFromUser.readLine();
      } catch (IOException ioe) {
        System.out.println("Failed to read user input.");
      }

      sentence = protocol.parsePre(sentence);

      // write to server
      try {
        outToServer.writeBytes(sentence + '\n');
        outToServer.flush();
      } catch (IOException ioe) {
        System.out.println("Failed to write user input.");
      }

      // create read stream and receive from server
      TransferObject obj = null;
      try {
        obj = (TransferObject) inFromServer.readObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      String output = protocol.parsePost(obj);

      // print output
      System.out.println("===== FROM SERVER ==== ");
      System.out.println(output);
    }
  }
}
