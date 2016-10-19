import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NubotCanvas extends JComponent implements NubotDraws {

    //Singleton
    private static NubotCanvas simInstance;
    Configuration map = Configuration.getInstance();
    private BufferedImage canvasBFI;
    private BufferedImage hudBFI;
    private Graphics2D hudGFX;
    private Graphics2D canvasGFX;
    private Dimension canvasDimension = new Dimension(0, 0);
    private int monomerRadius = 15;
    private Point XYDrawOffset = new Point(0, 0);
    public NubotCanvas(Dimension size) {
        setSize(size);
        canvasDimension.setSize(size);
        XYDrawOffset.setLocation(size.width / 2, -size.height / 2);
        init();

    }

    public static NubotCanvas getSimInstance() {
        if (simInstance == null) {
            System.out.println("NubotCanvas.java: simInstance is null.");
            return null;
        }
        return simInstance;
    }

    public static void setSimInstance(NubotCanvas nubotCanvas) {
        simInstance = nubotCanvas;
    }

    public int getMonomerRadius() {
        return monomerRadius;
    }

    public void setMonomerRadius(int radius) {
        this.monomerRadius = radius;
    }

    public void setOffset(int x, int y) {
        XYDrawOffset.setLocation(x, y);
    }

    public void translateOffset(int x, int y) {
        XYDrawOffset.translate(x, y);
    }

    public int getOffsetX() {
        return XYDrawOffset.x;
    }

    public int getOffsetY() {
        return XYDrawOffset.y;
    }

    public Point getOffset() {
        return XYDrawOffset;
    }

    public synchronized void renderNubot(Collection<Monomer> mapM) {

        NubotDrawer.clearGraphics(canvasGFX, canvasDimension);
        NubotDrawer.drawMonomers(canvasGFX, new ArrayList<Monomer>(mapM), monomerRadius, XYDrawOffset);
        repaint();

    }

    public void init() {
        canvasBFI = new BufferedImage(canvasDimension.width, canvasDimension.height, BufferedImage.TYPE_INT_ARGB);
        canvasGFX = canvasBFI.createGraphics();
        canvasGFX.setClip(0, 0, canvasDimension.width, canvasDimension.height);
        canvasGFX.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        XYDrawOffset.setLocation(canvasDimension.width / 2, -canvasDimension.height / 2);
        //for the nubot graphics/image & visuals
        hudBFI = new BufferedImage(canvasDimension.width, canvasDimension.height, BufferedImage.TYPE_INT_ARGB);
        hudGFX = (Graphics2D) hudBFI.getGraphics();
        hudGFX.setColor(Color.BLACK);
        hudGFX.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hudGFX.setFont(new Font("TimesRoman", Font.BOLD, 20));

    }

    @Override
    public synchronized void paintComponent(Graphics g) {

        g.drawImage(canvasBFI, 0, 0, null);
        g.drawImage(hudBFI, 0, 0, null);

    }

    public Graphics2D getGFX() {
        return canvasGFX;
    }

    public void showToast(int x, int y, String text, int duration) {
        NubotDrawer.clearGraphics(hudGFX, canvasDimension);
        hudGFX.drawString(text, x, y);
        repaint();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable clearBFI = new Runnable() {
            @Override
            public void run() {
                NubotDrawer.clearGraphics(hudGFX, canvasDimension);
                repaint();


            }
        };


        executor.schedule(clearBFI, duration, TimeUnit.MILLISECONDS);

    }

    public void monomerRadiusDecrement() {
        monomerRadius -= 1;
    }

    public void monomerRadiusIncrement() {
        monomerRadius += 1;
    }

}
