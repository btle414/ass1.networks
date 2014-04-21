import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ben on 13/04/2014.
 */
public class ClientThread {

  private final static int TIMEOUT_INTERVAL_SECONDS = 10;

  private Socket clientSocket;
  private ClientProtocol protocol;
  private String pollSentence;
  private Timer poll;
  private String mode;
  private String name;

  public ClientThread(Socket socket, String mode, String name) {
    clientSocket = socket;
    protocol = new ClientProtocol();
    poll = new Timer();
    this.mode = mode;
    this.name = name;
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
        poll.scheduleAtFixedRate(new PollTimerTask(outToServer, inFromServer, protocol.getPollSentence()), TIMEOUT_INTERVAL_SECONDS*1000, TIMEOUT_INTERVAL_SECONDS*1000);
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
