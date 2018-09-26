
package manosrobertcs340p2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Robert Manos
 */
public class Judge extends Thread {

    private String Name;
    public static long time = System.currentTimeMillis();
    public static Racer[] pool;
    private static ArrayList<ArrayList<String>> keepTrack = new ArrayList<ArrayList<String>>();

    public Judge(Racer[] pool) {
        this.pool = Racer.pool;
        for (int i = 0; i < pool.length; i++) {
            ArrayList<String> tmp = new ArrayList<String>();
            keepTrack.add(tmp);
        }
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
    }

    public void signal(int a, String m, long b) {
        keepTrack.get(a).add(m);
        keepTrack.get(a).add(String.valueOf(b));
    }

    @Override
    public void run() {
        //check if has remainder group 
        boolean hasRemainder = false;
        int remainder = 0;
        if (Racer.nRacer % Racer.numLines != 0) {
            hasRemainder = true;
            remainder = Racer.nRacer % Racer.numLines;
        }
        int i = 0;
        while (i < Racer.nRacer / Racer.numLines) {
            if (Racer.riverReady.getQueueLength() == 3) {
                Racer.riverReady.release(3);
                i++;
            }
        }
        //this is necessary if the number of racers%numlines is not zero i.e. does not fit evenly
        while (hasRemainder) {
            if (Racer.riverReady.getQueueLength() == remainder) {
                Racer.riverReady.release(remainder);
                hasRemainder = false;
            }
        }
        //check report semaphore
        while (Racer.doneReport.getQueueLength() != Racer.nRacer) {
        }//busy wait  
        judgeReport();
        Racer.doneReport.release(Racer.nRacer);
        System.out.println("Judge done!");

    }

    private static void judgeReport() {
        ArrayList<Float> sortWithSleep = new ArrayList<Float>();
        ArrayList<Float> sortWithoutSleep = new ArrayList<Float>();
        ArrayList<Float> resultWithSleep = new ArrayList<Float>();
        ArrayList<Float> resultWithoutSleep = new ArrayList<Float>();
        //for( int l: keepTrack){
        for (int i = 0; i < keepTrack.size(); i++) {
            Float total = Float.valueOf(keepTrack.get(i).get(25)) - Float.valueOf(keepTrack.get(i).get(1));
            resultWithSleep.add(total);

        }
        sortWithSleep = resultWithSleep;
        ///Calculate result without sleep
        for (int i = 0; i < keepTrack.size(); i++) {

            Float total = Float.valueOf(keepTrack.get(i).get(25)) - Float.valueOf(keepTrack.get(i).get(1));
            total = total - (Float.valueOf(keepTrack.get(i).get(3)) - Float.valueOf(keepTrack.get(i).get(5)));
            total = total - (Float.valueOf(keepTrack.get(i).get(9)) - Float.valueOf(keepTrack.get(i).get(11)));

            total = total - (Float.valueOf(keepTrack.get(i).get(17)) - Float.valueOf(keepTrack.get(i).get(19)));
            sortWithoutSleep.add(total);
            resultWithoutSleep.add(total);
        }
        Collections.sort(sortWithSleep, new Comparator<Float>() {
            public int compare(Float s1, Float s2) {
                return s1.compareTo(s2);
            }
        });

        Collections.sort(sortWithoutSleep, new Comparator<Float>() {
            public int compare(Float s1, Float s2) {
                return s1.compareTo(s2);
            }
        });

        System.out.println("The_result_With_Sleep_:");
        for (int i = 0; i < resultWithSleep.size(); i++) {

            System.out.println("Place #" + i + "was racer" + resultWithSleep.indexOf(sortWithSleep.get(i)) + "with total time:" + sortWithSleep.get(i));
        }

        System.out.println("The_result_WithOUT!_Sleep_:");
        for (int i = 0; i < resultWithoutSleep.size(); i++) {
            System.out.println("Place #" + i + "was racer" + resultWithoutSleep.indexOf(sortWithoutSleep.get(i)) + "with total time:" + sortWithoutSleep.get(i));

        }

    }

}
