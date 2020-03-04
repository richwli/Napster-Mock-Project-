import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class client1 {
	public static void main(String[]args) throws IOException, ClassNotFoundException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		InetAddress servAdd = InetAddress.getByName("localhost"); // Server address	
		int myPort = 59432; //personal UDP port number for Client-Server
		int servPort = 8005; // port number of serverDatabase
		int clientPort;
		SocketAddress clientAddress;
		File requestedClientFile;
		ServerSocket handshakeSocket = new ServerSocket(myPort);	
		System.out.println("Hello! Welcome to (not) Napster!!!");
		System.out.println("Please put down your desired Username");
		String username = reader.readLine();
		clientUtils connection = new clientUtils(username,myPort,servAdd,servPort);
		
		//Set up application bootup handshake
		int seqCounter = 0; //Sequence number to improve UDP reliability with server
		String server_available = connection.clientServerUpdate("CLIENT_SERVER_HANDSHAKE-"+seqCounter);
		try{
			if(server_available.equals("CLIENT_SERVER_NOT_FOUND")){
				throw new SocketTimeoutException();
			}else if (server_available.equals("SERVER_CLIENT_HANDSHAKE")){
				System.out.println("Server Found!!!");
				seqCounter++;
			}
			//initialize TCP SYN read thread for p2p handshake
			Thread helper = new Thread (new clientHandshakeThread(handshakeSocket));
			helper.start();
			
			int select = 0;
			while(select!=4){
				select = 0;
				System.out.println("\n"+"User, here are are the options you have to choose from: ");
				System.out.println("To ask the server for the info on a file, please type 1 in the terminal.\n");
				System.out.println("To send the server your info for a file, please type 2 in the terminal.\n");
				System.out.println("To ask another user for a file, please type 3 in the terminal.\n");	
				System.out.println("To go back, just type 'back' in the terminal.\n");
				String input = reader.readLine();
				try{
					select = Integer.parseInt(input);
					if(select<1 || select > 4)
						throw new NumberFormatException();
					
				}catch (NumberFormatException e){
					System.out.println("Your input sucked. Please select a better one. Put only numbers 1-4.\n");
				}
				
				switch(select){
					/*
					 * Bad Input Case
					 */
					case 0: 
						break;
										
					/*
					 * Requesting File from Server
					 */
					case 1: 
						fileTransfer requestResponse;
						System.out.println("What is the name of the file that you want?");
						String fileName = reader.readLine();
						if (fileName.equals("back")) break; //User can break from case and return to menu
						
						requestResponse = connection.clientServerAsk(fileName,"CLIENT_DATA_REQUEST-"+seqCounter);
						if (requestResponse.getTypeSetter().equals("SERVER_DATA_REQUEST_SUCCESS")){ //Server documented response
							requestResponse.prettyPrint();
							seqCounter++;
						}else if (requestResponse.getTypeSetter().equals("SERVER_DATA_REQUEST_FAILURE")){ //File not found
							System.out.println("Sorry! The file you requested does not exist or you don't have permission to access it.");
							seqCounter++;
						}else if (requestResponse.getTypeSetter().equals("CLIENT_SERVER_NOT_FOUND")){ //Server not found
							throw new SocketTimeoutException();
						}
						break;
					
					/*
					 * Uploading file to server
					 */
					case 2:  
						fileTransfer uploadResponse; //Shows if ACK back from server was received or not
						System.out.println("What is the name of the file that you are documenting?");
						String textName = reader.readLine();
						if (textName.equals("back")) break; 
						
						uploadResponse = connection.clientServerAsk(textName,"CLIENT_DATA_UPLOAD-"+seqCounter);
						String receipt = uploadResponse.getTypeSetter();
						if (receipt.equals("SERVER_DATA_UPLOAD_SUCCESS")){
							System.out.println("Your information has been submitted successfully.");
							seqCounter++;
						}else if (receipt.equals("SERVER_DATA_UPLOAD_FAILURE")){
							System.out.println("Sorry! Someone has already submitted this file.");
							seqCounter++;
						}else if (receipt.equals("CLIENT_SERVER_NOT_FOUND")){
							throw new SocketTimeoutException();
						}
						break;
						
					/*
					 * Request P2P from another client
					 */
					case 3:  
						fileTransfer requestForFile, inFile;
						Socket clientSocket = null;
						InetAddress myAddress= InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
						while(true){ //try to take the right input
							try{
								System.out.println("What are the details of the client you're looking for?");
								System.out.println("Write it as filename, IP address, port#");
								input = reader.readLine();
								if (input.equals("back")) break;
								String[] tokens;
								tokens = input.split(" ");
								
								String filename=tokens[0];
								InetAddress tcpIP = InetAddress.getByName(tokens[1]);				
								int tcpPort=Integer.parseInt(tokens[2]);
								requestForFile = new fileTransfer();
								requestForFile.setFile(filename);
								requestForFile.setIP(tcpIP);
								requestForFile.setPort(tcpPort);
								
								try{
									//Sending out bytestream
									clientSocket = new Socket(tcpIP,tcpPort);
									OutputStream os = clientSocket.getOutputStream(); 
									ObjectOutputStream oos = new ObjectOutputStream(os);
									oos.flush();
									oos.writeObject(requestForFile);   //send object to server
									oos.flush();
									
									//Reading in bytestream
									File file = new File(filename);
									if (!file.exists()){
										System.out.println("Creating a duplicate for write.");
									}else{
										file.renameTo(new File(filename+"(1)"));
									}
									file.createNewFile();	
									InputStream is = clientSocket.getInputStream(); 
									OutputStream fr = new FileOutputStream(file);
									byte[]b = new byte[1024];
									int content;
									
									while(true){
										content = is.read(b);
										fr.write(b,0,content);
										if (content != b.length){
											break;
										}
								}
									System.out.println("Bye");
									fr.close();
									clientSocket.close();
								}catch(ConnectException e){
									System.out.println("Error 404, the client you requested is offline/or may not exist.");
								}
								System.out.println("Finished file transfer.");
								break;
							}catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
								System.out.println("You put the wrong input. Please try again.");
							}
						}	
						break;
						
					case 4:
						fileTransfer clientDone = new fileTransfer();
						clientDone.setTypeSetter("CLIENT_SERVER_HANDSHAKE_END");
						break;
				}
			}
	}catch(SocketTimeoutException e){
		System.out.println("Sorry! 404 Error Server Not Found. Please restart the application.");
		handshakeSocket.close();
	}
		System.out.println("Thanks for using Napster!");
	}
}
