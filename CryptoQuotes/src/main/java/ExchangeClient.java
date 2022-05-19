import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class ExchangeClient extends WebSocketClient {

  private static final int LEVELS = 10;
  TreeMap<BigDecimal,BigDecimal> bidMap;
  TreeMap<BigDecimal,BigDecimal> askMap;
  String cryptoCcy;
  Logs logs;


  public ExchangeClient(URI serverURI, String cryptoCcy) {
    super(serverURI);
    this.cryptoCcy = cryptoCcy;
    logs = new Logs(cryptoCcy);
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {

    send("{\n" +
            "    \"type\": \"subscribe\",\n" +
            "    \"product_ids\": [\n" +
            "        \"" +
            cryptoCcy +
            "\"\n" +
            "    ],\n" +
            "    \"channels\": [\"ticker\"]\n" +
            "}");

  }


  Timestamp prevTimestamp;

  @Override
  public void onMessage(String message) {
//    System.out.println(message);
    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
    if(prevTimestamp == null) {
       prevTimestamp = currentTimestamp;
    }
    else if(TimeUnit.MILLISECONDS.toSeconds(currentTimestamp.getTime() - prevTimestamp.getTime())>1) {

      prevTimestamp = currentTimestamp;

      JSONParser parser = new JSONParser();
      try {
        JSONObject json = (JSONObject) parser.parse(message);
        String type = (String) json.get("type");
        if (type.equals("ticker")) {
          Long sequence = (Long) json.get("sequence");
          String price = (String) json.get("price");
          String cryptoCcy = (String) json.get("product_id");
          logs.writeToLogs(Instant.now().toString() + "," + cryptoCcy + "," + sequence + "," + price);
        }


      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    send("{\n" +
            "    \"type\": \"unsubscribe\",\n" +
            "    \"channels\": [\"ticker\"]\n" +
            "}");

    System.out.println(
        "Connection closed by " + (remote ? "remote peer" : "user") + " Code: " + code + " Reason: "
            + reason);
  }

  @Override
  public void onError(Exception ex) {
    ex.printStackTrace();
  }

}