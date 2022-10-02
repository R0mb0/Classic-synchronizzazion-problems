package francescorombaldoni.dining.philosophers.condition.variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author rombo
 * 
 * SHARED OBJECT
 */
class Table {
    /*PRIVATE FIELDS*/
    /*the shared object must know all the philosophers*/
    private HashMap<Philosopher, Integer> list;
    private int philosopherCounter;
    
    /*list of available chopstick*/
    private List<Boolean> chopsticks;/*False = the chopstick is available*/
    
    /*mutex to protect the memory writing*/
    private ReentrantLock mutex;
    /*condiction to suspend the Threads*/
    private Condition condition;
    
    /*the time that the Philosopher spend to eat*/
    private int timeToEat;
    /*generate randomly the time that the Philosopher spend to eat*/
    private boolean isRandomTimeToEat;
    private Random random;
    
    /*fields for debug purpose*/
    private long time;
    private List<Long> averageTimeToEat;
    
    /*BUILDERS*/
    /**
     * 
     * @param timeToSleep the time that the Philosopher spend to eat.
     */
    public Table(int timeToEat){
        this.list = new HashMap<Philosopher, Integer>();
        this.philosopherCounter = 0;
        this.chopsticks = new ArrayList<Boolean>();
        this.mutex = new ReentrantLock();
        this.condition = this.mutex.newCondition();
        this.timeToEat = timeToEat;
        this.isRandomTimeToEat = false;
        this.time = System.currentTimeMillis();
        this.averageTimeToEat = new ArrayList<Long>();
    }
    
    /**
     * 
     * @param isRandomTimeToSleep generate randomly the time that the Philosopher spend to eat.
     */
    public Table(boolean isRandomTimeToEat){
        this.list = new HashMap<Philosopher, Integer>();
        this.philosopherCounter = 0;
        this.chopsticks = new ArrayList<Boolean>();
        this.mutex = new ReentrantLock();
        this.condition = this.mutex.newCondition();
        if(!isRandomTimeToEat){
            System.out.println("Error the @param isRandomTimeToSleep must be true");
            System.exit(1);
        }
        this.isRandomTimeToEat = isRandomTimeToEat;
        this.random = new Random();
        this.time = System.currentTimeMillis();
        this.averageTimeToEat = new ArrayList<Long>();
    }
    
    /*PRIVATE METHODS*/
    /**
     * 
     * @return the time that the philosopher must spend to eat.
     */
    private int getTimeToSleep(){
        if(this.isRandomTimeToEat){
            return this.random.nextInt(501);
        }else{
            return this.timeToEat;
        }
    }
    
    /*PUBLIC METHODS*/
    /*method to obtain a philosopher subscribed to the table*/
    /**
     * 
     * @param name the name of the Philosopher.
     * @param life the iterations number.
     * @param timeToSleep the time that the Philosopher think after have eat.
     * @return a Philosopher subscribed to table.
     */
    public Philosopher getPhilosopher(String name, int life, int timeToSleep){
        Philosopher temp = new Philosopher(name, this, life, timeToSleep);
        this.list.put(temp, this.philosopherCounter);
        this.philosopherCounter++;
        return temp;
    }
    
    /*method to obtain a philosopher subscribed to the table*/
    /**
     * 
     * @param name the name of the Philosopher.
     * @param life the iterations number.
     * @param isRandomTimeToSleep generate randomly the time that the Philosopher think after have eat.
     * @return a Philosopher subscribed to table.
     */
    public Philosopher getPhilosopher(String name, int life, boolean isRandomTimeToSleep){
        Philosopher temp = new Philosopher(name, this, life, isRandomTimeToSleep);
        this.list.put(temp, this.philosopherCounter);
        this.philosopherCounter++;
        return temp;
    }
    
    /**
     * 
     * @param philosopher the philosopher that want register his waiting time to eat.
     */
    public void insertTime(Philosopher philosopher){
        try{
            /*start critical section*/
            this.mutex.lock();
            this.averageTimeToEat.add(philosopher.getTime());
        }finally{
            this.mutex.unlock();
            /*end critical section*/
        }
    }
    
    /**
     * 
     * @return the average time that the philosophers has waited before to eat.
     */
    public long getAverageTimeToEat(){
        int temp = 0;
        for(long t : this.averageTimeToEat){
            temp += t; 
        }
        return temp/this.averageTimeToEat.size();
    }
    
    /**
     * 
     * @return the duration of this simulation
     */
    public long getTotalTime(){
        return System.currentTimeMillis() - this.time;
    }

    /*PUBLIC SYNCHRONIZATION METHODS*/
    
    /**
     * 
     * @param philosopher the philosopher that wants to eat.
     * @return the time that the philosopher must spend to eat.
     */
    public int eat(Philosopher philosopher) {
        /*start condiction*/
        if(this.chopsticks.size() == 0){
            for(int i = 0; i < this.list.size(); i++){
                this.chopsticks.add(false);
            }
        }
        
        /*verify if this philosopher is the last one added*/
        if(this.list.get(philosopher) == this.list.size() -1){
            /*latest philosopher*/
            try{
                /*start critical section*/
                this.mutex.lock();
                
                /*try to catch the first chopstick*/
                while(this.chopsticks.get(0)){
                    try{
                        this.condition.await();
                    }catch(InterruptedException e){
                        System.out.println("Error of: " + philosopher.getName()+ 
                                " in eat() when he tried to catch the first chopstick");
                        System.exit(1);
                    }
                }
                /*catch the first chopstic*/
                this.chopsticks.set(0, true);
                System.out.println("---> " + philosopher.getName() + " has caught the first chopstick");
                
                /*try to catch the last chopstick*/
                while(this.chopsticks.get(this.chopsticks.size() -1)){
                    try{
                        this.condition.await();
                    }catch(InterruptedException e){
                        System.out.println("Error of: " + philosopher.getName()+ 
                                " in eat() when he tried to catch the last chopstick");
                        System.exit(1);
                    }
                }
                
                /*catch the last chopstic*/
                this.chopsticks.set(this.chopsticks.size()-1, true);
                System.out.println("--> " + philosopher.getName() + " has caught the last chopstick");
            }finally{
                this.mutex.unlock();
                /*end critical section*/
            }
            
        }else{
            /*normal philosopher*/
            try{
                /*start critical section*/
                this.mutex.lock();
                
                /*try to catch the first chopstic*/
                while(this.chopsticks.get(this.list.get(philosopher))){
                    try{
                        this.condition.await();
                    }catch(InterruptedException e){
                        System.out.println("Error of: " + philosopher.getName()+ 
                                " in eat() when he tried to catch the first chopstick");
                        System.exit(1);
                    }
                }
                
                /*catch the first chopstic*/
                this.chopsticks.set(this.list.get(philosopher), true);
                System.out.println("---> " + philosopher.getName() + " has caught the chopstick: " + this.list.get(philosopher));
                
                /*try to catch the second chopstic*/
                while(this.chopsticks.get(this.list.get(philosopher)+1)){
                    try{
                        this.condition.await();
                    }catch(InterruptedException e){
                        System.out.println("Error of: " + philosopher.getName()+ 
                                " in eat() when he tried to catch the second chopstick");
                        System.exit(1);
                    }
                }
                
                 /*catch the last chopstic*/
                this.chopsticks.set(this.list.get(philosopher)+1, true);
                System.out.println("--> " + philosopher.getName() + " has caught the chopstick: " + (this.list.get(philosopher)+1));
            }finally{
                this.mutex.unlock();
                /*end critical section*/
            }
        }
        int temp = this.getTimeToSleep();
        System.out.println("==> " + philosopher.getName() + " is going to eat for: " + temp);
        return temp;
    }
    
    
    /**
     * 
     * @param philosopher the philosopher that has finished to eat
     */
    public void releaseChopsticks(Philosopher philosopher){
         if(this.list.get(philosopher) == this.list.size() -1){
             try{
                 /*start critical section*/
                 this.mutex.lock();
                 
                 /*release all the the caught chopsticks*/
                 this.chopsticks.set(0, false);
                 this.chopsticks.set(this.chopsticks.size()-1, false);
                 
                 /*signal tath the memory has been changed*/
                 System.out.println("-> " + philosopher.getName() + " has released the first and the last chopsticks");
                 this.condition.signalAll();
             }finally{
                 this.mutex.unlock();
                 /*end critical section*/
             }
         }else{
             try{
                 /*start critical section*/
                 this.mutex.lock();
                 
                 /*release all the the caught chopsticks*/
                 this.chopsticks.set(this.list.get(philosopher), false);
                 this.chopsticks.set(this.list.get(philosopher)+1, false);
                 
                 /*signal tath the memory has been changed*/
                 System.out.println("-> " + philosopher.getName() + " has released the first and the second chopsticks");
                 this.condition.signalAll();
             }finally{
                 this.mutex.unlock();
                 /*end critical section*/
             }
         }
    }
}
