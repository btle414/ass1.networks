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

    ClientThread ct = new ClientThread(clientSocket);
    ct.execute();

    // close client socket
    clientSocket.close();

  } // end of main

} // end of class TCPClient