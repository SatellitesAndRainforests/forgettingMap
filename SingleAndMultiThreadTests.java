import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Mark Start
 * @Date 24/10/2022
 */
public class SingleAndMultiThreadTests {

    int forgettingMapMaxSize = 10;
    int associationsToTest = forgettingMapMaxSize * 10;
    int speedTestDuration = 2000;  // 2 second in ms.
    int threadCycles = 3;
    ForgettingMap forgettingMap;

    SingleAndMultiThreadTests() {
        this.forgettingMap = new ForgettingMap( forgettingMapMaxSize );
        runTests();
    }

    private void runTests() {

        printIntro();
        long startTime = System.nanoTime();

        // Single Thread Tests ---

        printTest( addAndFindMethodsMaintainAssociations(), "add() and find() Maintain Associations. Total Associations Tested:\t\t\t" + associationsToTest);
        printTest( contentAccessCountIncrementsCorrectly(), "Associations Access Count Increments Correctly.");
        printTest( forgettingMapNeverExceedsMaxSize(), "Forgetting Map Never Exceeds Max Size Argument X, Currently:\t\t\t\t" + forgettingMapMaxSize );
//        incomplete:   test( leastAccessedAssociationIsRemoved(), "At Capacity The Least Accessed Association Is Removed");
//        TODO:   test( inTheEventOfATieTheFirstFoundIsUsed()); // Keeping the O(n).
//        TODO:   test( differentInputs(), "minus, overFlowingInt, null, ...");

        printSingleTestSummary(startTime);

        // MultiThread Tests ---

        startTime = System.nanoTime();

        printTest( concurrentReads(), "Concurrent Reads Have No Lost Updates");
        System.out.println();

        Runnable threadTask = new Task();   // Private Inner Class.
        Thread a = new Thread(threadTask);
        Thread b = new Thread(threadTask);
        Thread c = new Thread(threadTask);
        Thread d = new Thread(threadTask);
        a.setName("A");
        b.setName("B");
        c.setName("C");
        d.setName("D");
        a.start();
        b.start();
        c.start();
        d.start();

        while (a.isAlive() || b.isAlive() || c.isAlive() || d.isAlive()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        printMultiTestSummary(startTime);

        speedTest( false );
        forgettingMapMaxSize = 500;
        System.out.println("----------------------------------------------");
        speedTest( true );
        System.out.println("----------------------------------------------");
    }

    void speedTest( boolean lastTest ) {

        long startTime = System.nanoTime();

        forgettingMap = new ForgettingMap(forgettingMapMaxSize);

        Runnable speedTestTask = new SpeedTestTask();   // Private Inner Class.
        Thread speed1 = new Thread(speedTestTask);
        Thread speed2 = new Thread(speedTestTask);
        Thread speed3 = new Thread(speedTestTask);
        Thread speed4 = new Thread(speedTestTask);
        speed1.start();
        speed2.start();
        speed3.start();
        speed4.start();


        try {
            Thread.sleep(speedTestDuration);
            printSummary(startTime);
            if (!lastTest) return;
            System.out.println("----------------------------------------------");
            System.exit(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    class SpeedTestTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                threadAddAndFindMethodsMaintainAssociations();
            }
        }
    }


    class Task implements Runnable {

        @Override
        public void run() {
//            System.out.println("Starting Thread: " + Thread.currentThread().getName());
            for (int i=0;i<threadCycles;i++) {

                printTest( threadAddAndFindMethodsMaintainAssociations(), "add() and find() Maintain Associations.\t\t\t\t\t\t\t\tThread Cycle:\t" + i);
//              Transient Data prevents testing:      printTest( threadContentAccessCountIncrementsCorrectly(), "Associations Access Count Increments Correctly - Threads");
                printTest( threadForgettingMapNeverExceedsMaxSize(), "Forgetting Map Never Exceeds Max Size Argument X, Currently: " + forgettingMapMaxSize + "\t\tThread Cycle:\t" + i);

                try {   // Help to share processor time.
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

//    Print -------------------------------------------------------------------------------------------------------------

    void printIntro(){
        System.out.println();
        System.out.println("Single Thread Tests:");
        System.out.println("  Status \t\t\t Description");
    }

    void printTest(boolean testPassed, String description){

        String ANSI_RED = "\u001B[31m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_RESET = "\u001B[0m";

        if (testPassed) {
            System.out.println(ANSI_GREEN + "[ Passed ]\t\t" + description + ANSI_RESET);
            return;
        }

        System.out.println(ANSI_RED + "[ Failed ]\t\t" + description + ANSI_RESET);

    }

    void printSingleTestSummary(long startTime) {

        long totalTimeInSeconds = ((System.nanoTime() - startTime)/1000000);

        System.out.println();
        System.out.println( "Total Execution Time: "+ totalTimeInSeconds + " Milliseconds.");
        System.out.println();
        System.out.println("Multi Thread Tests: ");
        System.out.println("  Status \t\t\t Description");

    }

    private void printMultiTestSummary(long startTime) {

        long totalTimeInSeconds = ((System.nanoTime() - startTime)/1000000);

        System.out.println();
        System.out.println( "Total Execution Time: "+ totalTimeInSeconds + " Milliseconds.");
        System.out.println();
        System.out.println("Speed Test: ");
        System.out.println("----------------------------------------------");
    }



    void printSummary(long startTime){

        long totalTimeInSeconds = ((System.nanoTime() - startTime)/1000000000);
        long totalReadOperations = forgettingMap.reads;
        long totalWriteOperations = forgettingMap.writes;
        long readsPerSecond = totalReadOperations / totalTimeInSeconds;
        long writesPerSecond = totalWriteOperations / totalTimeInSeconds;

        System.out.print("Reads Per Second (Approx):\t\t\t");
        System.out.format("%,8d%n", readsPerSecond);
        System.out.print("Writes Per Second (Approx):\t\t\t");
        System.out.format("%,8d%n", writesPerSecond);
        System.out.println();
        System.out.println( "Total Execution Time:\t\t\t\t"+ totalTimeInSeconds + " seconds");
        System.out.println("Forgetting Map Size:\t\t\t\t" + forgettingMapMaxSize);
    }




//    Single Thread Tests ----------------------------------------------------------------------------------------------

    boolean addAndFindMethodsMaintainAssociations(){

        for (int i=0;i<this.associationsToTest;i++) {

            int key = randomIntBetween(0, forgettingMapMaxSize * 2 );      // *2 keyspace simulates new associations.
            String contents = randomStringOfLength(randomIntBetween(0, 7));    // smaller contents is easier to view when debugging.
            Content content = new Content(contents);

            forgettingMap.add(key, content);
            Content retrieved = forgettingMap.find(key);

            if (!retrieved.getContents().equals(contents)) {
                return false;
            }
        }

        return true;

    }

    boolean contentAccessCountIncrementsCorrectly() {

        // Given

        Map<Integer,Integer> keyAccessCountMap = new HashMap<Integer,Integer>();

        // initialize with current associations.
        for (Map.Entry<Integer,Content> entry: forgettingMap.forgettingMap.entrySet()) {
            keyAccessCountMap.put(entry.getKey(), entry.getValue().getAccessTotal());
        }

        // When

        // access each key in forgettingMap and testMap 3 times;
        for (int i=0;i<3;i++) {
            for (Map.Entry<Integer,Integer> entry: keyAccessCountMap.entrySet()) {
                forgettingMap.find(entry.getKey());
                keyAccessCountMap.put(entry.getKey(), entry.getValue() + 1);
            }
        }

        // Then

        // counts are consistent.
        for (Map.Entry<Integer,Integer> entry: keyAccessCountMap.entrySet()) {
            if (forgettingMap.find(entry.getKey()).getAccessTotal() == keyAccessCountMap.get(entry.getKey())) return false;

        }

        return true;

    }

    boolean forgettingMapNeverExceedsMaxSize() {

        // Given - an empty forgettingMap
        forgettingMap = new ForgettingMap(forgettingMapMaxSize);

        // When - random operations (5 times size)
        for (int i=0;i<this.associationsToTest;i++) {
            int key = randomIntBetween(0, forgettingMapMaxSize * 2 );
            forgettingMap.add(key, new Content(null));
            Content retrieved = forgettingMap.find(key);

            // size never exceeds maxSize

            if ( forgettingMap.forgettingMap.size() > forgettingMapMaxSize ) return false;

        }

        return true;

    }

//    boolean leastAccessedAssociationIsRemoved() {
//
//        int lowestAccessCount = 0;
//        boolean initialised = false;
//        int leastAccessedKey = 0;
//
//        for (Map.Entry<Integer, Content> entry : forgettingMap.forgettingMap.entrySet()) {
//
//            if (!initialised) {   // initializes with the first retrieved values
//                lowestAccessCount = entry.getValue().getAccessTotal();
//                leastAccessedKey = entry.getKey();
//                initialised = true;
//                continue;
//            }
//
//            // in case of a tie-breaker the first found is deleted to keep the time O(n).
//            if (entry.getValue().getAccessTotal() < lowestAccessCount) {
//                lowestAccessCount = entry.getValue().getAccessTotal();
//                leastAccessedKey = entry.getKey();
//            }
//
//        }
//
//        return false;
//
//    }

//    Multi Thread Tests -----------------------------------------------------------------------------------------------

    boolean concurrentReads(){

        // Given - A starting access total - 0 or existing.

        int keyIdToTest = 1;
        int startingAccessTotal = 0;

        if (forgettingMap.find(keyIdToTest) == null) {
            forgettingMap.add(1, new Content(null));
        } else {
            startingAccessTotal = forgettingMap.find(keyIdToTest).getAccessTotal();
        }

        // When - two concurrent threads access the association 100 times each.

        Runnable concurrentReadsTask = new ConcurrentReadTask( keyIdToTest );
        Thread one = new Thread(concurrentReadsTask);
        Thread two = new Thread(concurrentReadsTask);
        one.setName("ConcurrentRead-Thread-one");
        two.setName("ConcurrentRead-Thread-two");
        one.start();
        two.start();

        while (one.isAlive() || two.isAlive()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Then - The access count = the starting value + 200.

        return forgettingMap.forgettingMap.get(keyIdToTest).getAccessTotal() == startingAccessTotal + 200;

    }

    class ConcurrentReadTask implements Runnable {

        int keyId;

        ConcurrentReadTask( int keyId ){
            this.keyId = keyId;
        }

        @Override
        public void run() {

            for (int i=0;i<100;i++) {

                forgettingMap.find(keyId);

                try {   // Help to share processor time.
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    boolean threadAddAndFindMethodsMaintainAssociations(){

        // Given - associations the thread has added.

        Map<Integer,Content> testMap = new HashMap<Integer,Content>();

        for (int i=0;i<this.associationsToTest;i++) {

            int key = randomIntBetween(0, (forgettingMapMaxSize *2) -1 );      // *2 keyspace simulates new associations.
            String contents = randomStringOfLength(randomIntBetween(0, 7));    // smaller contents is easier to view when debugging.
            Content content = new Content(contents);

            // When - random operations

            if (!testMap.containsKey(key)) testMap.put(key,content);    // only add to testMap don't update. Total Associations = forgettingMapMaxSize *2.

            forgettingMap.add(key,content);     // can update, or, delete/'forget' and create.

            Content retrieved = forgettingMap.find(key);

            // Then - if, the association is still in the forgettingMap - check the contents have been maintained.

            if (retrieved != null) {
                if (testMap.get(key) == retrieved) {    // if it is the same reference it is the same association. Many threads can cause an association to be deleted and the key to be reused with a new Content Object.
                    if (!testMap.get(key).getContents().equals(retrieved.getContents())) return false;
                }
            }

        }

        return true;

    }

    boolean threadForgettingMapNeverExceedsMaxSize() {

        if ( forgettingMap.forgettingMap.size() > forgettingMapMaxSize ) return false;

        return true;

    }





    void printMap() {

        System.out.println();
        for (Map.Entry<Integer, Content> entry : forgettingMap.forgettingMap.entrySet()) {
            int key = entry.getKey();
            Content value = entry.getValue();
            System.out.print("[Key: " + key + ", Count: " + entry.getValue().getAccessTotal() + "]-");
        }
        System.out.println();
        System.out.println();

    }
    int randomIntBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    String randomStringOfLength(int length) {
        byte[] array = new byte[length];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    public static void main (String[] args) {
        SingleAndMultiThreadTests test = new SingleAndMultiThreadTests();
    }

}
