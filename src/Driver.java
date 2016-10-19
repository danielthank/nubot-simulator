import org.monte.media.quicktime.QuickTimeWriter;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Driver {
    //Video
    public double nubRatio = 0;
    public NubotVideo nubotVideo;
    private Thread simHeartBeat;
    private Runnable simRunnable;
    private RecordRunnable recordRunnable;
    private MainFrame mainFrame;
    private NubotCanvas simNubotCanvas;
    private Configuration map;
    private Configuration mapCC;

    Driver(final Dimension size) {
        map = Configuration.getInstance();
        mainFrame = new MainFrame(size, this);
        simNubotCanvas = NubotCanvas.getSimInstance();

        simRunnable = () -> {
            simNubotCanvas.repaint();
            while (map.simulation.isRunning) {
                try {
                    if (!map.simulation.isRecording)
                        Thread.sleep((long) (map.simulation.speedRate * 1000.0 * map.timeStep));

                    if (map.simulation.animate) {
                        map.computeTimeStep();
                        map.executeFrame();
                        mainFrame.renderNubot(map.values());
                    }

                    mainFrame.setStatus("Simulating...", null, null, "Monomers: " + map.getSize(), "Time: " + map.timeElapsed, "Step: " + map.markovStep);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            mainFrame.setStatus("Simulation finished ", null, null, null, null, null);
            if (map.isFinished) {
                JOptionPane.showMessageDialog(simNubotCanvas, "No more rules can be applied!", "Finished", JOptionPane.OK_OPTION);
            }
        };
    }

    void createMapCC() {
        mapCC = map.getCopy();
    }

    void simStart() {
        simStop();
        simHeartBeat = new Thread(simRunnable);
        simHeartBeat.start();
    }

    void simStop() {
        if (simHeartBeat != null && simHeartBeat.isAlive())
            simHeartBeat.interrupt();
    }

    void recordSim(String vidName, int numRecords, int recordLength, boolean toEnd, double ratio, boolean RtoN) {

        ExecutorService execServ = Executors.newFixedThreadPool(numRecords);

        for (int i = 1; i <= numRecords; i++) {
            Configuration rMap = mapCC.getCopy();
            rMap.simulation.recordingLength = recordLength;
            rMap.simulation.isRecording = true;
            rMap.simulation.isRunning = true;

            execServ.submit(new RecordRunnable(new NubotVideo(800, 600, QuickTimeWriter.VIDEO_PNG, 20, vidName + i), rMap, ratio));
        }
        execServ.shutdown();
    }
}
