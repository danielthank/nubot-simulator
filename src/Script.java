/**
 * Created by daniel on 2016/10/26.
 */
import java.io.*;

public class Script {
    public static void main(String args[]) {
        Configuration map = new Configuration();
        map.loadFile(new File("./conf/my.rules"));
    }
}
