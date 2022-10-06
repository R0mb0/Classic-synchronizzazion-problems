package francescorombaldoni.readers.and.writers.condition.variables;

import java.util.Random;

/**
 *
 * @author rombo
 */
class Writer extends Thread{
    
    /*FIELDS*/
    private Buffer buffer;
    private Random random;
    private int object;
    private int life;
    private int timeToSleep;
    
    private long time;
    
    /*BUILDERS*/
    /**
     * 
     * @param name the name of writer.
     * @param buffer the shared object.
     * @param life the number of objects that the writer will produce.
     * @param timeToSleep the time to spend later have been wrote an element.
     */
    public Writer(String name, Buffer buffer, int life, int timeToSleep){
        super(name);
        this.buffer = buffer;
        this.random = new Random();
        this.life = life;
        this.timeToSleep = timeToSleep;
    }
    
    /**
     * 
     * @param name the name of writer.
     * @param buffer the shared object.
     * @param life the number of objects that the writer will produce.
     * @param timeToSleep generate randomly the time to spend later have been wrote an element.
     */
    public Writer(String name, Buffer buffer, int life, boolean timeToSleep){
        super(name);
        
        if(!timeToSleep){
            System.out.println("Error the @param timeToSleep must be true");
            System.exit(1);
        }
        
        this.buffer = buffer;
        this.random = new Random();
        this.life = life;
        this.timeToSleep = this.random.nextInt(301);
    }
    
    /*PUBLIC METHODS*/
    /**
     * 
     * @return the objecy created.
     */
    public int getObject(){
        return this.object;
    }
    
    /**
     * 
     * @return @return the time waited before to write in the buffer.
     */
    public long getTime(){
        return System.currentTimeMillis() - this.time;
    }
    
    /*RUN METHOD*/
    public void run(){
        while(this.life > 0){
            
            this.time = System.currentTimeMillis();
            this.object = this.random.nextInt(1001);
            while(!this.buffer.isNewElement(this.object)){
                this.object = this.random.nextInt(1001);
            }
            System.out.println("__> " + this.getName() + " is ready to write: " + this.object);
            this.buffer.write(this);
            this.buffer.insertWriterTime(this);
            System.out.println("_> " + this.getName() + " had wrote to buffer");
            try{
                Thread.sleep(this.timeToSleep);
            }catch(InterruptedException e){
                System.out.println("Error of producer: " + this.getName());
            }
            this.life--;
        }
    }
    
}
