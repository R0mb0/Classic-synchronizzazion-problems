package francescorombaldoni.sleeping.barber.condition.variables;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rombo
 * 
 * WAITING ROOM THAT CONTAINS THE NUMBER OF AVAIBLE CHAIRS
 */
class WaitingRoom {
    
    /*PRIVATE FIELDS*/
    private List<Customer> chairs;
    private int totalChairs;
    
    /*BUILDER*/
    /**
     * 
     * @param totalChairs the number of avaible chairs (the length of the list).
     */
    public WaitingRoom(int totalChairs){
        this.totalChairs = totalChairs;
        this.chairs = new ArrayList<>();
    }
    
    /*PUBLIC METHODS*/
    /**
     * 
     * @param customer to add in the list.
     * @throws Exception There isn't avaible chairs
     */
    public void add(Customer customer) throws Exception{
        if(this.chairs.size() <= this.totalChairs){
            this.chairs.add(customer);
        }else{
            throw new Exception("There isn't avaible chairs");
        }
    }
    
    /**
     * 
     * @param index the customer index in the List.
     * @return the customer designed.
     */
    public Customer getCustomer(int index){
        return this.chairs.get(index);
    }
    
    /**
     * 
     * @param customer to remove from the list.
     */
    public void remove(Customer customer){
        this.chairs.remove(customer);
    }
    
    /**
     * 
     * @param index of the element to remove from the list.
     */
    public void remove(int index){
        this.chairs.remove(index);
    }
    
    /**
     * 
     * @return if all the chairs are avaible.
     */
    public boolean isEmpty(){
        return this.chairs.isEmpty();
    }
    
    /**
     * 
     * @return if all the chairs aren't avaible. 
     */
    public boolean isFull(){
        return this.chairs.size() >= this.totalChairs;
    }
    
    /**
     * 
     * @return the number of the occupied chairs. 
     */
    public int getOccupiedChairs(){
        return this.chairs.size();
    }
    
    /**
     * 
     * @return the total number of avaible chairs int the waiting room.
     */
    public int getTotalChairs(){
        return this.totalChairs;
    }
}
