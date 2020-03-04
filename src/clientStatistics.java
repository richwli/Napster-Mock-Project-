import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class clientStatistics {
	private String username = "";
	private int filesUploadedServer = 0; //num of files uploaded to server
	private float downloadSpeed = 0; //avg of time per kb taken for user to transfer to/from files
	private float uptime = 0; //avg time online depending on live pings
	private int historyUploadsPeer = 0; //total uploads made to peers in lifetime
	private int recentUploadsPeer = 0; //uploads completed in recent history
	private String clientStatisticsFile = "\\Users\\Owner\\Documents\\Network Java Codes\\PP4b\\ClientData.txt";
	
	public clientStatistics(String u){
		this.username = u;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setUsername(String input){
		username = input;
	}
	
	public int getFilesUploadedServer(){
		return filesUploadedServer;
	}
	
	public void setFilesUploadedServer(int input){
		filesUploadedServer = input;
	}
	
	public float getDownloadSpeed(){
		return downloadSpeed;
	}
	
	public void setDownloadSpeed(float input){
		downloadSpeed = input;
	}
	
	public float getUptime(){
		return uptime;
	}
	
	public void setUptime(float input){
		uptime = input;
	}
	
	public int getHistoryUploadsPeer(){
		return historyUploadsPeer;
	}
	
	public void setHistoryUploadsPeer(int input){
		historyUploadsPeer = input;
	}
	
	public int getRecentUploadsPeer(){
		return recentUploadsPeer;
	}
	
	public void setRecentUploadsPeer(int input){
		recentUploadsPeer = input;
	}
	
	public float ratingFormula(){
		return (float) ((float)(downloadSpeed/2*historyUploadsPeer/filesUploadedServer)/(-1*Math.log10(uptime)*recentUploadsPeer)*5);
	}
	
	public void updateStatistics() throws IOException{
		File path = new File(clientStatisticsFile);
		File newPath = new File("Temp_ClientData.txt");
		newPath.createNewFile();
		BufferedReader lineReader = new BufferedReader(new FileReader(path));
		BufferedWriter lineWriter = new BufferedWriter(new FileWriter(newPath));
		String line,lineName;
		String[]split;
		int newUser = 0; //Set to 1 if the new user is not in database
		while((line = lineReader.readLine()) != null){
			//System.out.println(line);
			split = line.split(",",2);
			lineName = split[0];
			if(username.equals(lineName)){
				//System.out.println("found!");
				lineWriter.write(this.rowDataConversion());
				lineWriter.newLine();
				newUser = 1;
			}else{
				lineWriter.write(line);
				lineWriter.newLine();
			}
		}
		if (newUser == 0){
			lineWriter.write(this.rowDataConversion());
		}
		lineWriter.flush();
		lineWriter.close();
		lineReader.close();
		
		if (path.exists()){
			if(path.delete()){
				newPath.renameTo(path);
			}
			else System.out.println("File cannot be deleted.");
	}
	}
	
	public Boolean retrieveStatistics() throws IOException{
		File path = new File(clientStatisticsFile);
		BufferedReader lineReader = new BufferedReader(new FileReader(path));
		String line,lineName,lineInfo;
		String[]split;
		while((line = lineReader.readLine()) != null){
			//System.out.println(line);
			split = line.split(",");
			lineName = split[0]; 
			if(username.equals(lineName)){
				//System.out.println("found!");
				try{
					setFilesUploadedServer(Integer.parseInt(split[1]));
					setDownloadSpeed(Float.parseFloat(split[2]));
					setUptime(Float.parseFloat(split[3]));
					setHistoryUploadsPeer(Integer.parseInt(split[4]));
					setRecentUploadsPeer(Integer.parseInt(split[5]));
					lineReader.close();
					return true; //This is a returning user
				}catch(ArrayIndexOutOfBoundsException | NumberFormatException e){
					System.out.println("ERROR DATA CORRUPTION FOR USER: " + username);
				}
				break;
			}
		}
		lineReader.close();
		return false; //This is a new user
		}

	
	private String rowDataConversion(){
		return username + ','+ filesUploadedServer +
			   ','+ downloadSpeed +','+ uptime +','+ 
			   historyUploadsPeer +','+ recentUploadsPeer;
	}
}
