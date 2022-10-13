package francescorombaldoni.sleeping.barber.condition.variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author rombo
 * 
 * SHARED OBJECT
 */
class Shop {
    /*PRIVATE FIELDS*/
   private WaitingRoom waitingRoom;
   private Random random;
   private ReentrantLock mutex;
   private Condition sleepingBarber;
   
   /*fields for debug purpose*/
   private Long time;
   private Map<Customer, Integer> servedCustomers;
   private List<Long> barberAverageTime;
   
   /*BUILDERS*/
   /**
    * 
    * @param totalChairs the number of avaible chairs
    */
   public Shop(int totalChairs){
       this.waitingRoom = new WaitingRoom(totalChairs);
       this.mutex = new ReentrantLock();
       this.sleepingBarber = this.mutex.newCondition();
       
       this.time = System.currentTimeMillis();
       this.servedCustomers = new HashMap<Customer, Integer>();
       this.barberAverageTime = new ArrayList<Long>();
   }
   
   /**
    * 
    * @param totalChairs generate randomly the number of avaible chairs
    */
   public Shop(boolean totalChairs){
       if(!totalChairs){
            System.out.println("Error the @param totalChairs must be true");
            System.exit(1);
        }
       this.random = new Random();
       this.waitingRoom = new WaitingRoom(this.random.nextInt(21));
       this.mutex = new ReentrantLock();
       this.sleepingBarber = this.mutex.newCondition();
       
       this.time = System.currentTimeMillis();
       this.servedCustomers = new HashMap<Customer, Integer>();
       this.barberAverageTime = new ArrayList<Long>();
   }
   
   /*PUBLIC METHODS*/
   /**
    * 
    * @param barber the barber that want insert his waiting time.
    */
   public void insertBarberTime(Barber barber){
       try{
           /*start critical section*/
           this.mutex.lock();
           this.barberAverageTime.add(barber.getTime());
       }finally{
           this.mutex.unlock();
           /*end critical section*/
       }
   }
   
   /**
    * 
    * @return barber average waiting time before to cut hair.
    */
   public long getBarberAverageTime(){
        int temp = 0;
        for(long t : this.barberAverageTime){
            temp += t;
        }
        return temp / this.barberAverageTime.size();
   }
   /**
    * 
    * @return the sumilation total time
    */
   public long getTotalTime(){
       return System.currentTimeMillis() - this.time;
   }
   
   /*print all the customers and the times that they has been served*/
   public void printServedCustomersTime(){
       int temp = 1;
       for(var entry : this.servedCustomers.entrySet()){
           System.out.println(temp+". " + entry.getKey().getName() + " has been served " + entry.getValue()
          + " times");
           temp++;
       }
   }
   
   /*PUBLIC SYNCHRONIZATION METHODS*/
   /**
    * 
    * @param customer the customer that wants cut his hair;
    * @throws Exception 
    */
   public void hairCut(Customer customer) throws Exception{
       try{
           /*start critical section*/
           this.mutex.lock();
           
           /*if the customer is a new customer, so register him*/
           if(!this.servedCustomers.containsKey(customer)){
               this.servedCustomers.put(customer, 0);
           }
           
           System.out.println("--> " + customer.getName() + " is entered into the shop");
           
           if(!this.waitingRoom.isFull()){
               System.out.println("B> " + customer.getName() + " waiting for the hair cut");
               this.waitingRoom.add(customer);
               this.sleepingBarber.signal();
               customer.suspend(this.mutex.newCondition());
           }else{
               this.sleepingBarber.signal();
               System.out.println("X> " + customer.getName() + " is leaving the shop");
           }
       }finally{
           this.mutex.unlock();
           /*end critical section*/
       }
   }
   
   /**
    * 
    * @param barber the barber that serve the customers of shop
    */
   public void serveCustomer(Barber barber){
       try{
           /*start critical section*/
           this.mutex.lock();
           
           System.out.println("___> " + barber.getName() + "is ready to serve a customer");
           
           /*if there isn't any customer suspend the thread*/
           while(this.waitingRoom.isEmpty()){
               System.out.println("__> " + barber.getName() + "is sleeping");
               this.sleepingBarber.await();
           }
           
           Customer temp = this.waitingRoom.getCustomer(this.waitingRoom.getOccupiedChairs() - 1);
           System.out.println("S> " + barber.getName() + " is serving: " + temp.getName());
           temp.restart();
           this.servedCustomers.replace(temp, this.servedCustomers.get(temp) + 1);
           this.waitingRoom.remove(temp);
           
       }catch(InterruptedException e){
           System.out.println("Error of : " + barber.getName() + " in serveCustomer method");
           System.exit(1);
       }finally{
           this.mutex.unlock();
           /*end critical section*/
       }
   }
}
