import Server.EBook.EBookDatabase;

import java.io.*;
import java.net.Socket;

/**
 * Created by ben on 12/04/14.
 */
public class ServerThread implements Runnable {

  private Socket socket;
  private EBookDatabase ebd;
  private ServerProtocol protocol;

  public ServerThread(Socket socket, EBookDatabase ebd) {
    this.socket = socket;
    this.ebd = ebd;
    this.protocol = new ServerProtocol(ebd);
  }

  @Override
  public void run() {
    try {
      newConnection();
    } catch (Exception e) {
      System.out.println("New connection failed.");
    }
  }

  public void newConnection() throws Exception {
    // create read stream to get input
    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    ObjectOutputStream outToClient = new ObjectOutputStream(socket.getOutputStream());
    String clientSentence;
    while (true) {
      System.out.println("Waiting for input");
      clientSentence = inFromClient.readLine();

      System.out.println("received: " + clientSentence);
      // send reply

      execute(clientSentence, outToClient);
    }
  }

  public void execute(String input, ObjectOutputStream out) {
    TransferObject outputObj = protocol.parse(input);

    try {
      out.writeObject(outputObj);
      out.flush();
    } catch (IOException ioe) {
      System.out.println("Failed to output to client.");
    }
  }

}
