
package manosrobertcs340p2;

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.MIN_PRIORITY;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Robert Manos *
 */
public class Racer extends Thread {
        
    public static final Semaphore riverReady = new Semaphore(0);    // This semaphore is used to control the release of racers over the river by the judge

    private static Semaphore mountainMutex = new Semaphore(1, true);    //This is a mutex that only lets one racer over the mountain at a time
    public static Semaphore doneReport = new Semaphore(0);  //This is used by the judge to only release the report once all the racesr have entered
    private static Semaphore[] goHomeArray;     //This is used to cascade the go home from one racer to the next starting with the last racer thread ~racer10
    public static int nRacer;
    public static int numLines;

    private boolean ready = false;
    private String Name;
    public static long time = System.currentTimeMillis();
    public static Vector<Racer> line = new Vector(nRacer);
    public int idnum;
    public static Vector<Racer> riverLine = new Vector(nRacer);
    public static Racer[] pool;
    public static int done = 0;
    private static int lineKey;
    private static String key = "";
    private static String[] fMap;
    public static Judge theJudge;

    public static void Init() {
//sets up the map
        String[] compass = {"a", "b", "c", "d"};
        int size = ThreadLocalRandom.current().nextInt(301, 499);
        lineKey = ThreadLocalRandom.current().nextInt(size);
        fMap = new String[size];
        for (int i = 0; i < size; i++) {
            fMap[i] = (compass[ThreadLocalRandom.current().nextInt(3)]
                    + compass[ThreadLocalRandom.current().nextInt(3)]
                    + compass[ThreadLocalRandom.current().nextInt(3)]
                    + compass[ThreadLocalRandom.current().nextInt(3)]);
            if (i == lineKey) {
                key = fMap[i];
            }
        }
        //sets up an array of semaphores to execute "going home"
        goHomeArray = new Semaphore[nRacer];
        for (int i = 0; i < nRacer; i++) {
            goHomeArray[i] = new Semaphore(0);
        }

    }

    public static void setNRacer(int NewnRacer) {
        nRacer = NewnRacer;
        pool = new Racer[nRacer];
    }

    public Racer(int id) {
        idnum = id;
        setName("Thread-" + id);
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
        theJudge.signal(this.idnum, m, (System.currentTimeMillis() - time));
    }

    private void rest() {
        try {
            this.sleep(ThreadLocalRandom.current().nextInt(6710));
        } catch (InterruptedException ex) {
            // ex.printStackTrace();
        }
        this.msg("resting");
    }

    private void forest() {
        this.msg("entering forest");
        //if found ->leave forest
        for (int i = 0; i < fMap.length; i++) {
            if (fMap[i] == key) {
                this.msg("exiting forest, has Found key is exiting Forest");
                break;
            }

        }
    }

    private void mountain() {
        this.msg("entering mountain");

        try {
            mountainMutex.acquire();
            try {
                this.msg("crossing mountain!");

                this.sleep(ThreadLocalRandom.current().nextInt(6710));
            } finally {
                mountainMutex.release();
                this.msg("exiting mountain");
            }
        } catch (InterruptedException ie) {
            // ...
        }
    }

    private void river() {
        this.msg("entering the river");
        try {
            riverReady.acquire();
            this.msg("crossing the river ");
            this.sleep(ThreadLocalRandom.current().nextInt(6710));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        this.msg("exiting the river");
    }

    public boolean isResting() {
        return this.getState().toString() == "TIMED_WAITING";
    }

    private void goHome() {

        if (this.idnum == nRacer - 1) {
            goHomeArray[this.idnum].release(1);
        } else {
            try {
                goHomeArray[this.idnum].acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Racer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        this.msg("going home");
        if (this.idnum != 0) {
            goHomeArray[this.idnum - 1].release(1);
        }

    }

    private void doneReport() {

        try {
            doneReport.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Racer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        msg("starting");
        rest();
        forest();
        rest();
        mountain();
        rest();
        river();
        msg("FinishedRace");
        doneReport();
        goHome();

    }

}
