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
    
    /*BUILDER*/
    /**
     * 
     * @param name the name of the producer
     * @param buffer the shared object
     * @param life the number of iterations of producer
     * @param timeToSleep the time to sleep later the production of an element
     */
    public Producer(String name, Buffer buffer,int life, int timeToSleep){
        super(name);
        this.buffer = buffer;
        this.life = life;
        this.timeToSleep = timeToSleep;
        this.random = new Random();
        this.time = System.currentTimeMillis();
        
    }
    
    /*PUBLIC METHODS*/
    public int getElement(){
        return this.element;
    }
    
    public long getTime(){
        return System.currentTimeMillis() - this.time;
    }
    
    /*RUN METHOD*/
    public void run(){
        /*while the thred has life*/
        while(this.life > 0){
            
            try{
                this.element = this.random.nextInt(200);
                this.buffer.produce(this);
                this.buffer.insertProducerTime(this);
                Thread.sleep(this.timeToSleep);
            }catch(InterruptedException e){
                System.out.println("Error of producer: " + this.getName());
                System.exit(1);
            }
            
            this.life--;
        }
    }
    
}
