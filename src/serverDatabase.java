import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
public class serverDatabase {
	private static String typeSetterContent(String command){
		//Parses typeSetter string for its command instruction. -1 if things go gravely wrong.
		try{
			return command.split("-")[0];
		}catch(NumberFormatException e){
			System.out.println("ERROR DATA CORRUPTION.");
			return "";
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		int servPort = 8005;
		int threadCount = 0;
		serverUtils serverConnection = new serverUtils(servPort);
		serverConnection.createServerSocket();
		serverConnection.setInPacket();
		ConcurrentHashMap<String,fileTransfer> database = new ConcurrentHashMap<String,fileTransfer>(); 
		ConcurrentHashMap<String,fileTransfer> clientFileTransferMap = new ConcurrentHashMap<String,fileTransfer>();
		
		while(true){
			fileTransfer inData = serverConnection.openPacket();
			System.out.println("\n(ServerDatabase Side)");
			inData.prettyPrint();
			System.out.println("\n");

			if(clientFileTransferMap.putIfAbsent(inData.getName(),inData) != null){
				System.out.println(inData.getName()+" is being updated.");		
				fileTransfer oldValue = clientFileTransferMap.get(inData.getName());
				System.out.println( "(Key-Sleep) inData.getName is: "+inData.getName() +"\n");
				System.out.println( "(Key-Sleep) oldValue is: " ); oldValue.prettyPrint() ;
				System.out.println( "(Key-Sleep) inData.getTypeSetter is: "+inData.getTypeSetter() +"\n");
				
				clientFileTransferMap.replace(inData.getName(),oldValue,inData); //ERROR OCCURRING AFTER HERE!!!
			}
			//Adds incoming packet into map for processing
			System.out.println(inData.getTypeSetter()+" added to Hashmap!!!");
			
			if(typeSetterContent(inData.getTypeSetter()).equals("CLIENT_SERVER_HANDSHAKE")){
				/*
				 * Spawns new thread for each handshake thread.
				 */
				int threadPort = ThreadLocalRandom.current().nextInt(1024,49152);
				System.out.println("New user here!!! Address is "+ inData.getIP()+ " Port is "+inData.getPort()+"\n");
				serverUtils threadConnection = new serverUtils(threadPort);
				threadConnection.createServerSocket();
				threadConnection.setAddressPort(inData.getIP(), inData.getPort());
				Thread helper = new Thread (new serverThread(threadCount, database, threadConnection,clientFileTransferMap, inData.getName()));
				helper.start();
				threadCount++;
			}
			}
		}
	
}

