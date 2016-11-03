import org.javatuples.Pair;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

class RecordRunnable implements Runnable {

    private NubotVideo nubotVideo;
    private Configuration mapCopy;
    private double nubRatio;
    private PrintWriter log;
    private CountDownLatch latch;

    RecordRunnable(NubotVideo nubVid, Configuration map, double ratio, PrintWriter pw, CountDownLatch _latch) {
        nubotVideo = nubVid;
        mapCopy = map;
        nubRatio = ratio;
        log = pw;
        latch = _latch;

        NubotDrawer.drawNubotVideoFrame(nubotVideo.getBFI(), "#Monomers: " + map.size() + "\nStep: " + map.markovStep + "\nTime: " + map.timeElapsed, new ArrayList<Monomer>(map.values()), nubotVideo.getMonomerRadius(), nubotVideo.getOffset());
        nubotVideo.encodeFrame(1);
    }


    @Override
    public void run() {
        double t;
        double realt;
        double fakeFloorRealT;
        while (mapCopy.simulation.isRunning) {
            if (mapCopy.simulation.isRecording) {
                t = mapCopy.computeTimeStep();
                realt = t * nubRatio;
                fakeFloorRealT = realt - (realt % nubotVideo.getFrameDuration());
                System.out.println(mapCopy.size());
                System.out.println("recording running " + t + " " + nubRatio + " " + realt + " " + nubotVideo.getFrameDuration());
                if (fakeFloorRealT - mapCopy.simulation.lastr >= nubotVideo.getFrameDuration()) {
                    System.out.println("d" + (Math.round((fakeFloorRealT - mapCopy.simulation.lastr) / nubotVideo.getFrameDuration()) - 1));
                    NubotDrawer.drawNubotVideoFrame(nubotVideo.getBFI(), "#Monomers: " + mapCopy.size() + "\nStep: " + mapCopy.markovStep + "\nTime: " + Double.toString(mapCopy.timeElapsed), new ArrayList<Monomer>(mapCopy.values()), nubotVideo.getMonomerRadius(), nubotVideo.getOffset());
                    nubotVideo.encodeFrame(Math.round((fakeFloorRealT - mapCopy.simulation.lastr) / nubotVideo.getFrameDuration()) - 1);
                    System.out.println((((fakeFloorRealT - mapCopy.simulation.lastr) / nubotVideo.getFrameDuration()) - 1) + "ffrt/gfd-1 ");
                    mapCopy.executeFrame();
                    nubotVideo.encodeFrame(1);
                    mapCopy.simulation.lastr = fakeFloorRealT;
                    //get min and max frame draw points0
                    Pair<Point, Point> minMaxXY = mapCopy.simulation.calculateMinMax(new ArrayList<Monomer>(mapCopy.values()), nubotVideo.getMonomerRadius(), nubotVideo.getOffset(), nubotVideo.getRes());
                    //get the caculation dimension of the mapCopy
                    Dimension nubotDimension = new Dimension(minMaxXY.getValue1().x - minMaxXY.getValue0().x + nubotVideo.getMonomerRadius() * 2, minMaxXY.getValue1().y - minMaxXY.getValue0().y + nubotVideo.getMonomerRadius() * 2);
                    //reduce monomer radius if it exceeds the video resolution
                    while (nubotDimension.width > nubotVideo.getResWidth() || nubotDimension.getHeight() > nubotVideo.getResHeight()) {
                        if (nubotVideo.getMonomerRadius() > 1)
                            nubotVideo.monomerRadiusDecrement();
                    }
                    //re-calculate dimensions
                    minMaxXY = mapCopy.simulation.calculateMinMax(new ArrayList<Monomer>(mapCopy.values()), nubotVideo.getMonomerRadius(), nubotVideo.getOffset(), nubotVideo.getRes());
                    int minX = minMaxXY.getValue0().x;
                    int minY = minMaxXY.getValue0().y;
                    int maxX = minMaxXY.getValue1().x;
                    int maxY = minMaxXY.getValue1().y;
                    int monDiam = 2 * nubotVideo.getMonomerRadius();
                    nubotDimension = new Dimension(minMaxXY.getValue1().x - minMaxXY.getValue0().x + nubotVideo.getMonomerRadius() * 2, minMaxXY.getValue1().y - minMaxXY.getValue0().y + nubotVideo.getMonomerRadius() * 2);
                    if (mapCopy.simulation.agitationON && nubotDimension.height < nubotVideo.getResHeight() && nubotDimension.width < nubotVideo.getResWidth()) {
                        //translate the canvas xy offset left or up if there is a draw point outside the right and bottom bounderies
                        nubotVideo.translateOffset(maxX + monDiam > nubotVideo.getResWidth() ? -maxX - monDiam + nubotVideo.getResWidth() - (nubotVideo.getResWidth() - nubotDimension.width) / 2 : 0, maxY + monDiam > nubotVideo.getResHeight() ? maxY + monDiam - nubotVideo.getResHeight() + (nubotVideo.getResHeight() - nubotDimension.height) / 2 : 0);
                        //translate right and down if minimum draw points are outside
                        nubotVideo.translateOffset(minX < 0 ? Math.abs(minX) + (nubotVideo.getResWidth() - minMaxXY.getValue1().x) / 2 : 0, minY < 0 ? -Math.abs(minY) - (nubotVideo.getResHeight() - maxY) / 2 : 0);
                    } else {
                       /* mapCopy.simulation.canvasXYoffset.translate(minMaxXY.getValue1().x + mapCopy.simulation.monomerRadius*2 > nubotVideo.getResWidth() ? -(minMaxXY.getValue0().x)/2 : 0, minMaxXY.getValue1().y + mapCopy.simulation.monomerRadius*2 > nubotVideo.getResHeight() ? (minMaxXY.getValue0().y)/2 : 0);
                         mapCopy.simulation.canvasXYoffset.translate(minMaxXY.getValue0().x < 0 ? (nubotDimension.width - minMaxXY.getValue1().x)/2 : 0, minMaxXY.getValue0().y < 0 ? -(nubotVideo.getResHeight() - minMaxXY.getValue1().y)/2 : 0 );    */
                    }
                }
            } else {
                mapCopy.computeTimeStep();
                mapCopy.executeFrame();
            }
        }
        if (mapCopy.simulation.isRecording && nubotVideo != null) {
            NubotDrawer.drawNubotVideoFrame(nubotVideo.getBFI(),
    /*Top left string*/   "#Monomers: " + mapCopy.size() + "\nStep: " + mapCopy.markovStep + "\nTime: " + Double.toString(mapCopy.timeElapsed),
                    new ArrayList<Monomer>(mapCopy.values()),
                    nubotVideo.getMonomerRadius(),
                    nubotVideo.getOffset());
            nubotVideo.encodeFrame(1);
            nubotVideo.finish();
        }
        latch.countDown();
    }
}
