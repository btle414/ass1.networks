import Server.EBook.EBookDatabase;

import java.io.*;
import java.net.Socket;

/**
 * Created by ben on 12/04/14.
 */
public class ServerThread implements Runnable {

  private int threadIndex;
  private Socket socket;
  private EBookDatabase ebd;
  private ServerProtocol protocol;
  private BufferedReader inFromClient;
  private ObjectOutputStream outToClient;

  public ServerThread(int threadIndex, Socket socket, EBookDatabase ebd, BufferedReader inFromClient, ObjectOutputStream outToClient) {
    this.threadIndex = threadIndex;
    this.socket = socket;
    this.ebd = ebd;
    this.protocol = new ServerProtocol(threadIndex, ebd);
    this.inFromClient = inFromClient;
    this.outToClient = outToClient;
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
    String clientSentence;
    while (true) {
      System.out.println("Waiting for input");
      clientSentence = inFromClient.readLine();

      System.out.println("received: " + clientSentence);
      // send reply

      execute(clientSentence, outToClient);
    }
  }

  public synchronized void execute(String input, ObjectOutputStream out) {
    TransferObject outputObj = protocol.parse(input);

    try {
      out.writeObject(outputObj);
      out.flush();
    } catch (IOException ioe) {
      System.out.println("Failed to output to client.");
    }
  }

}
