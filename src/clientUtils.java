import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class clientUtils {
	private static final int sLine_MAX = 1024;
	byte[] receiveByteArray= new byte[sLine_MAX];
	byte[] sendByteArray= new byte[sLine_MAX];
	DatagramPacket inPacket, outPacket;
	fileTransfer inData;
	DatagramSocket clientSocket;
	InetAddress serverAddress;
	int serverPort;
	int selfPort;
	String username;
	
	public clientUtils(String name, int self, InetAddress servAdd, int servPort) throws SocketException, UnknownHostException{
		this.selfPort = self;
		this.clientSocket = new DatagramSocket(selfPort);
		this.serverAddress = servAdd;
		this.serverPort = servPort;
		this.username = name;
	}
	
	//UDP send
	private void setOutPacket() throws SocketException { //create a UDP packet to send to a server
		outPacket = new DatagramPacket(sendByteArray, sendByteArray.length, serverAddress, serverPort); 
	}

	private void sendPacket(fileTransfer object) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(sLine_MAX);  
		ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream)); 
		os.flush();
		os.writeObject(object); 
		os.flush();
		sendByteArray = byteStream.toByteArray();  
		setOutPacket();
		clientSocket.send(outPacket); //Begins handshake with server	
		os.close();
	}
	
	//UDP Read
	private void setInPacket() throws SocketException { //create a UDP packet to receive frome a server
		inPacket = new DatagramPacket(receiveByteArray, receiveByteArray.length);
	}
	
	private fileTransfer openPacket() throws IOException, ClassNotFoundException {
		setInPacket();
		int timeOutCount = 0;
		clientSocket.setSoTimeout(300);   // set the timeout in millisecounds.
		while(true){
		    try { 
				clientSocket.receive(inPacket);
				receiveByteArray = inPacket.getData();
				ByteArrayInputStream byteStream2 = new ByteArrayInputStream(receiveByteArray);
				ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream2));
				inData = (fileTransfer) is.readObject();  
				is.close();
				return inData;
		    }
		    catch (SocketTimeoutException e) {
		    	System.out.println("Sorry your connection have timed out.");
		    	timeOutCount++;
		    	if(timeOutCount == 3){
			        fileTransfer error = new fileTransfer();
			        error.setTypeSetter("CLIENT_SERVER_NOT_FOUND");
			        return error;
		    	}
		    }
		}
	}
	
	//Methods for client use	
	public fileTransfer clientServerAsk(String fileName,String typeMsg) throws ClassNotFoundException, IOException{ //return fileTransfer, that will be parsed for file
		fileTransfer returnFile, askFile = new fileTransfer();
		askFile.setTypeSetter(typeMsg);
		askFile.setPort(clientSocket.getLocalPort());
		askFile.setIP(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));
		askFile.setFile(fileName);
		askFile.setName(username);
		sendPacket(askFile);
		returnFile = openPacket();
		return returnFile;
		}
	
	/*
	 * clientServerUpdate sets up clientStatistics updates between client and server.
	 */
	public String clientServerUpdate(String typeMsg) throws ClassNotFoundException, IOException{
		fileTransfer ackState,sendFile = new fileTransfer();
		sendFile.setTypeSetter(typeMsg);
		sendFile.setName(username);
		sendFile.setPort(clientSocket.getLocalPort());
		sendFile.setIP(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));
		sendPacket(sendFile);
		ackState = openPacket();
		return ackState.getTypeSetter();
	}
	}
