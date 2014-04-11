package Server;

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

  public ServerThread(Socket socket, EBookDatabase ebd) {
    this.socket = socket;
    this.ebd = ebd;
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
    String clientSentence;
    while (true) {
      clientSentence = inFromClient.readLine();

      System.out.println("received: " + clientSentence);
      // send reply
      DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

      execute(clientSentence, outToClient);

      System.out.println("connection from " + socket);
    }
  }

  public void execute(String input, DataOutputStream out) {
    String outputString = ebd.getBook("joyce").getPage(1).getText();

    try {
      out.writeBytes(outputString);
      out.writeBytes(""+'\n'); //terminate readLine
      out.flush();
    } catch (IOException ioe) {
      System.out.println("Failed to output to client.");
    }
  }

}
