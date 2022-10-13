package francescorombaldoni.producer.consumer.condition.variables;

import java.util.Random;

/**
 *
 * @author rombo
 * 
 * Consumer Thread
 */
class Consumer extends Thread{
    /*FIELDS*/
    private Buffer buffer;/*internal shared object*/
    private int timeToSleep;
    private boolean life;
    private long time;
    private boolean isRandomTimeToSleep;
    private Random random;
    
    /*BUILDERS*/
    /**
     * 
     * @param name the name of the consumer.
     * @param buffer the shared object.
     * @param timeToSleep the time to sleep later the consumation of an element into
     * the buffer.
     */
    public Consumer(String name, Buffer buffer, int timeToSleep){
        super(name);
        this.buffer = buffer;
        this.timeToSleep = timeToSleep;
        this.life = true;
        this.isRandomTimeToSleep = false;
    }
    
    /**
     * 
     * @param name the name of the consumer.
     * @param buffer the shared object.
     * @param isRandomTimeToSleep generate randomly the time that the consumer
     * sleep later the consumation of an element into the buffer.
     */
     public Consumer(String name, Buffer buffer, boolean isRandomTimeToSleep){
        super(name);
        this.buffer = buffer;
        this.timeToSleep = timeToSleep;
        this.life = true;
        if(!isRandomTimeToSleep){
            System.out.println("Error the @param isRandomTimeToSleep must be true");
            System.exit(1);
        }
        this.isRandomTimeToSleep = isRandomTimeToSleep;
        this.random = new Random();
    }
    
    
    
    /*PUBLIC METHODS*/
    /**
     * 
     * @return the waiting time before got served.
     */
    public long getTime(){
        return System.currentTimeMillis() - this.time;
    }
    
    /*RUN METHOD*/
    public void run(){
        /*while the producer is alive*/
        while(life){
            
            /*initialize the time variable*/
            this.time = System.currentTimeMillis();
            
            try{
                System.out.println("=> "+ this.getName() + " had consumed: " +this.buffer.consume(this));
                this.buffer.insertConsumerTime(this);
                if(this.isRandomTimeToSleep){
                    Thread.sleep(this.random.nextInt(501));
                }else{
                    Thread.sleep(this.timeToSleep);
                }
            }catch(InterruptedException e){
                System.out.println(this.getName()+" is going to be terminated");
                this.life = false;
            }
        }
        
        System.out.println(this.getName() + " is terminated");
    }
}
