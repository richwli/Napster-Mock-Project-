import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class serverUtils {
	private static final int sLine_MAX = 1024;
	byte[] receiveByteArray= new byte[sLine_MAX];
	byte[] sendByteArray= new byte[sLine_MAX];
	DatagramPacket inPacket, outPacket;
	DatagramSocket serverSocket;
	InetAddress clientAddress, serverAddress;
	int clientPort, serverPort;

	public serverUtils(int serverPort) {
		this.serverPort = serverPort;
	}
	
	// Reuses another socket for server use
	public void setServerSocket(DatagramSocket socket) throws SocketException {
		serverSocket = socket;
	}
	
	// Creates a new socket for server use
	public void createServerSocket() throws SocketException {
		serverSocket = new DatagramSocket(serverPort);
	}

	public void setInPacket() throws SocketException {
		inPacket = new DatagramPacket(receiveByteArray, receiveByteArray.length);
	}
	
	public void serverPrint(){
		System.out.println("Handling client at " + inPacket.getAddress().getHostAddress() + " on port " + inPacket.getPort());
	}
	
	public void setAddressPort(InetAddress ip,int port){
		clientAddress = ip;
		clientPort = port;
	}
	
	public fileTransfer openPacket() throws IOException, ClassNotFoundException {
		setInPacket();
		serverSocket.receive(inPacket);
		receiveByteArray = inPacket.getData();
		ByteArrayInputStream byteStream2 = new ByteArrayInputStream(receiveByteArray);
		ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream2));
		fileTransfer data = (fileTransfer) is.readObject();  //get the object with it's data and methods
		is.close();
		return data;
	   }

	public void sendPacket(fileTransfer object) throws IOException, ClassNotFoundException {	
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(sLine_MAX);  
		ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream)); 
		os.flush();
		os.writeObject(object); 
		os.flush();
		sendByteArray = byteStream.toByteArray();  
		outPacket = new DatagramPacket(sendByteArray, sendByteArray.length, clientAddress, clientPort); 
//		System.out.println(" [serverUtil Side] Setter is: "+object.getTypeSetter());
//		System.out.println(" [serverUtil Side] Client Address is: "+clientAddress);
//		System.out.println(" [serverUtil Side] Port is: "+clientPort);
		serverSocket.send(outPacket); 
		os.close(); 
}
	
	//Returns client Socket information
	public InetSocketAddress getClientSocketAddress(){
		InetSocketAddress clientSocketAddress = new InetSocketAddress(clientAddress,clientPort);
		return clientSocketAddress;
	}
	
}


