import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class serverPingThread implements Runnable{
	final int MAX_TIMEOUT = 2000;
	InetSocketAddress clientServerSocket;
	
	public serverPingThread(InetSocketAddress s){
		clientServerSocket = s;
	}

	public void run(){
		System.out.println("Ping Socket is running successfully.");
		Boolean clientOnline = true;
		
		while(clientOnline){
			try{
				Socket pingSocket = new Socket();
				pingSocket.connect(clientServerSocket, MAX_TIMEOUT);
				pingSocket.close();
				System.out.println("I Pinged");
			}catch(IOException e){
				System.out.println("Client is now offline.");
				clientOnline = false;
			}
		
	}	
		System.out.println("Ping thread is dead.");
}
}