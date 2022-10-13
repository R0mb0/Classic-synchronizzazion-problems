package francescorombaldoni.sleeping.barber.condition.variables;

import java.util.Random;

/**
 *
 * @author rombo
 */
class Barber extends Thread{
    
    /*PRIVATE FIELDS*/
    private Shop shop;
    private Random random;
    private int timeToCutHair;
    private int customersToServe;
    private long time;
    /*BUILDERS*/
    
    /**
     * 
     * @param name name of the thread.
     * @param shop the shop where to serve customers.
     * @param customersToServe number of customers to serve before close the shop.
     * @param timeToCutHair time to cut customers hair.
     */
    public Barber(String name, Shop shop, int customersToServe, int timeToCutHair){
        super(name);
        this.shop = shop;
        this.customersToServe = customersToServe;
        this.timeToCutHair = timeToCutHair;
       
    }
    
    /**
     * 
     * @param name name of the thread.
     * @param shop the shop where to serve customers.
     * @param customersToServe generate randomly the number of customers to serve before close the shop.
     * @param timeToCutHair time to cut customers hair.
     */
    public Barber(String name, Shop shop, boolean customersToServe, int timeToCutHair){
        super(name);
        
        if(!customersToServe){
            System.out.println("Error the @param customersToServe must be true");
            System.exit(1);
        }
        
        this.shop = shop;
        this.random = new Random();
        this.customersToServe = this.random.nextInt(301);
        this.timeToCutHair = timeToCutHair;
    }
    
    /**
     * 
     * @param name name of the thread.
     * @param shop the shop where to serve customers.
     * @param customersToServe the number of customers to serve before close the shop.
     * @param timeToCutHair generate randomly the time to cut customers hair.
     */
    public Barber(String name, Shop shop, int customersToServe, boolean timeToCutHair){
        super(name);
        
        if(!timeToCutHair){
            System.out.println("Error the @param timeToCutHair must be true");
            System.exit(1);
        }
        
        this.shop = shop;
        this.random = new Random();
        this.customersToServe = customersToServe;
        this.timeToCutHair = this.random.nextInt(201);
        
    }
    
    /**
     * 
     * @param name name of the thread.
     * @param shop the shop where to serve customers.
     * @param customersToServe generate randomly the number of customers to serve before close the shop.
     * @param timeToCutHair generate randomly the time to cut customers hair.
     */
    public Barber(String name, Shop shop, boolean customersToServe, boolean timeToCutHair){
        super(name);
        
        if(!customersToServe || !timeToCutHair){
            System.out.println("Error the @param customersToServe and @param timeToCutHair must be true");
            System.exit(1);
        }
        this.shop = shop;
        this.random = new Random();
        this.customersToServe = this.random.nextInt(301);
        this.timeToCutHair = this.random.nextInt(201);
    }

    /*PUBLIC METHODS*/
    /**
     * 
     * @return the time spend to sleep before serve a customer
     */
    public Long getTime() {
        return System.currentTimeMillis() - this.time;
    }
    
    /*RUN METHOD*/
    public void run(){
        /*while the thread must serve customers*/
        while(this.customersToServe > 0){
            
            this.time = System.currentTimeMillis();
            this.shop.serveCustomer(this);
            this.shop.insertBarberTime(this);
            try{
                Thread.sleep(this.timeToCutHair);
            }catch(InterruptedException e){
                System.out.println("Error of: " + this.getName() + " in run method");
                System.exit(1);
            }
            this.customersToServe--;
        }
    }
    
}
