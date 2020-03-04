import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class serverThread implements Runnable{
	int threadname; //designation number for thread
	String clientUsername; //username of client thread is responsible for
	ConcurrentHashMap<String,fileTransfer> dict; //data holding submitted data
	serverUtils connection;
	ConcurrentHashMap<String,fileTransfer> inDataMap;
	
	public serverThread(int n, ConcurrentHashMap<String,fileTransfer> d, serverUtils c, ConcurrentHashMap<String,fileTransfer> map, String user){
		this.threadname = n; 
		this.clientUsername = user;
		this.dict = d;
		this.connection = c;
		this.inDataMap = map;
	}
	
	public int typeSetterSequence(String command){
		//Parses typeSetter string for its sequence number. -1 if things go gravely wrong.
		try{
			return Integer.parseInt(command.split("-")[1]);
		}catch(NumberFormatException /*|ArrayIndexOutOfBoundsException*/ e){
			System.out.println("I am thread "+threadname+" and DATA CORRUPTION: "+command);

			return -1;
		}
	}
	
	public String typeSetterContent(String command){
		//Parses typeSetter string for its command instruction. -1 if things go gravely wrong.
		try{
			return command.split("-")[0];
		}catch(NumberFormatException e){
			System.out.println("ERROR DATA CORRUPTION.");
			return "";
		}
	}
	
	public void run(){
		System.out.println("Thread: "+threadname+ " has booted.\n"
							+ "It is in charge of user: "+clientUsername +"\n"
							+"Its port is: " +connection.serverPort);
		//Exit will be set to 1 if client terminates. This is achieved if there is no ping back or client sends termination msg
		int exit = 0; 
		int expectedSeqNumber = 0;
		String file;
		String clientInput;
		fileTransfer inData; //data received from client 
		fileTransfer outData = null;  //data sent back to client
		
		// Thread to ping client incase of spurious shutdowns
		//Thread pingThread = new Thread(new serverPingThread(connection.getClientSocketAddress()));
		//pingThread.start();
		
		while(exit == 0){
			if(outData == null) outData = new fileTransfer();
			inData = inDataMap.get(clientUsername);
			
			if(typeSetterSequence(inData.getTypeSetter()) == expectedSeqNumber){
				clientInput = typeSetterContent(inData.getTypeSetter());
			}else{
				clientInput = "";
			}
			
			switch(clientInput){
				case "":
					break;
				
				case "CLIENT_SERVER_HANDSHAKE":
					//Each time client requests with server, this will be the communication response
					outData.setTypeSetter("SERVER_CLIENT_HANDSHAKE");
					break;
					
				case "CLIENT_DATA_UPLOAD":
					//Each time client requests with server, this will be the communication response
					file = inData.getFile();
					if(dict.putIfAbsent(file,inData) == null){
						outData.setTypeSetter("SERVER_DATA_UPLOAD_SUCCESS");
					}else{ 
						outData.setTypeSetter("SERVER_DATA_UPLOAD_FAILURE"); //***Do I need this still?
					}
					break;
				
				case "CLIENT_DATA_REQUEST":
					file = inData.getFile();
					fileTransfer dictFileTransfer = dict.get(file);
					if(dictFileTransfer != null){
						
						outData.cloneFileTransfer(dictFileTransfer);
						outData.setTypeSetter("SERVER_DATA_REQUEST_SUCCESS");
						
					}else{ 
						outData.setTypeSetter("SERVER_DATA_REQUEST_FAILURE");  //File cannot be found
					}
					break;
				
				case "CLIENT_SERVER_HANDSHAKE_END":
					//Clause if client gracefully terminates
					exit = 1;
					break;
			}
			
			try { //Sends whichever result back to client
				if(!clientInput.equals("")){
					connection.sendPacket(outData);
					expectedSeqNumber++;
					System.out.println("Thread "+ threadname+" replied back to client. \n");
				}
			}catch (ClassNotFoundException | IOException e) {
				System.out.println("Seq num mismatch. Setter is: "+ inData.getTypeSetter());
				e.printStackTrace();
			}
		}
			System.out.println("The mighty thread "+ threadname +" has finished its purpose!\n");
		}
}

