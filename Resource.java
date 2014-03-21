package oving4;

/**
 * A resource with an associated lock that can be held by only one transaction at a time.
 */
class Resource
{
  static final int NOT_LOCKED = -1;
  int abortTransactionId;

  /**
   * The transaction currently holding the lock to this resource
   */
  private int lockOwner;
  private Server owner;

  /**
   * Creates a new resource.
   */
  Resource(Server owner)
  {
    lockOwner = NOT_LOCKED;
    this.owner = owner;
  }

  /**
   * Gives the lock of this resource to the requesting transaction. Blocks
   * the caller until the lock could be acquired.
   *
   * @param transactionId The ID of the transaction that wants the lock.
   * @return Whether or not the lock could be acquired.
   */
  synchronized boolean lock(int transactionId)
  {
    if (lockOwner == transactionId) {
      System.err.println("Error: Transaction " + transactionId + " tried to lock a resource it already has locked!");
      return false;
    }
    long start = System.currentTimeMillis();
    while (lockOwner != NOT_LOCKED) {
      try {
    	if(Globals.USE_TIMEOUT){
	        wait(3000);
	        if(start<System.currentTimeMillis()-3000){
	        	System.out.println("TIMEOUT");
	        	return false;
	        }
    	}
    	if(Globals.PROBING_ENABLED){
    		wait();
    		
	        if(abortTransactionId == transactionId)
	        	return false;
    	}
      } catch (InterruptedException ie) {
      }
    }

    lockOwner = transactionId;
    return true;
  }

  /**
   * Releases the lock of this resource.
   *
   * @param transactionId The ID of the transaction that wants to release lock.
   *                      If this transaction doesn't currently own the lock an
   *                      error message is displayed.
   * @return Whether or not the lock could be released.
   */
  synchronized boolean unlock(int transactionId)
  {
    if (lockOwner == NOT_LOCKED || lockOwner != transactionId) {
      System.err.println("Error: Transaction " + transactionId + " tried to unlock a resource without owning the lock!");
      return false;
    }

    lockOwner = NOT_LOCKED;
    // Notify a waiting thread that it can acquire the lock
    notifyAll();
    return true;
  }

  /**
   * Gets the current owner of this resource's lock.
   *
   * @return An Integer containing the ID of the transaction currently
   * holding the lock, or NOT_LOCKED if the resource is unlocked.
   */
  synchronized int getLockOwner()
  {
    return lockOwner;
  }

  /**
   * Unconditionally releases the lock of this resource.
   */
  synchronized void forceUnlock()
  {
    lockOwner = NOT_LOCKED;
    // Notify a waiting thread that it can acquire the lock
    notifyAll();
  }

  /**
   * Checks if this resource's lock is held by a transaction running on the specified server.
   *
   * @param serverId The ID of the server.
   * @return Whether or not the current lock owner is running on that server.
   */
  synchronized boolean isLockedByServer(int serverId)
  {
    return lockOwner != NOT_LOCKED && ServerImpl.getTransactionOwner(lockOwner) == serverId;
  }
}
