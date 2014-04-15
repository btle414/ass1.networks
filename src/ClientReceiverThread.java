import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Benjamin on 15/04/2014.
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
      //System.out.println("===== FROM SERVER ==== ");
      if (!output.isEmpty()) System.out.println(output);
    }

  }
}
