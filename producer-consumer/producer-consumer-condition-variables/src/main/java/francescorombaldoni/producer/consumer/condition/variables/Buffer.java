package francescorombaldoni.producer.consumer.condition.variables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author rombo
 * 
 * SHARED OBJECT
 */
class Buffer {
    /*PRIVATE FIELDS*/
    
    /*buffer state*/
    private boolean isLimited;
    
    /*in case of limited buffer*/
    private int[] limitedBuffer;
    private int bufferElements;
    private int insertIndex;
    private int readIndex;
    
    /*in case of unlimited buffer*/
    private List<Integer> unlimitedBuffer;
    
    /*mutex to modify the shared object*/
    private ReentrantLock mutex;
    
    /*condition variables to control the acess to the shared memory*/
    private Condition bufferEmpty;
    private Condition bufferFull;
    
    /*PRIVATE FIELDS FOR DEBUG PURPOSE*/
    private long time;
    private List<Long> producersTimes;
    private List<Long> consumersTimes;
    
    
    /*BUILDERS*/
    /*limited buffer case*/
    /**
     * 
     * @param bufferLength set the number of cells of buffer  
     */
    public Buffer(int bufferLength){
        if(bufferLength <= 0){
            System.out.println("Error -> The buffer couldn't have negative length"
                    + " or 0 length");
            System.exit(1);
        }
        
        this.isLimited = true;
        this.limitedBuffer = new int[bufferLength];
        this.bufferElements = 0;
        this.insertIndex = 0;
        this.readIndex = 0;
        this.mutex = new ReentrantLock();
        this.bufferEmpty = this.mutex.newCondition();
        this.bufferFull = this.mutex.newCondition();
        this.time = System.currentTimeMillis();
        this.producersTimes = new ArrayList<Long>();
        this.consumersTimes = new ArrayList<Long>();
    }
    
    /*unlimited buffer case*/
    /**
     * 
     * @param isUnlimited true if the buffer is unlimited, else insert a positive
     * integer number
     */
    public Buffer(boolean isUnlimited){
        if(!isUnlimited){
            System.out.println("Error -> If the buffer is limited, insert the "
                    + "length of the buffer");
            System.exit(1);
        }
        
        this.isLimited = false;
        this.unlimitedBuffer = new ArrayList<Integer>();
        this.mutex = new ReentrantLock();
        this.bufferEmpty = this.mutex.newCondition();
        this.time = System.currentTimeMillis();
        this.producersTimes = new ArrayList<Long>();
        this.consumersTimes = new ArrayList<Long>();
    }
    
    /*PRIVATE METHODS*/
    
    /*insert an element into limited buffer*/
    /**
     * 
     * @param element the integer to insert into buffer
     */
    private void insertToBuffer(int element){
        this.bufferElements++;
        this.limitedBuffer[this.insertIndex] = element;
        this.insertIndex = (this.insertIndex + 1) % this.limitedBuffer.length;
    }
    
    /*read an element from limited buffer*/
    /**
     * 
     * @return the right element from the buffer.
     */
    private int readFromBuffer(){
        this.bufferElements--;
        int temp = this.limitedBuffer[this.readIndex];
        this.readIndex = (this.readIndex + 1) % this.limitedBuffer.length;
        return temp;
    }
    
    /*PUBLIC METHODS*/
    /**
     * 
     * @return the total time of progrma's execution
     */
    public long getTotalTime(){
        return System.currentTimeMillis() - this.time;
    }
    
    /*insert the time spent by the producer to produce*/
    /**
     * 
     * @param producer the producer that ask for insert a number into the buffer 
     */
    public void insertProducerTime(Producer producer){
        try{
            /*start critical section*/
            this.mutex.lock();
            this.producersTimes.add(producer.getTime());
        }finally{
            this.mutex.unlock();
            /*end critical section*/
        }
    }
    
    /**
     * 
     * @return the average time spent by the producers to produce.
     */
    public long getProducerAverageTime(){
        int temp = 0;
        for(long t : this.producersTimes){
            temp += t;
        }
        return temp / this.producersTimes.size();
    }
    
    
    /*insert the time spent by the consumer to consume*/
    /**
     * 
     * @param consumer the consumer that ask for consume an element into the 
     * buffer
     */
    public void insertConsumerTime(Consumer consumer){
        try{
            /*start critical section*/
            this.mutex.lock();
            this.consumersTimes.add(consumer.getTime());
        }finally{
            this.mutex.unlock();
            /*end critical section*/
        }
    }
    
    /**
     * 
     * @return the average time spent by the consumers to consume.
     */
    public long getConsumerAverageTime(){
        int temp = 0;
        for(long t : this.consumersTimes){
            temp += t;
        }
        return temp / this.consumersTimes.size();
    }
    
    /*PUBLIC SYNCHRONIZATION METHODS*/
    /**
     * 
     * @param producer the producer that produce an element to insert into the buffer.
     */
    public void produce(Producer producer){
        System.out.println("--> " + producer.getName() + " wants produce an element");
        if(this.isLimited){
            /*limited buffer*/
            try{
                /*start critical section*/
                this.mutex.lock();
                /*stop the Thread if can't produce an alement*/
                while(this.bufferElements == this.limitedBuffer.length){
                    try{
                        this.bufferFull.await();
                    }catch(InterruptedException e){
                        System.out.println("Error of: " + producer.getName() + " into produce"
                                + " method");
                        System.exit(1);
                    }  
                }
                /*produce an element*/
                this.insertToBuffer(producer.getElement());
                System.out.println("-> " + producer.getName() + " has produced: " + producer.getElement());
                /*notify that the buffer is not empty*/
                this.bufferEmpty.signal();
            }finally{
                this.mutex.unlock();
            }
        }else{
            /*unlimited buffer*/
            try{
                /*start critical section*/
                this.mutex.lock();
                /*produce an element*/
                System.out.println("-> " + producer.getName() + " has produced: " + producer.getElement());
                this.unlimitedBuffer.add(producer.getElement());
                /*notify that the buffer is not empty*/
                this.bufferEmpty.signal();
            }finally{
                this.mutex.unlock();
                /*end critical section*/
            }
        }
    }
    
    /**
     * 
     * @param consumer the consumer that consume an element from the buffer.
     * @return the time to suspend the consumer later its consumation.
     * @throws InterruptedException 
     */
    public int consume(Consumer consumer) throws InterruptedException{
        System.out.println("==> " + consumer.getName() + " is ready to consume an element");
        if(this.isLimited){
            /*limited buffer*/
            try{
                /*start critical section*/
                this.mutex.lock();
                /*stop the Thread if can't consume an alement*/
                while(this.bufferElements == 0){
                    this.bufferEmpty.await();
                }
                /*remove an element*/
                int temp = this.readFromBuffer();
                /*signal that the buffer isn't full*/
                this.bufferFull.signal();
                return temp;
            }finally{
                this.mutex.unlock();
                /*end critical section*/
            }
        }else{
            /*unlimited buffer*/
            try{
                /*start critical section*/
                this.mutex.lock();
                /*stop the Thread if can't consume an alement*/
                while(this.unlimitedBuffer.isEmpty()){
                    this.bufferEmpty.await();
                }
                /*remove an element*/
                int temp = this.unlimitedBuffer.get(0);
                this.unlimitedBuffer.remove(0);
                return temp;
                
            }finally{
                this.mutex.unlock();
                /*end critical section*/
            }
        }
    }
}
