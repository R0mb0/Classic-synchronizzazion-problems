package francescorombaldoni.producer.consumer.condition.variables;

import java.util.Random;

/**
 *
 * @author rombo
 * 
 * Producer Thread
 */
class Producer extends Thread{
    /*PRIVATE FIELDS*/
    private Buffer buffer;/*internal shared object*/
    private int life;
    private int timeToSleep;
    private Random random;
    private int element; /*the element to insert into the buffer*/
    private long time;
    private boolean isRandomTimeToSleep;
    
    /*BUILDERS*/
    /**
     * 
     * @param name the name of the producer.
     * @param buffer the shared object.
     * @param life the number of iterations of producer.
     * @param timeToSleep the time to sleep later the production of an element.
     */
    public Producer(String name, Buffer buffer,int life, int timeToSleep){
        super(name);
        this.buffer = buffer;
        this.life = life;
        this.timeToSleep = timeToSleep;
        this.random = new Random();
        this.isRandomTimeToSleep = false;        
    }
    
    /**
     * 
     * @param name the name of the producer
     * @param buffer the shared object.
     * @param life the number of iterations of producer.
     * @param isRandomTimeToSleep generate randomly the time that the producer
     * sleep later the consumation of an element into the buffer.
     */
    public Producer(String name, Buffer buffer,int life, boolean isRandomTimeToSleep){
        super(name);
        this.buffer = buffer;
        this.life = life;
        this.timeToSleep = timeToSleep;
        this.random = new Random();
        if(!isRandomTimeToSleep){
            System.out.println("Error the @param isRandomTimeToSleep must be true");
            System.exit(1);
        }
        this.isRandomTimeToSleep = isRandomTimeToSleep;        
    }
    
    /*PUBLIC METHODS*/
    /**
     * 
     * @return the element produced by the producer.
     */
    public int getElement(){
        return this.element;
    }
    
    /**
     * 
     * @return the waiting time before got served.
     */
    public long getTime(){
        return System.currentTimeMillis() - this.time;
    }
    
    /*RUN METHOD*/
    public void run(){
        /*while the thred has life*/
        while(this.life > 0){
            
            /*initialize the time variable*/
            this.time = System.currentTimeMillis();
            
            try{
                this.element = this.random.nextInt(200);
                this.buffer.produce(this);
                this.buffer.insertProducerTime(this);
                if(this.isRandomTimeToSleep){
                    Thread.sleep(this.random.nextInt(501));
                }else{
                    Thread.sleep(this.timeToSleep);
                }
            }catch(InterruptedException e){
                System.out.println("Error of producer: " + this.getName());
                System.exit(1);
            }
            
            this.life--;
        }
    }
    
}
