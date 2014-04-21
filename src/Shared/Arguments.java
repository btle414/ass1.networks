package Shared;

/**
 * Created by ben on 29/03/14.
 */
public class Arguments {

  //both
  private String address;
  private int port;

  //client
  private String mode;
  private int polling;
  private String userName;

  public Arguments() {
    this.address = "";
    this.port = 0;
    this.mode = "";
    this.polling = 0;
    this.userName = "";
  }

  public Arguments(String[] args) {
    this.parseString(args);
  }

  public Arguments(String address, int port, String mode, int polling, String userName) {
    this.address = address;
    this.port = port;
    this.mode = mode;
    this.polling = polling;
    this.userName = userName;
  }

  public String getAddress() {
    return address;
  }

  public int getPort() {
    return port;
  }

  public String getMode() {
    return mode;
  }

  public int getPolling() {
    return polling;
  }

  public String getUserName() {
    return userName;
  }

  public void parseString(String[] args) {
    address = args[3];
    port = Integer.parseInt(args[4]);
    mode = args[0];
    polling = Integer.parseInt(args[1]);
    userName = args[2];
  }
}
