package Client;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ben on 13/04/2014.
 * Main thread responsible for reading user input, passing it to the parser and then outputting it to the server. Contains a timer for polls.
 */
public class ClientThread {

  private Socket clientSocket;
  private ClientProtocol protocol;
  private String pollSentence;
  private Timer poll;
  private String mode;
  private String name;
  private int pollInterval;

  public ClientThread(Socket socket, String mode, String name, int pollInterval) {
    clientSocket = socket;
    protocol = new ClientProtocol();
    poll = new Timer();
    this.mode = mode;
    this.name = name;
    this.pollInterval = pollInterval;
  }

  /**
   * Function which will loop and continuously read user input, and then send the appropriate formatted request.
   */
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

    //start input stream
    Runnable r = new ClientReceiverThread(protocol, inFromServer);
    new Thread(r).start();

    writeOut(protocol, outToServer, inFromServer, protocol.parsePre("setup " + mode + " " + name));

    while (true) {
      // get input from keyboard
      String sentence = "";
      try {
        sentence = inFromUser.readLine();
      } catch (IOException ioe) {
        System.out.println("Failed to read user input.");
      }

      class PollTimerTask extends TimerTask {

        private DataOutputStream outToServer;
        private ObjectInputStream inFromServer;
        private String pollSentence;

        public PollTimerTask(DataOutputStream outToServer, ObjectInputStream inFromServer, String pollSentence) {
          this.outToServer = outToServer;
          this.inFromServer = inFromServer;
          this.pollSentence = pollSentence;
        }

        @Override
        public void run() {
          //System.out.println("Polling.");
          writeOut(protocol, outToServer, inFromServer, pollSentence);
        }
      }

      sentence = protocol.parsePre(sentence);
      if (!protocol.isPush() && protocol.isPollSentence(sentence)) {
        poll.cancel();
        poll = new Timer();
        poll.scheduleAtFixedRate(new PollTimerTask(outToServer, inFromServer, protocol.getPollSentence()), pollInterval*1000, pollInterval*1000);
      }

      if (!sentence.isEmpty()) writeOut(protocol, outToServer, inFromServer, sentence);

    }
  }

  public void writeOut(ClientProtocol protocol, DataOutputStream outToServer, ObjectInputStream inFromServer, String sentence) {
    // write to server
    try {
      outToServer.writeBytes(sentence + '\n');
      outToServer.flush();
    } catch (IOException ioe) {
      System.out.println("Failed to write user input.");
    }

  }
}
