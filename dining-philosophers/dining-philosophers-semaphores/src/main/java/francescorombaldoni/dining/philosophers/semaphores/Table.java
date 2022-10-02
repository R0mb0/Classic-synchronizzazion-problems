/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package francescorombaldoni.dining.philosophers.semaphores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author rombo
 */
class Table {
    /*PRIVATE FIELDS*/
    /*the shared object must know all the philosophers*/
    private HashMap<Philosopher, Integer> list;
    private int philosopherCounter;
    
    /*list of available chopstick*/
    private List<Semaphore> chopsticks;
    private boolean isFifoSemaphores;
    
    /*mutex to protect the memory writing*/
    private ReentrantLock mutex;
    
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
    public Table(int timeToEat,boolean isFifoSemaphores){
        this.list = new HashMap<Philosopher, Integer>();
        this.philosopherCounter = 0;
        this.chopsticks = new ArrayList<Semaphore>();
        this.isFifoSemaphores = isFifoSemaphores;
        this.mutex = new ReentrantLock();
        this.timeToEat = timeToEat;
        this.isRandomTimeToEat = false;
        this.time = System.currentTimeMillis();
        this.averageTimeToEat = new ArrayList<Long>();
    }
    
    /**
     * 
     * @param isRandomTimeToSleep generate randomly the time that the Philosopher spend to eat.
     */
    public Table(boolean isRandomTimeToEat, boolean isFifoSemaphores){
        this.list = new HashMap<Philosopher, Integer>();
        this.philosopherCounter = 0;
        this.chopsticks = new ArrayList<Semaphore>();
        this.isFifoSemaphores = isFifoSemaphores;
        this.mutex = new ReentrantLock();
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
                this.chopsticks.add(new Semaphore(1, this.isFifoSemaphores));
            }
        }
        
        /*verify if this philosopher is the last one added*/
        if(this.list.get(philosopher) == this.list.size() -1){
            /*latest philosopher*/
            
            try{
                /*catch the first chopstic*/
                this.chopsticks.get(0).acquire();
                System.out.println("---> " + philosopher.getName() + " has caught the first chopstick");
            }catch(InterruptedException e){
                System.out.println("Error of: " + philosopher.getName()+ 
                        " in eat() when he tried to catch the first chopstick");
                System.exit(1);
            }
            
            try{
                /*catch the last chopstic*/
                this.chopsticks.get(this.chopsticks.size()-1).acquire();
                System.out.println("--> " + philosopher.getName() + " has caught the last chopstick");
            }catch(InterruptedException e){
                System.out.println("Error of: " + philosopher.getName()+ 
                        " in eat() when he tried to catch the last chopstick");
                System.exit(1);
            }
            
            
        }else{
            /*normal philosopher*/
            
            try{
                /*catch the first chopstic*/
                this.chopsticks.get(this.list.get(philosopher)).acquire();
                System.out.println("---> " + philosopher.getName() + " has caught the chopstick: " + this.list.get(philosopher));
            }catch(InterruptedException e){
                System.out.println("Error of: " + philosopher.getName()+ 
                        " in eat() when he tried to catch the first chopstick");
                System.exit(1);
            }
            
            try{
                /*catch the second chopstic*/
                this.chopsticks.get(this.list.get(philosopher)+1).acquire();
                System.out.println("--> " + philosopher.getName() + " has caught the chopstick: " + (this.list.get(philosopher)+1));
            }catch(InterruptedException e){
                System.out.println("Error of: " + philosopher.getName()+ 
                        " in eat() when he tried to catch the second chopstick");
                System.exit(1);
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
             
             /*release all the the caught chopsticks*/
             this.chopsticks.get(0).release();
             this.chopsticks.get(this.chopsticks.size()-1).release();
             /*signal tath the memory has been changed*/
             System.out.println("-> " + philosopher.getName() + " has released the first and the last chopsticks");
            
         }else{
             /*release all the the caught chopsticks*/
             this.chopsticks.get(this.list.get(philosopher)).release();
             this.chopsticks.get(this.list.get(philosopher)+1).release();
                 
             /*signal tath the memory has been changed*/
             System.out.println("-> " + philosopher.getName() + " has released the first and the second chopsticks");
         }
    }
}
