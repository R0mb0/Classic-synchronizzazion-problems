package francescorombaldoni.producer.consumer.semaphores;

/**
 *
 * @author rombo
 */
public class ProducerConsumerSemaphores {
    public static final int BUFFERS_LENGTH = 10;
    //public static final boolean BUFFERS_LENGTH = true; /*remove the comment for unlimited buffer*/
    public static final boolean FIFO_BUFFER = true;
    public static final int CONSUMERS_NUMBER = 5;
    public static final int CONSUMERS_TIME_TO_SLEEP = 100;
    //public static final boolean CONSUMERS_TIME_TO_SLEEP = true; /*remove the comment for random consumers time to sleep*/
    public static final int PRODUCERS_NUMBER = 100;
    public static final int PRODUCERS_TIME_TO_SLEEP = 300;
    //public static final boolean PRODUCERS_TIME_TO_SLEEP = true; /*remove the comment for random producers time to sleep*/
    public static final int PRODUCERS_LIFE = 2;
    
    /*MAIN*/
    public static void main(String[] args) {
        
        Buffer buffer = new Buffer(BUFFERS_LENGTH, FIFO_BUFFER);
        Consumer[] consumers = new Consumer[CONSUMERS_NUMBER];
        Producer[] producers = new Producer[PRODUCERS_NUMBER];
        
        /*inizialize consumers*/
        for(int i = 0; i < consumers.length; i++){
            consumers[i] = new Consumer("Consumer_"+i, buffer, CONSUMERS_TIME_TO_SLEEP);
            consumers[i].start();
        }
        
        /*inizialize producers*/
        for(int i = 0; i < producers.length; i++){
            producers[i] = new Producer("Producer_"+i, buffer, PRODUCERS_LIFE, PRODUCERS_TIME_TO_SLEEP);
            producers[i].start();
        }
        
        /*monitoring the states of threads*/
        try{
            
            for(int i = 0; i < producers.length; i++){
                producers[i].join();
            }
            
            for(int i = 0; i < consumers.length; i++){
                consumers[i].interrupt();
                consumers[i].join();
            }
            
        }catch(InterruptedException e){
            System.out.println("Error in the main");
            System.exit(1);
        }
        
        System.out.println("00000 THE SIMULATION IS TERMINATED 00000");
        System.out.println("-> Time of execution: " + buffer.getTotalTime()+"ms");
        System.out.println("-> Average time of producers wait to produce an element: " + buffer.getProducerAverageTime()+"ms");
        System.out.println("-> Average time of consumers wait to consume an element: " + buffer.getConsumerAverageTime()+"ms");
    }
}
