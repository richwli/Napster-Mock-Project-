import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class clientHandshakeThread implements Runnable{
	ServerSocket handshakeSocket;
	int threadcount;
	
	public clientHandshakeThread (ServerSocket handshake){
		this.handshakeSocket = handshake;
	}
	public void run() {
		int threadCount = 0;
		SocketAddress clientAddress;
		int clientPort;
		while(true){
			// Handshake
			Socket convoSocket;
			try {
				convoSocket = handshakeSocket.accept();
				clientAddress = convoSocket.getRemoteSocketAddress();
				clientPort = convoSocket.getPort();
				System.out.println("New client has requested a file! \n " +
								   "Connection: "+ clientAddress+ "\n" +
								   "Port: "+ clientPort);
				//Spawns dedicated threads that will handle file transfer and P2P talk
				Thread helper = new Thread (new clientFileTransferThread (convoSocket,threadCount)); 
				helper.start();
				threadCount++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
}