import java.awt.*;

public interface NubotDraws {
    //a way to decrement the monomer radius
    public void monomerRadiusDecrement();

    //increment monomer radius
    public void monomerRadiusIncrement();

    //return the monomer radius
    public int getMonomerRadius();

    // set the monomer radius
    public void setMonomerRadius(int radius);

    //set the offset of any drawn object, be it monomer,bond, or string
    public void setOffset(int x, int y);

    //translate the off by x and y
    public void translateOffset(int x, int y);

    //return the x value of the offset
    public int getOffsetX();

    // return the y value of the offset
    public int getOffsetY();

    //return the offset
    public Point getOffset();


}
