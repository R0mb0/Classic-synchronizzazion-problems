package francescorombaldoni.readers.and.writers.condition.variables;

/**
 *
 * @author rombo
 */
public class ReadersAndWritersConditionVariables {

   public final static int BUFFERS_LENGTH = 7;
   //public final static boolean BUFFERS_LENGTH = true; /*remove the comment for random buffers elements*/
   public static final int READERS_NUMBER = 10;
   public static final int READERS_TIME_TO_READ = 150;
   //public static final boolean READERS_TIME_TO_READ = true; /*remove the comment for random readers time to read*/
   public static final int READERS_TIME_TO_SLEEP = 300;
   //public static final boolean READERS_TIME_TO_SLEEP = true; /*remove the comment for random readers time to sleep*/
   public static final int WRITERS_NUMBER = 5;
   public static final int WRITERS_TIME_TO_SLEEP = 200;
   //public static final boolean WRITERS_TIME_TO_SLEEP = true; /*remove the comment for random writers time to read*/
   public static final int WRITERS_LIFE = 8;
   
    
    public static void main(String[] args) {
        Buffer buffer = new Buffer(BUFFERS_LENGTH);
        Writer[] writers = new Writer[WRITERS_NUMBER];
        Reader[] readers = new Reader[READERS_NUMBER];
        
        /*inizialize writers*/
        for(int i = 0; i < writers.length; i++){
            writers[i] = new Writer("Writer_"+i, buffer, WRITERS_LIFE, WRITERS_TIME_TO_SLEEP);
            writers[i].start();
        }
        
        /*inizialize readers*/
        for(int i = 0; i < readers.length; i++){
            readers[i] = new Reader("Reader_"+i, buffer, READERS_TIME_TO_READ, READERS_TIME_TO_SLEEP);
            readers[i].start();
        }
        
        /*monitoring the states of threads*/
        try{
            
            for(int i = 0; i < writers.length; i++){
                writers[i].join();
            }
            
            for(int i = 0; i < readers.length; i++){
                readers[i].interrupt();
                readers[i].join();
            }
            
        }catch(InterruptedException e){
            System.out.println("Error in the main");
            System.exit(1);
        }
        
        System.out.println("00000 THE SIMULATION IS TERMINATED 00000");
        System.out.println("-> Time of execution: " + buffer.getTotalTime()+"ms");
        System.out.println("-> Average time of writers wait to write an element: " + buffer.getWritersAverageTime()+"ms");
        System.out.println("-> Average time of readers wait to read an element: " + buffer.getReadersAverageTime()+"ms");
    }
}
