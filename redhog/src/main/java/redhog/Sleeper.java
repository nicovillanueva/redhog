package redhog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by nico on 3/20/16.
 */
public class Sleeper {
    public static void sleepFor(int time, String unit) {
        List<String> valids = new ArrayList<>();
        valids.add("seconds");
        valids.add("milliseconds");
        valids.add("minutes");
        if(! valids.contains(unit)) {
            System.out.println("Invalid time unit, defaulting to seconds");
            unit = "seconds";
        }
        unit = unit.toUpperCase();
        try {
            TimeUnit.valueOf(unit).sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
