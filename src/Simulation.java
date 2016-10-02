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

public class
Simulation {
    public Point canvasXYoffset;
    public double lastr = 0;
    public double speedRate = 0;
    public volatile
    int monomerRadius = 15;
    public boolean configLoaded = false;
    public boolean rulesLoaded = false;
    public boolean debugMode = false;
    public boolean isPaused = false;
    public boolean isRunning = false;
    public boolean agitationON = false;
    public boolean isRecording = false;
    public int recordingLength = 1000;
    public boolean animate = true;

    public double agitationRate = 0.0;


   /* public static  Point getCanvasPosition(Point gridPosition) {
        return new Point(canvasXYoffset.x + gridPosition.x * 2 * monomerRadius + gridPosition.y * monomerRadius - monomerRadius, -1 * (canvasXYoffset.y + gridPosition.y * 2 * monomerRadius - monomerRadius));
    }*/


    public static Point getCanvasPosition(Point gridPosition, Point xyOffset, int radius) {
        return new Point(xyOffset.x + gridPosition.x * 2 * radius + gridPosition.y * radius - radius, -1 * (xyOffset.y + gridPosition.y * 2 * radius - radius));
    }

    public static double calculateExpDistribution(double i) {
        Random rand = new Random();
        double randNum = rand.nextDouble();

        while (randNum == 0.0)
            randNum = rand.nextDouble();

        return (-1 * Math.log(randNum)) / i;
    }


    public static Pair<Point, Point> calculateMinMax(ArrayList<Monomer> monList, int radius, Point offset, Dimension bounds) {


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