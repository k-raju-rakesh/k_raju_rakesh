import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LiveQuotes {

    ExchangeClient exchangeClient = null;

    public static void main(String[] args) throws IOException {


        if (args.length == 1) {
            String cryptoCcy = args[0];

            LiveQuotes liveQuotes = new LiveQuotes();

            try {
                liveQuotes.getQuotes(cryptoCcy);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        Thread.sleep(200);
                        liveQuotes.shutDownApp();
                        System.out.println("Shutting down ...");

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            System.out.println("Invalid input arguments");
        }
    }

    public void getQuotes(String cryptoCcy) throws URISyntaxException {
        exchangeClient = new ExchangeClient(new URI("wss://ws-feed.exchange.coinbase.com"), cryptoCcy);
        exchangeClient.connect();
    }

    public void shutDownApp() {
        exchangeClient.closeConnection(0, "User Terminated Application");
        S3Utils.closeConnection();
    }

}
