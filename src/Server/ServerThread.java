package Server;

import Server.EBook.EBookDatabase;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
    String clientSentence;
    while (true) {
      System.out.println("Waiting for input");
      clientSentence = inFromClient.readLine();

      System.out.println("received: " + clientSentence);
      // send reply

      execute(clientSentence, outToClient);
    }
  }

  public void execute(String input, DataOutputStream out) {
    String outputString = protocol.parse(input);

    try {
      out.writeBytes(outputString);
      out.writeBytes("" + '\n'); //terminate readline
      out.flush();
    } catch (IOException ioe) {
      System.out.println("Failed to output to client.");
    }
  }

}
