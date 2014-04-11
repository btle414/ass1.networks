package Client;/*
 *
 *  client for Client.TCPClient from Kurose and Ross
 *
 *  * Usage: java Client.TCPClient [server addr] [server port]
 */
import java.io.*;
import java.net.*;

public class TCPClient {

  public static void main(String[] args) throws Exception {

    // get server address
    String serverName = "localhost";
    if (args.length >= 1)
      serverName = args[0];
    InetAddress serverIPAddress = InetAddress.getByName(serverName);

    // get server port
    int serverPort = 6789;
    //change above port number if required
    if (args.length >= 2)
      serverPort = Integer.parseInt(args[1]);

    // create socket which connects to server
    Socket clientSocket = new Socket(serverIPAddress, serverPort);

    execute(clientSocket);

    // close client socket
    clientSocket.close();

  } // end of main

  public static void execute(Socket clientSocket) {
    BufferedReader inFromUser = null;
    DataOutputStream outToServer = null;
    BufferedReader inFromServer = null;

    try {
      inFromUser = new BufferedReader(new InputStreamReader(System.in));
      outToServer = new DataOutputStream(clientSocket.getOutputStream());
      inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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

      // write to server
      try {
        outToServer.writeBytes(sentence + '\n');
        outToServer.flush();
      } catch (IOException ioe) {
        System.out.println("Failed to write user input.");
      }

      // create read stream and receive from server
      String sentenceFromServer = "";
      try {
        String line;
        while (true) {
          line = inFromServer.readLine();
          if (line.length() <= 0) break;
          sentenceFromServer += line;
        }
      } catch (IOException ioe) {
        System.out.println("Failed to read from server.");
      }

      // print output
      System.out.println("===== FROM SERVER ==== ");
      System.out.println(sentenceFromServer);
    }
  }

} // end of class Client.TCPClient