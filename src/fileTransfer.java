import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;

public class fileTransfer implements Serializable{
	private String name; //username
	private InetAddress ip;
	private int port;
	private String file; //fileName
	private String typeSetter; //Will be the new form of communication via specific string instructs
	
	/* TypeSetter Formats
	 * CLIENT_SERVER_HANDSHAKE
	 * CLIENT_DATA_REQUEST
	 * CLIENT_STAT*
	 * CLIENT_SERVER_HANDSHAKE
	 * CLIENT_DATA_REQUEST 
	 * CLIENT_DATA_UPLOAD 
	 * CLIENT_SERVER_NOT_FOUND 
	 * CLIENT_SERVER_DATA_FOUND 
	 * 
	 * SERVER_CLIENT_HANDSHAKE
	 * SERVER_DATA_UPLOAD_SUCCESS
	 * SERVER_DATA_UPLOAD_FAILURE
	 * SERVER_DATA_REQUEST_SUCCESS
	 * SERVER_DATA_REQUEST_FAILURE
	 */
	public void cloneFileTransfer(fileTransfer original){
		// Clones the contents of one fileTransfer to another
		typeSetter=original.getTypeSetter();
		file=original.getFile();
		name=original.getName();
		ip =original.getIP();
		port = original.getPort();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getTypeSetter(){
		return typeSetter;
	}
	
	public void setTypeSetter(String type){
		typeSetter = type;
	}
	
	public void setName(String n){
		this.name = n;
	}
	
	public InetAddress getIP(){
		return this.ip;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public String getFile(){
		return this.file;
	}
	
	public void setIP(InetAddress newIP){
		this.ip = newIP;
	}
	
	public void setPort(int newPort){
		this.port = newPort;
	}
	
	public void setFile(String newFile){
		this.file = newFile;
	}
	
	public void prettyPrint(){
		System.out.println("(( TypeSetter: "+this.getTypeSetter()+" ))");
		System.out.println("(( Filename: "+this.getFile()+" ))");
		System.out.println("(( Username: "+this.getName()+" ))");
		System.out.println("(( IP: "+this.getIP()+" ))");
		System.out.println("(( Port: "+this.getPort()+" ))");
	}
}
