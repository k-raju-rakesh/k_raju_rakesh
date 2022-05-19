import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.File;
import java.io.IOException;

/**
 * Created by mattua on 23/07/2016.
 */
public class S3Utils {


    public static final String access_key_id= "AKIAV4UQOILJ23VXHMBQ";

    public static final String secret_access_key ="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";



    private static AmazonS3Client s3Client;
    public static void writeToS3(String ccy, File file) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(S3Utils.access_key_id, S3Utils.secret_access_key);
        s3Client = new AmazonS3Client(awsCreds);

        PutObjectRequest putRequest = new PutObjectRequest("priceticks", ccy +"/" + file.getName(), file);
        PutObjectResult response = s3Client.putObject(putRequest);

        System.out.println("Completed writing file " + ccy +"/" + file.getName() + " to S3");
    }

    public static void closeConnection() {
        s3Client.shutdown();
    }


}

