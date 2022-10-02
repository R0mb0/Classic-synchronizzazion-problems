package francescorombaldoni.dining.philosophers.semaphores;

import java.util.Random;

/**
 *
 * @author rombo
 * 
 * Philosopher Thread
 */
class Philosopher extends Thread{
    /*PRIVATE FIELDS*/
    private Table table;
    private int life;
    private int timeToThinking;
    private boolean isRandomTimeToThinking;
    private Random random;
    
    /*PRIVATE FIELDS FOR DEBUG PURPOSE*/
    private long time;
    
    /*BUILDERS*/
    /**
     * 
     * @param name the name of the Philosopher.
     * @param table the shared object.
     * @param life the iterations number.
     * @param timeToSleep the time that the Philosopher think after have eat.
     */
    public Philosopher(String name, Table table, int life, int timeToThinking){
        super(name);
        this.table = table;
        this.life = life;
        this.timeToThinking = timeToThinking;
        this.isRandomTimeToThinking = false;
    }
    
    /**
     * 
     * @param name the name of the Philosopher.
     * @param table the shared object.
     * @param life the iterations number.
     * @param isRandomTimeToSleep generate randomly the time that the Philosopher think after have eat.
     */
    public Philosopher(String name, Table table, int life, boolean isRandomTimeToThinking){
        super(name);
        this.table = table;
        this.life = life;
        if(!isRandomTimeToThinking){
            System.out.println("Error the @param isRandomTimeToThinking must be true");
            System.exit(1);
        }
        this.isRandomTimeToThinking = isRandomTimeToThinking;
        this.random = new Random();
    }
    
    /*PUBLIC METHODS*/
    /**
     * 
     * @return the philosopher waiting time before to eat.
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
            
            System.out.println("===> " + this.getName() + " is hungry");
            
            try{
                
                int temp1 = this.table.eat(this);
                this.table.insertTime(this);
                Thread.sleep(temp1);
                this.table.releaseChopsticks(this);
                if(this.isRandomTimeToThinking){
                    int temp2 = this.random.nextInt(1001);
                    System.out.println("=> " + this.getName() + " is going to sleep for: "
                    + temp2 +"ms");
                    Thread.sleep(temp2);
                }else{
                    System.out.println("=> " + this.getName() + " is going to sleep for: "
                    + this.timeToThinking +"ms");
                    Thread.sleep(this.timeToThinking);
                }
                
            }catch(InterruptedException e){
                System.out.println("Error during the thinking of: " + this.getName());
                System.exit(1);
            }
            
            this.life--;
        }
    }
}
