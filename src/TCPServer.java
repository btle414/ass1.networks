import Server.EBook.EBookDatabase;

import java.net.*;

public class TCPServer {

  public static EBookDatabase ebd = new EBookDatabase();

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

      Runnable r = new ServerThread(connectionSocket, ebd);
      new Thread(r).start();

    } // end of while (true)

  } // end of main()

} // end of class TCPServer