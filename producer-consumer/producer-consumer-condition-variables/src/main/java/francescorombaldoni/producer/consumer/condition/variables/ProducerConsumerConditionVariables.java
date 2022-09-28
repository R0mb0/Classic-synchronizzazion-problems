package francescorombaldoni.producer.consumer.condition.variables;

/**
 *
 * @author rombo
 */
public class ProducerConsumerConditionVariables {
    public static final int BUFFERS_ELEMENTS = 10;
    //public static final boolean BUFFERS_ELEMENTS = true; /*remove the comment for unlimited buffer*/
    public static final int CONSUMERS_NUMBER = 5;
    public static final int CONSUMERS_TIME_TO_SLEEP = 100;
    public static final int PRODUCERS_NUMBER = 100;
    public static final int PRODUCERS_TIME_TO_SLEEP = 300;
    public static final int PRODUCERS_LIFE = 2;
    
    /*MAIN*/
    public static void main(String[] args) {
        Buffer buffer = new Buffer(BUFFERS_ELEMENTS);
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
        System.out.println("-> Time of execution: " + buffer.getTotalTime());
        System.out.println("-> Average time of producers wait to produce an element: " + buffer.getProducerAverageTime());
        System.out.println("-> Average time of cpnsumers wait to consume an element: " + buffer.getConsumerAverageTime());
    }
}