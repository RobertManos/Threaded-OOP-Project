
package manosrobertcs340p2;

/**
 *
 * @author Robert Manos
 */
public class ManosRobertCS340P2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        long time = System.currentTimeMillis();
        int nRacer = 10;
        Racer.numLines = 3;
        if (args.length != 0) {
            nRacer = Integer.valueOf(args[0]);

            Racer.numLines = Integer.valueOf(args[0]);
        }

        Racer.setNRacer(nRacer);
        Racer.Init();
        Judge theJudge;
        theJudge = new Judge(Racer.pool);
        Racer.theJudge = theJudge;

        for (int i = 0; i < nRacer; i++) {

            Racer.pool[i] = new Racer(i);
            Racer.pool[i].start();
        }
        theJudge.run();

    }

}
