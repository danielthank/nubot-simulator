import org.monte.media.Format;
import org.monte.media.quicktime.QuickTimeWriter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class NubotVideo implements NubotDraws {
    boolean ready = false;
    private QuickTimeWriter qtWriter;
    private double frameDuration = 0;
    private int frameRate = 60;
    private File file;
    private Dimension resolution = new Dimension(0, 0);
    private BufferedImage cFrameBFI;
    private Graphics2D frameGFX;
    private int monomerRadius = 15;
    private Point XYDrawOffset = new Point(0, 0);

    public NubotVideo(final int rWidth, final int rHeight, Format vFormat, final int framerate, final String vFileName) {
        File vF = new File(vFileName + ".mov");
        try {

            this.qtWriter = new QuickTimeWriter(vF);
            this.file = vF;
            this.frameRate = framerate;
            this.frameDuration = 1.0 / framerate;

            this.resolution.setSize(rWidth, rHeight);
            this.qtWriter.addVideoTrack(vFormat, framerate, rWidth, rHeight);
            this.cFrameBFI = new BufferedImage(rWidth, rHeight, BufferedImage.TYPE_INT_ARGB);
            this.frameGFX = (Graphics2D) cFrameBFI.getGraphics();
            this.setOffset(rWidth / 2, -rHeight / 2);
            initCGFX(this.frameGFX);
            this.ready = true;
        } catch (Exception exception) {
            System.out.println("NubotVideo.java-NubotVideo(Constructor): " + exception.getMessage());
        }

    }

    public int getMonomerRadius() {
        return this.monomerRadius;
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

    public void setMonomerRaidus(final int radius) {
        this.monomerRadius = radius;
    }

    public void monomerRadiusDecrement() {
        monomerRadius -= 1;
    }

    public void monomerRadiusIncrement() {
        monomerRadius += 1;
    }

    public void clearGFX(Graphics2D gfx) {
        if (ready && this.frameGFX != null) {
            gfx.setComposite(AlphaComposite.Clear);
            gfx.fillRect(0, 0, cFrameBFI.getWidth(), cFrameBFI.getHeight());
            gfx.setComposite(AlphaComposite.SrcOver);

        }
    }

    public void initCGFX(Graphics2D cgfx) {
        cgfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        cgfx.setFont(new Font("TimesRoman", Font.PLAIN, Math.max(cFrameBFI.getHeight() / 80, cFrameBFI.getWidth() / 60)));
    }

    public Graphics2D getGFX() {
        return frameGFX;
    }

    public BufferedImage getBFI() {

        return cFrameBFI;
    }

    public int getResWidth() {
        return resolution.width;
    }

    public int getResHeight() {
        return resolution.height;
    }

    public Dimension getRes() {
        return resolution;
    }

    public double getFrameDuration() {
        return frameDuration;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void encodeFrame(BufferedImage bfi) {
        if (ready && qtWriter != null) {
            try {
                this.qtWriter.write(0, bfi, 1);
            } catch (Exception exception) {
                System.out.println("NubotVideo.java-encodeFrame(BufferedImage): " + exception.getMessage());
            }
        }
    }

    public void encodeFrame(long duration) {
        if (duration < 1)
            duration = 1;
        if (this.ready && this.qtWriter != null) {
            System.out.println("sup " + duration + "  " + this.frameRate);
            try {
                if (duration > this.frameRate) {
                    int reps = (int) (duration / this.frameRate);

                    for (int i = 0; i < reps; i++) {
                        this.qtWriter.write(0, cFrameBFI, 20);
                    }
                    this.qtWriter.write(0, cFrameBFI, duration % this.frameRate);
                } else
                    this.qtWriter.write(0, cFrameBFI, duration);


            } catch (Exception exception) {
                System.out.println("NubotVideo.java-encodeFrame(): " + exception.getMessage());
            }
        }
    }

    public void finish() {
        if (qtWriter != null) {
            try {
                this.qtWriter.close();
            } catch (Exception exception) {
                System.out.println("NubotVideo.java-finish(): " + exception.getMessage());
            }

        }
    }

    public void init() {

    }

}
