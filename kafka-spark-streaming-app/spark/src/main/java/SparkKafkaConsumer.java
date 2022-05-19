import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import kafka.serializer.StringDecoder;

import org.json.JSONObject;

public class SparkKafkaConsumer {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Spark Streaming started now .....");

		SparkConf conf = new SparkConf().setAppName("kafka-sandbox").setMaster("local[*]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		// batchDuration - The time interval at which streaming data will be divided into batches
		JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(10000));

		Map<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", "localhost:9092");
		Set<String> topics = Collections.singleton("prices");

		JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc, String.class,
				String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);

		  final String COMMA = ",";


		  List<Double> priceList = new ArrayList<>();
		  List<String> ccyList = new ArrayList<>();
		  List<String> timeList = new ArrayList<>();

		  directKafkaStream.foreachRDD(rdd -> {
			  
		  System.out.println("New data arrived  " + rdd.partitions().size() +" Partitions and " + rdd.count() + " Records");
			  if(rdd.count() > 0) {
				rdd.collect().forEach(rawRecord -> {
					  

					final JSONObject obj = new JSONObject(rawRecord._2);
					Object msg = obj.get("message");
					String msgStr = "";

					if (msg != null) {
						msgStr = msg.toString();
					}

					StringTokenizer st = new StringTokenizer(msgStr,",");

					  if(st.hasMoreTokens()) {


						String time = st.nextToken();
						  timeList.add(time);

						  System.out.println(time);
 					    String ccy = st.nextToken();
						  ccyList.add(ccy);
					    String tickId = st.nextToken();
					    String price = st.nextToken();
						  priceList.add(Double.parseDouble(price));


					  }
					  
				  });

				FileWriter writer = new FileWriter("10seconds.csv",true);

				Double price_sum = 0.0;
				for(Double price:priceList) {
					price_sum = price_sum + price;
				}

				  price_sum = price_sum/priceList.size();
				  writer.write(ccyList.get(0)+ "," +timeList.get(timeList.size() - 1)+","+ price_sum+"\n");

				  priceList.clear();
				  ccyList.clear();
				  timeList.clear();


				System.out.println("Master dataset has been created : ");
				writer.close();
			  }
		  });
		 
		ssc.start();
		ssc.awaitTermination();
	}
	

}



