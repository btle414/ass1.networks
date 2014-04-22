package Client;

import Shared.TransferObject;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Benjamin on 15/04/2014.
 * Class responsible for the receiver thread of the client. It will listen for responses, parse them and then print the stdout the response.
 */
public class ClientReceiverThread implements Runnable {

  private ClientProtocol cp;
  private ObjectInputStream ois;

  public ClientReceiverThread(ClientProtocol cp, ObjectInputStream ois) {
    this.cp = cp;
    this.ois = ois;
  }

  @Override
  public void run() {
    while (true) {

      // create read stream and receive from server
      TransferObject obj = null;
      try {
        obj = (TransferObject) ois.readObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      String output = cp.parsePost(obj);

      // print output
      if (!output.isEmpty()) System.out.println(output);
    }

  }
}
