package Client;

import Shared.Arguments;

import java.net.*;

/**
 * The class which instantiates the two client threads.
 */
public class TCPClient {

  public void run(String[] args) throws Exception {

    Arguments a = new Arguments(args);

    InetAddress serverIPAddress = InetAddress.getByName(a.getAddress());

    // create socket which connects to server
    Socket clientSocket = new Socket(serverIPAddress, a.getPort());

    ClientThread ct = new ClientThread(clientSocket, a.getMode(), a.getUserName());
    ct.execute();

    // close client socket
    clientSocket.close();

  } // end of main

} // end of class Client.TCPClient