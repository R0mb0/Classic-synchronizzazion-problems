package francescorombaldoni.sleeping.barber.semaphores;

import java.util.Random;

/**
 *
 * @author rombo
 * 
 * CUSTOMER THREAD
 */
class Customer extends Thread{
    
    /*private fields*/
    private Shop shop;
    private Random random;
    private int timeToSleep;
    private boolean life;
    
    
    /*BUILDERS*/
    /**
     * 
     * @param name name of thread.
     * @param shop the shop where cut his hair.
     * @param timeToSleep time to sleep later a request.
     */
    public Customer(String name, Shop shop, int timeToSleep){
        super(name);
        this.shop = shop;
        this.timeToSleep = timeToSleep;
        this.life = true;
    }
    
    /**
     * 
     * @param name
     * @param shop
     * @param timeToSleep 
     */
    public Customer(String name, Shop shop, boolean timeToSleep){
        super(name);
        if(!timeToSleep){
            System.out.println("Error the @param totalChairs must be true");
            System.exit(1);
        }
        this.shop = shop;
        this.random = new Random();
        this.timeToSleep = this.random.nextInt(501);
        this.life = true;
    }
    
    /*RUN METHOD*/
    public void run(){
        /*while the tread has life*/
        while(this.life){
            
            try{
                System.out.println("---> " + this.getName() + " is going to the shop");
                this.shop.hairCut(this);
                Thread.sleep(this.timeToSleep);
            }catch(Exception e){
                this.life = false;
                System.out.println("##> " + this.getName() + " is going to terminate");
            }
            
        }
        
        System.out.println("#> " + this.getName() + " is terminated");
    }
}

