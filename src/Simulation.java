//
// Simulation.java
// Nubot Simulator
//
// Created by David Chavez on 4/1/14.
// Copyright (c) 2014 Algorithmic Self-Assembly Research Group. All rights reserved.
//


import org.javatuples.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

class Simulation {
    Point canvasXYoffset;
    double lastr = 0;
    double speedRate = 0;
    public volatile
    int monomerRadius = 15;
    boolean configLoaded = false;
    boolean rulesLoaded = false;
    public boolean debugMode = false;
    boolean isPaused = false;
    boolean isRunning = false;
    boolean agitationON = false;
    boolean isRecording = false;
    int recordingLength = 2147483647;
    boolean animate = true;
    double agitationRate = 0.0;

    static Point getCanvasPosition(Point gridPosition, Point xyOffset, int radius) {
        return new Point(xyOffset.x + gridPosition.x * 2 * radius + gridPosition.y * radius - radius, -1 * (xyOffset.y + gridPosition.y * 2 * radius - radius));
    }

    static double calculateExpDistribution(double i) {
        Random rand = new Random();
        double randNum = rand.nextDouble();

        while (randNum == 0.0)
            randNum = rand.nextDouble();

        return (-1 * Math.log(randNum)) / i;
    }


    static Pair<Point, Point> calculateMinMax(ArrayList<Monomer> monList, int radius, Point offset, Dimension bounds) {


        Point maxXY = new Point(0, 0);
        Point minXY = new Point(bounds.width / 2, bounds.height / 2);

        for (Monomer m : monList) {
            Point gridLocation = m.getLocation();
            Point pixelLocation = Simulation.getCanvasPosition(gridLocation, offset, radius);
            maxXY.x = Math.max(maxXY.x, pixelLocation.x);
            minXY.x = Math.min(minXY.x, pixelLocation.x);
            maxXY.y = Math.max(maxXY.y, pixelLocation.y);
            minXY.y = Math.min(minXY.y, pixelLocation.y);

        }


        return Pair.with(minXY, maxXY);
    }


}