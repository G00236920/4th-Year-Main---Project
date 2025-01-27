import threading
from queue import Queue
import time
import MariaDBConnection

lock = threading.Lock()

print_lock = threading.Lock()


def exampleJob(worker):
      
    time.sleep(.5) # pretend to do some work.
    if(worker == 0):
        MariaDBConnection.startServer
        print("Thread 1 Start")
        
    elif(worker == 1):
        #thread 2 start
        print("Thread 2 Start")


# The threader thread pulls an worker from the queue and processes it
def threader():
    
    while True:
        # gets an worker from the queue
        worker = q.get()

        # Run the example job with the avail worker in queue (thread)
        exampleJob(worker)

        # completed with the job
        q.task_done()

# Create the queue and threader 
q = Queue()
 
# how many threads are we going to allow for
for x in range(2):
     t = threading.Thread(target=threader)

     # classifying as a daemon, so they will die when the main dies
     t.daemon = True

     # begins, must come after daemon definition
     t.start()

start = time.time()

# 2 jobs assigned.
for worker in range(2):
    q.put(worker)

# wait until the thread terminates.
q.join()

print('Entire job took:',time.time() - start)