package francescorombaldoni.dining.philosophers.condition.variables;

/**
 *
 * @author rombo
 */
public class DiningPhilosophersConditionVariables {

    public static final int TIME_TO_EAT = 200;
    //public static final boolean TIME_TO_EAT = true; /*remove the comment for a random time to eat*/
    public static final int PHILOSOPHERS_NUMBER = 5;
    public static final int PHILOSOPHERS_LIFE = 2; 
    public static final int TIME_TO_THINKING = 400;
    //public static final boolean TIME_TO_THINKING = true; /*remove the comment for a random time to thinking*/
    
    public static void main(String[] args) {
        Table table = new Table(TIME_TO_EAT);
        Philosopher[] philosophers = new Philosopher[PHILOSOPHERS_NUMBER];
        
        /*initialize the philosophers*/
        for(int i = 0; i < philosophers.length; i++){
            philosophers[i] = table.getPhilosopher("Philosopher_"+i, PHILOSOPHERS_LIFE, TIME_TO_THINKING);
            philosophers[i].start();
        }
        
        /*monitoring the threads states*/
        try{
            for(int i = 0; i < philosophers.length; i++){
                philosophers[i].join();
            }
        }catch(InterruptedException e){
            System.out.println("Error in the main");
            System.exit(1);
        }
        
        System.out.println("00000 THE SIMULATION IS TERMINATED 00000");
        System.out.println("-> Time of execution: " + table.getTotalTime()+"ms");
        System.out.println("-> Average time of philosophers wait to eat: " + table.getAverageTimeToEat()+"ms");
    }
}
