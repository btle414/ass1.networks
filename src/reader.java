import Client.TCPClient;

/**
 * Created by Benjamin on 21/04/2014.
 */
public class reader {

  public static void main(String[] args) throws Exception {
    TCPClient c = new TCPClient();
    c.run(args);
  }

}
