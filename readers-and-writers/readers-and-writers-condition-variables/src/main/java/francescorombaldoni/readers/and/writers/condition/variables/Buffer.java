package francescorombaldoni.readers.and.writers.condition.variables;

import java.util.ArrayList;
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
class Buffer {
    /*FIELDS*/
    /*buffers fields*/
    private int[] buffer;
    private int elementsInBuffer;
    private int isertIndex; /*the buffer will be written in circular way*/
    /*buffer length field*/
    private Random random;
    /*thread fields*/
    private ReentrantLock mutex;
    private Condition emptyBuffer;
    private Condition reading;
    private List<Reader> readersReady;
    /*debug purpose fields*/
    private long time;
    private List<Long> readersTimes;
    private List<Long> writersTimes;
    
    /*BUILDERS*/
    /**
     * 
     * @param bufferLength the buffers length.
     */
    public Buffer(int bufferLength){
        if(bufferLength <= 0){
            System.out.println("Error -> The buffer couldn't have negative length"
                    + " or 0 length");
            System.exit(1);
        }
        
        this.buffer = new int[bufferLength];
        this.elementsInBuffer = 0;
        this.isertIndex = 0;
        
        this.mutex = new ReentrantLock();
        this.emptyBuffer = this.mutex.newCondition();
        this.reading = this.mutex.newCondition();
        this.readersReady = new ArrayList<Reader>();
        
        this.time = System.currentTimeMillis();
        this.readersTimes = new ArrayList<Long>();
        this.writersTimes = new ArrayList<Long>();
    }
    
    /**
     * 
     * @param randomBuffer generate a random buffer length limited to 20 elements
     */
    public Buffer(boolean randomBuffer){
        if(!randomBuffer){
            System.out.println("Error the @param randomBuffer must be true");
            System.exit(1);
        }
        
        this.random = new Random();
        this.buffer = new int[this.random.nextInt(20)];
        this.elementsInBuffer = 0;
        this.isertIndex = 0;
        
        this.mutex = new ReentrantLock();
        this.emptyBuffer = this.mutex.newCondition();
        this.reading = this.mutex.newCondition();
        this.readersReady = new ArrayList<Reader>();
        
        this.time = System.currentTimeMillis();
        this.readersTimes = new ArrayList<Long>();
        this.writersTimes = new ArrayList<Long>();
        
    }
    
    /*PRIVATE METHODS*/
    /**
     * 
     * @param element the element to insert into the buffer.
     */
    private void writeAnElement(int element){
        this.elementsInBuffer++;
        this.buffer[this.isertIndex] = element;
        this.isertIndex = (this.isertIndex + 1) % this.buffer.length;
    }
    
    /*PUBLIC METHODS*/
    /**
     * 
     * @return the current buffer length. 
     */
    public int getBufferLenght(){
        return this.buffer.length;
    }
    
    /**
     * 
     * @param element the element to check if is duplicated.
     * @return true if the @param element isn't duplicated in buffer.
     */
    public boolean isNewElement(int element){
        for(int i : this.buffer){
            if(i == element){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @return the total time of progrma's execution.
     */
    public long getTotalTime(){
        return System.currentTimeMillis() - this.time;
    }
    
    /*insert the time spent by the readers to read an element from the buffer*/
    /**
     * 
     * @param reader the reader that want insert his waiting time.
     */
    public void inserReaderTime(Reader reader){
        try{
            /*start critical section*/
            this.mutex.lock();
            this.readersTimes.add(reader.getTime());
        }finally{
            this.mutex.unlock();
            /*end critical section*/
        }
    }
    
    /**
     * 
     * @return readers average waiting time spend before read the buffer. 
     */
    public long getReadersAverageTime(){
        int temp = 0;
        for(long t : this.readersTimes){
            temp += t;
        }
        
        return temp / this.readersTimes.size();
    }
    
    /**
     * 
     * @param writer the writer that want insert his waiting time.
     */
    public void insertWriterTime(Writer writer){
        try{
            /*start critical section*/
            this.mutex.lock();
            this.writersTimes.add(writer.getTime());
        }finally{
            this.mutex.unlock();
            /*end critical seciton*/
        }
    }
    
    /**
     * 
     * @return readers average waiting time spend before read the buffer. 
     */
    public long getWritersAverageTime(){
        int temp = 0;
        for(long t : this.writersTimes){
            temp += t;
        }
        
        return temp / this.writersTimes.size();
    }
    
    /*PUBLIC SYNCHRONIZATION METHODS*/
    /**
     * 
     * @param reader the reader that want read an element
     * @return the object readed
     */
    public int read(Reader reader) throws InterruptedException{
        try{
            /*start critical section*/
            this.mutex.lock();
            
            while(this.elementsInBuffer < this.buffer.length){
                this.emptyBuffer.await();
            }
            
            System.out.println("--> " + reader.getName() + " is going to read an element");
            
            /*register the reader*/
            this.readersReady.add(reader);
            
        }finally{
            this.mutex.unlock();
            /*end critical section*/
        }
        
        return this.buffer[reader.getIndexToRead()];
    }
    
    /**
     * 
     * @param reader  the reasder that had finished to read from buffer
     */
    public void endToRead(Reader reader) {
        try{
            /*start critical section*/
            this.mutex.lock();
            
            System.out.println("-> " + reader.getName() + " has finished to read an element");
            
            this.readersReady.remove(reader);
            this.reading.signalAll();
        }finally{
            /*end critical section*/
            this.mutex.unlock();
        }
    }
    
    public void write(Writer writer){
        try{
            /*start critical section*/
            this.mutex.lock();
            
            /*suspend the Thread if necessary*/
            while(!this.readersReady.isEmpty()){
                this.reading.await();
            }
            
            System.out.println("#> " + writer.getName() + " is going to write the buffer");
            
            this.writeAnElement(writer.getObject());
            this.emptyBuffer.signalAll();
        }catch(InterruptedException e){
            System.out.println("Error of: " + writer.getName() + " into write"
                                + " method");
                System.exit(1);
        }finally{
            this.mutex.unlock();
            /*end critical section*/
        }
    }
}
