package francescorombaldoni.sleeping.barber.condition.variables;

/**
 *
 * @author rombo
 */
public class SleepingBarberConditionVariables {
    
    public static final int SHOPS_CHAIRS = 5;
    //public static final boolean SHOPS_CHAIRS = true; /*remove the comment for random shop's chairs*/
    public static final int CUSTOMERS_NUMBER = 20;
    public static final int TIME_BEFORE_HAIR_CUT = 250;
    //public static final boolean TIME_BEFORE_HAIR_CUT = true; /*remove the comment for random time before the next hair cut*/
    public static final int CUSTOMERS_TO_SERVE_BEFORE_CLOSING = 30;
    //public static final boolean CUSTOMERS_TO_SERVE_BEFORE_CLOSING = true; /*remnove the comment for ranndom customers to serve before closing*/
    public static final int HAIR_CUT_TIME = 125;
    //public static final boolean HAIR_CUT_TIME = true; /*remove the comment for random hair cut time*/
    
    
    public static void main(String[] args) {
        Shop shop = new Shop(SHOPS_CHAIRS);
        Customer[] customers = new Customer[CUSTOMERS_NUMBER];
        
        /*initialize barber*/
        Barber barber = new Barber("Barber", shop, CUSTOMERS_TO_SERVE_BEFORE_CLOSING, HAIR_CUT_TIME);
        barber.start();
        
        /*initialize customers*/
        for(int i = 0; i < customers.length; i++){
            customers[i] = new Customer("Customer_"+i, shop, TIME_BEFORE_HAIR_CUT);
            customers[i].start();
        } 
        
        /*monitoring threads states*/
        try{
            barber.join();
            for(int i = 0; i < customers.length; i++){
                customers[i].interrupt();
                customers[i].join();
            } 
        }catch(InterruptedException e){
            System.out.println("Error in the main");
            System.exit(1);
        }   
        
        System.out.println("00000 THE SIMULATION IS TERMINATED 00000");
        System.out.println("-> Time of execution: " + shop.getTotalTime()+"ms");
        shop.printServedCustomersTime();
        System.out.println("-> Average time of barber's wait before serving a customer: " + shop.getBarberAverageTime()+"ms");
    }
}
