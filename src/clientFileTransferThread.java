import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;

public class clientFileTransferThread implements Runnable{
	Socket convoSocket;
	int threadName;
	
	public clientFileTransferThread (Socket convo, int name){
		this.convoSocket = convo;
		this.threadName = name;
	}
	
	public void run(){
		fileTransfer receivedFile, sendFile;
		
		InetAddress localIP = convoSocket.getLocalAddress();
		int localPort = convoSocket.getLocalPort();
		String ack = "Ack ack bitch";
		sendFile = new fileTransfer();
		sendFile.setFile(ack);
		sendFile.setIP(localIP);
		sendFile.setPort(localPort);

		try {
			//Receiving bitstream
			InputStream is = convoSocket.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			receivedFile = (fileTransfer)ois.readObject();
			receivedFile.prettyPrint();
			
			String fileName = receivedFile.getFile();
			File readFile = new File(fileName);
			if(readFile.exists()){
				//Sending bitstream
				FileInputStream fr = new FileInputStream(readFile);
				byte b[] = new byte[(int)readFile.length()];
			    BufferedInputStream bis = new BufferedInputStream(fr);
			    bis.read(b,0,b.length);			  
			    OutputStream os = convoSocket.getOutputStream(); 
				os.write(b,0,b.length);	
				os.flush();
				System.out.println("The requested file has ceased writing. \n\n");
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("The mighty thread #" + threadName + " has ended. \n\n");
		}
}

