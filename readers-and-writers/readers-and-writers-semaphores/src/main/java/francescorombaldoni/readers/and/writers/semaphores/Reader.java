package francescorombaldoni.readers.and.writers.semaphores;

import java.util.Random;

/**
 *
 * @author rombo
 * 
 * READER THREAD
 */
class Reader extends Thread{
    /*PRIVATE FIELDS*/
    private Buffer buffer;
    private int indexToRead;
    private Random random;
    private boolean life;
    private int timeToRead;
    private int timeToSleep;
    
    private long time;
    
    /*BUILDER*/
    /**
     * 
     * @param name the name of Reader.
     * @param buffer the shared object.
     * @param timeToRead the time that the Reader spend to read an element.
     * @param timeToSleep time to spend later have been readed an element.
     */
    public Reader(String name, Buffer buffer, int timeToRead, int timeToSleep){
        super(name);
        this.buffer = buffer;
        this.random = new Random();
        this.life = true;
        this.timeToRead = timeToRead;
        this.timeToSleep = timeToSleep;
    }
    
    /**
     * 
     * @param name the name of Reader.
     * @param buffer the shared object.
     * @param timeToRead generate randomly the time that the Reader spend to read an element.
     * @param timeToSleep time to spend later have been readed an element.
     */
    public Reader(String name, Buffer buffer, boolean timeToRead, int timeToSleep){
        super(name);
        
        if(!timeToRead){
            System.out.println("Error the @param timeToRead must be true");
            System.exit(1);
        }
        
        this.buffer = buffer;
        this.random = new Random();
        this.life = true;
        this.timeToRead = this.random.nextInt(51);
        this.timeToSleep = timeToSleep;
    }
    
    /**
     * 
     * @param name the name of Reader.
     * @param buffer the shared object.
     * @param timeToRead the time that the Reader spend to read an element.
     * @param timeToSleep generate randomly the time to spend later have been readed an element.
     */
    public Reader(String name, Buffer buffer, int timeToRead, boolean timeToSleep){
        super(name);
        
        if(!timeToSleep){
            System.out.println("Error the @param timeToSleep must be true");
            System.exit(1);
        }
        
        this.buffer = buffer;
        this.random = new Random();
        this.life = true;
        this.timeToRead = timeToRead;
        this.timeToSleep = this.random.nextInt(501);
    }
    
    /**
     * 
     * @param name the name of Reader.
     * @param buffer the shared object.
     * @param timeToRead generate randomly the time that the Reader spend to read an element.
     * @param timeToSleep generate randomly the time to spend later have been readed an element.
     */
    public Reader(String name, Buffer buffer, boolean timeToRead, boolean timeToSleep){
        super(name);
        
        if(!timeToRead || !timeToSleep){
            System.out.println("Error the @param timeToSleep and @param timeToRead must be true");
            System.exit(1);
        }
        
        this.buffer = buffer;
        this.random = new Random();
        this.life = true;
        this.timeToRead = this.random.nextInt(51);
        this.timeToSleep = this.random.nextInt(501);
    }
    
    /*PUBLIC METHODS*/
    
    /**
     * 
     * @return the time waited before read from buffer.
     */
    public long getTime(){
        return System.currentTimeMillis() - this.time;
    }
    
    /**
     * 
     * @return the index to read from buffer.
     */
    public int getIndexToRead(){
        return this.indexToRead;
    }
    
    /*RUN METHOD*/
    public void run(){
        
        while(this.life){
            
            try{
                this.time = System.currentTimeMillis();
                System.out.println("===> " + this.getName() + " is ready to read");
                this.indexToRead = this.random.nextInt(this.buffer.getBufferLenght());
                System.out.println("==> "+ this.getName() + " is reading: "
                + this.buffer.read(this));
                this.buffer.inserReaderTime(this);
                /*simulate the time to read*/
                Thread.sleep(this.timeToRead);
                System.out.println("=> " + this.getName() + " have finished to read");
                this.buffer.endToRead(this);
                /*suspend the thread*/
                Thread.sleep(this.timeToSleep);

            }catch(InterruptedException e){
                this.life = false;
            }

        }
    }
}
