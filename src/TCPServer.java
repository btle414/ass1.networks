import Server.EBook.EBookDatabase;

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

  public static void main(String[] args) throws Exception {
    //mine
    ebd.loadAll();

    // see if we do not use default server port
    int serverPort = 6789;
		/* change above port number this if required */

    if (args.length >= 1)
      serverPort = Integer.parseInt(args[0]);

    // create server socket
    ServerSocket welcomeSocket = new ServerSocket(serverPort);

    while (true) {

      // accept connection from connection queue
      Socket connectionSocket = welcomeSocket.accept();

      System.out.println("connection from " + connectionSocket);

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

} // end of class TCPServer