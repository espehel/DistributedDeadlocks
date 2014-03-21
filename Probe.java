package oving4;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Probe implements Runnable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 288210901973665886L;
	private ArrayList<Integer> edges = new ArrayList<Integer>();
	int lockOwner;
	Server currentServer;
	Transaction prevTransaction;
	final ServerImpl server;
	
	public Probe(ServerImpl server){
		this.server = server;
	}
	
	
	public Probe(int probeSender, int lockOwner, Server origin){
		edges.add(probeSender);
		this.lockOwner = lockOwner;
		currentServer = origin;
		server = null;
	}

	public Probe(int transaction1, int transaction2) {
		edges.add(transaction1);
		edges.add(transaction2);
//		this.prevTransaction = prevTransaction;
		server = null;
	}
	
	
	@Override
	public void run(){
		try {
			server.receiveProbe(edges);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Override
//	public void run() {
//		while (true) {
//			//if the lockOwner creates a cycle it breaks, else it adds the lock owner to the list of transactions
////			if(createsCycle(lockOwner))
////				break;
////			else
////				edges.add(lockOwner);
//			
//			
//			//Serveren som eier transaksjonenen som bruker ressursen som det ventes på
//			currentServer = ((ServerImpl) currentServer).getServer(ServerImpl.getTransactionOwner(lockOwner));
//			
//			//transaksjonen som eier ressursjen for transaksjon ventet på
//			Transaction currentTransaction;
//			try {
//				currentTransaction = ((ServerImpl)currentServer).getActiveTransaction();
//			
//			//hvis neste transaksjon venter på en ressurs legges den til edges lista, hvis ikke avsluttes probinga
//			if(currentTransaction.isWaitingForResource())
//				if(createsCycle(currentTransaction.getTransactionId())){
//					abortTransaction(currentServer);
//					break;//cycle must abort
//				}
//				else
//					edges.add(currentTransaction.getTransactionId());
//			else
//				break;//everything will be ok
//			
//			ResourceAccess waitingForResource = currentTransaction.getWaitingForResource();
//			System.out.println(waitingForResource);
//			//we are now at the next transaction and sets the owner of the lock it is waiting for accordingly
//			lockOwner = ((ServerImpl) waitingForResource.server).getResource(waitingForResource.resourceId).getLockOwner();
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
//		
//		
//	}
	
	private void abortTransaction(Server server) {
		((ServerImpl)server).abortActiveTransaction();
	}

	public boolean createsCycle(int transId){
		System.out.println(edges.size());
		for (Integer id : edges) {
			if(id == transId)
				return true;
		}
		return false;
	}
	public void addEdge(int transId){
		edges.add(transId);
	}
}
