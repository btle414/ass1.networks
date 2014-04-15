import java.io.*;
import java.net.*;

public class TCPClient {

  public static void main(String[] args) throws Exception {

    Arguments a = new Arguments(args);

    InetAddress serverIPAddress = InetAddress.getByName(a.getAddress());

    // create socket which connects to server
    Socket clientSocket = new Socket(serverIPAddress, a.getPort());

    ClientThread ct = new ClientThread(clientSocket, a.getMode());
    ct.execute();

    // close client socket
    clientSocket.close();

  } // end of main

} // end of class TCPClient