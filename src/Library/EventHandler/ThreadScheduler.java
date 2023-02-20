package Library.EventHandler;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Console;
import java.util.concurrent.*;


public class ThreadScheduler {
    // Private Instances

    private final ThreadPoolExecutor Scheduler = new ThreadPoolExecutor(0,5,1,TimeUnit.SECONDS,new SynchronousQueue<>()); // Creates a fixed amount of pooled threaded workers to prevent excessive threads and memory usage
    //private final ExecutorService Scheduler = Executors.newFixedThreadPool(5); // Creates a fixed amount of pooled threaded workers to prevent excessive threads and memory usage
    private static final ErrorHandler ErrorHandle = new ErrorHandler(); // Creates a new error handler instance

    //Constructor
    public ThreadScheduler(){
        // Sets the thread pool to allow core threads to time out
        Scheduler.allowCoreThreadTimeOut(true);
        Scheduler.setKeepAliveTime(1, TimeUnit.SECONDS);
    }

    //Public Methods 

    /**
     * Terminates all threads in a given thread pool
     */
    public void Terminate(){
        try {
            // Shuts down the thread pool to release system resources
            Scheduler.shutdown();

            // Waits for all the threads to finish executing
            boolean Status = Scheduler.awaitTermination(10000, TimeUnit.MILLISECONDS);

            // Checks the thread termination status
            if(!Status){
                //Checks the debug mode
                if(SystemStatus.DebugMode){
                    // Informs the user that the threads did not shut down in the time given (10 seconds) meaning a process is still running
                    Console.log("Thread pool did not shut down in time", Theme.Error());
                }
                // Logs the error
                ErrorHandle.log("Thread Scheduler Error", "Thread pool did not shut down in time",2);
            }
            else{
                //Checks the debug mode
                if(SystemStatus.DebugMode){
                    // Informs the user that the threads did not shut down in the time given (10 seconds) meaning a process is still running
                    Console.log("Thread pool shut down successfully", Theme.Success());
                }

                // logs the thread status
                ErrorHandle.action("Thread Scheduler", "Thread pool shut down successfully");
            }

        } catch (Exception e) {
            // Logs the error
            ErrorHandle.log("Thread Scheduler Error", "Thread Shutdown failed | Fault: " + e.getMessage(),3);
        }

    }

    /**
     * Allows you to run asynchronous tasks in a thread pool
     * @param Task Runnable function
     * @return void
     */
    public<t> CompletableFuture<t> Promise(Callable<t> Task){
        return CompletableFuture.supplyAsync(() -> {
            try{
                // Runs the task
                return Task.call();
            }
            catch(Exception e){
                // Logs the error  
                ErrorHandle.log("Thread Scheduler Promise Error", "Promise Execution Failed | Fault: " + e.getMessage(),2);
                return null;
            }
        },Scheduler);
    }
}
