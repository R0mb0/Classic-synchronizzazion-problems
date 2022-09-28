package francescorombaldoni.producer.consumer.semaphores;

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
    
    /*BUILDER*/
    /**
     * 
     * @param name the name of the consumer
     * @param buffer the shared object
     * @param timeToSleep the time to sleep later the consumation of an element into
     * the buffer
     */
    public Consumer(String name, Buffer buffer, int timeToSleep){
        super(name);
        this.buffer = buffer;
        this.timeToSleep = timeToSleep;
        this.life = true;
        this.time = System.currentTimeMillis();
    }
    
    /*PUBLIC MATHODS*/
    public long getTime(){
        return System.currentTimeMillis() - this.time;
    }
    
    /*RUN METHOD*/
    public void run(){
        /*while the producer is alive*/
        while(life){
            
            try{
                
                System.out.println("=> "+this.getName() + " had consumed: " +this.buffer.consume(this));
                this.buffer.insertConsumerTime(this);
                Thread.sleep(this.timeToSleep);
            }catch(InterruptedException e){
                System.out.println(this.getName()+" is going to be terminated");
                this.life = false;
            }
        }
        
        System.out.println(this.getName() + " is terminated");
    }
}
