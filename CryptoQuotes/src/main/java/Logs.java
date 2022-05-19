import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logs {

    private Integer fileNum = 1;
    private Integer lineCnt = 1;
    private static final Integer NUM_OF_LINES_IN_A_FILE = 10;
    private String fileName;
    private String cryptoCcy;

    public Logs(String cryptoCcy) {
        this.cryptoCcy = cryptoCcy;
    }

    public void writeToLogs(String line){

        try {
            PrintWriter logWriter = getLogFileWriter();
            logWriter.println(line);
            lineCnt++;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private PrintWriter myWriter;
    private File logFile;

    private PrintWriter getLogFileWriter() throws IOException {
        if(NUM_OF_LINES_IN_A_FILE < lineCnt || myWriter == null) {

            if(myWriter != null) {
                myWriter.flush();
                myWriter.close();

                S3Utils.writeToS3(cryptoCcy,logFile);
                fileNum++;
                lineCnt = 1;
            }
            fileName = "src/logs/Price-Ticks-" + fileNum + ".log";
            logFile = new File(fileName);
            myWriter = new PrintWriter(logFile);
        }
        return myWriter;
    }



}
