/**
 * Created by daniel on 2016/10/26.
 */

import java.io.*;
import java.util.concurrent.*;

public class Script {
    public static void main(String args[]) {
        Configuration map = new Configuration();
        map.loadFile(new File("./conf/my.rules"));

        int numRecords = 100;
        int recordLength = 10;

        try (PrintWriter log = new PrintWriter(new BufferedWriter(new FileWriter("output.txt", true)))) {
            ExecutorService execServ = Executors.newFixedThreadPool(10);
            CountDownLatch doneLatch = new CountDownLatch(numRecords);
            for (int i = 1; i <= numRecords; i++) {
                execServ.submit(() -> {
                    Configuration rMap = map.getCopy();
                    rMap.simulation.recordingLength = recordLength;
                    rMap.simulation.isRecording = false;
                    rMap.simulation.isRunning = true;
                    while (rMap.simulation.isRunning) {
                        rMap.computeTimeStep();
                        rMap.executeFrame();
                    }
                    System.out.println(rMap.getMiddleLength());
                    log.println(rMap.getMiddleLength());
                    doneLatch.countDown();
                });
            }
            execServ.shutdown();
            try {
                doneLatch.await();
            } catch(InterruptedException e) {
                System.out.println("interrupted");
            }
        }
        catch(IOException e) {
            System.out.println("ERROR");
        }
        System.out.println("OK");
    }
}
