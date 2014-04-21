package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.LinkedList;

public class TCPServer {

  public static EBookDatabase ebd = new EBookDatabase();
  public static final LinkedList<Boolean> pushList = new LinkedList<Boolean>();
  public static final LinkedList<ObjectOutputStream> objectStreams = new LinkedList<ObjectOutputStream>();
  public static int threadIndex = 0;

  public static void run(String[] args) throws Exception {

    // see if we do not use default server port
    int serverPort = 6789;
		/* change above port number this if required */

    if (args.length >= 1)
      serverPort = Integer.parseInt(args[0]);

    // create server socket
    ServerSocket welcomeSocket = new ServerSocket(serverPort);

    System.out.println("The server is listening on port number " + serverPort + ".");

    //mine
    ebd.loadAll();
    System.out.println("The database for discussion posts has been intialised.");

    while (true) {

      // accept connection from connection queue
      Socket connectionSocket = welcomeSocket.accept();

      // create read stream to get input
      BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
      ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());

      pushList.add(false);
      objectStreams.add(outToClient);
      Runnable r = new ServerThread(threadIndex, connectionSocket, ebd, inFromClient, outToClient);
      new Thread(r).start();

      threadIndex++;

    } // end of while (true)

  } // end of main()

} // end of class Server.TCPServer