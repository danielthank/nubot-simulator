import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class NubotDrawer {
    public synchronized static void drawBond(Monomer m, Graphics2D g, int monomerRadius, Point offset) {

        float canvasStrokeSize = monomerRadius / 3;

        Monomer tempMon = new Monomer(m);
        if (tempMon.hasBonds()) {
            ArrayList<Byte> rigidDirList = tempMon.getDirsByBondType(Bond.TYPE_RIGID);
            ArrayList<Byte> flexibleDirList = tempMon.getDirsByBondType(Bond.TYPE_FLEXIBLE);

            g.setColor(Color.RED);
            for (Byte dir : rigidDirList) {
                Point start = Simulation.getCanvasPosition(tempMon.getLocation(), offset, monomerRadius);
                Point end = Simulation.getCanvasPosition(Direction.getNeighborPosition(tempMon.getLocation(), dir), offset, monomerRadius);
                start.translate(monomerRadius, monomerRadius);
                end.translate(monomerRadius, monomerRadius);
                g.setStroke(new BasicStroke(canvasStrokeSize));
                g.draw(new Line2D.Float(start.x - monomerRadius / 3.5f, start.y, end.x, end.y));
            }

            for (Byte dir : flexibleDirList) {
                Point start = Simulation.getCanvasPosition(tempMon.getLocation(), offset, monomerRadius);
                Point end = Simulation.getCanvasPosition(Direction.getNeighborPosition(tempMon.getLocation(), dir), offset, monomerRadius);
                start.translate(monomerRadius, monomerRadius);
                end.translate(monomerRadius, monomerRadius);
                g.setStroke(new BasicStroke(canvasStrokeSize * 1.20f));
                g.setColor(Color.RED);
                g.draw(new Line2D.Float(start.x - monomerRadius / 3.5f, start.y, end.x, end.y));
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(canvasStrokeSize * .80f));
                g.draw(new Line2D.Float(start.x - monomerRadius / 3.5f, start.y, end.x, end.y));
            }
        }
    }

    public synchronized static void drawMonomers(Graphics g, ArrayList<Monomer> monList, final int monomerRadius, final Point offset) {


        Monomer[] mapTemp = new Monomer[monList.size()];
        monList.toArray(mapTemp);

        ArrayList<Monomer> tempMonList = new ArrayList<Monomer>();

        for (Monomer m : mapTemp) {
            if (monomerRadius >= 7)
                drawBond(m, (Graphics2D) g, monomerRadius, offset);
            tempMonList.add(new Monomer(m));

        }


        for (Monomer m : tempMonList) {

            drawMonomer(m, (Graphics2D) g, monomerRadius, offset);
        }


    }

    public synchronized static void drawMonomer(Monomer m, Graphics2D g, final int monomerRadius, final Point offset) {

        Point xyPos = Simulation.getCanvasPosition(m.getLocation(), offset, monomerRadius);
        int fontSize = monomerRadius;
        int monomerWidthAdjustment = monomerRadius / 4;
        int monomerWidth = monomerRadius * 2 - monomerWidthAdjustment;
        int monomerHeight = monomerRadius * 2;
        g.setColor(Color.WHITE);
        g.fillOval(
                /*X coord*/   xyPos.x,
                /*Y coord*/   xyPos.y,//  -1* (m.getLocation().y * (int)(Math.sqrt(3) * Simulation.monomerRadius)),
                /*Width  */   monomerWidth,
                /*Height */   monomerHeight);
        g.setStroke(new BasicStroke(monomerRadius / 10));
        g.setColor(new Color(2, 180, 206));
        g.drawOval(
                /*X coord*/   xyPos.x,
                /*Y coord*/   xyPos.y,//
                /*Width  */   monomerWidth,
                /*Height */   monomerHeight);
        g.setColor(Color.white);
        Rectangle2D bounds = g.getFont().getStringBounds(m.getState(), 0, m.getState().length(), g.getFontRenderContext());
        while (bounds.getWidth() < monomerWidth - 4 && bounds.getHeight() < monomerHeight - 4) {
            g.setFont(g.getFont().deriveFont((float) ++fontSize));
            bounds = g.getFont().getStringBounds(m.getState(), 0, m.getState().length(), g.getFontRenderContext());
        }
        while (bounds.getWidth() > monomerWidth - 4 || bounds.getHeight() > monomerHeight - 4) {
            g.setFont(g.getFont().deriveFont((float) --fontSize));
            bounds = g.getFont().getStringBounds(m.getState(), 0, m.getState().length(), g.getFontRenderContext());
        }
        g.setColor(Color.BLACK);
        g.drawString(
                /*String */     m.getState(),
                /*X Coord */    xyPos.x + monomerRadius - (int) bounds.getWidth() / 2 - monomerWidthAdjustment / 2,
                /*Y Coord */    xyPos.y + monomerRadius + (int) (bounds.getHeight() / 3.5));
    }

    public synchronized static void drawNubotVideoFrame(BufferedImage bfi, String topLeftString, ArrayList<Monomer> monList, int monomerRadius, Point offset) {

        int width = bfi.getWidth();
        int height = bfi.getHeight();
        Graphics2D gfx = (Graphics2D) bfi.getGraphics();

        gfx.setColor(Color.white);
        gfx.fillRect(0, 0, width, height);
        gfx.setColor(Color.BLACK);
        int lc = 1;
        for (String line : topLeftString.split("\n")) {
            Rectangle2D strDim = gfx.getFont().getStringBounds(line, 0, line.length(), gfx.getFontRenderContext());
            gfx.drawString(line, 20, 40 + (int) strDim.getHeight() * lc++);
        }

        try {

            drawMonomers(gfx, monList, monomerRadius, offset);

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

    }

    public synchronized static void clearGraphics(Graphics g, Dimension dimension) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, dimension.width, dimension.height);
        g2.setComposite(AlphaComposite.SrcOver);
    }

    public static Point getCanvasToGridPosition(Point canvasPosition, Point offset, int monRadius) {
        double monDiam = (2.0 * monRadius);
        int gridY = (int) Math.ceil((-canvasPosition.y - offset.y + monRadius) / monDiam);
        int gridX = (int) Math.ceil((canvasPosition.x - offset.x + monRadius - (gridY * monRadius)) / monDiam) - 1; // - 2*(gridY);

        return new Point(gridX, gridY);
    }


}
