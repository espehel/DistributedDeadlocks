package oving4;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Probe implements Runnable {
	

	final ServerImpl server;
	
	public Probe(ServerImpl server){
		this.server = server;
	}
	
	@Override
	public void run(){
		try {
			server.receiveProbe(new ArrayList<Integer>());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
