import java.util.HashMap;
import java.util.TreeMap;

public class fileTree implements Comparable{
	String fileName; //name for the particular tree file here
	TreeMap<Float,fileTransfer> tree; //efficient resource in search, updating data, and etc 
	HashMap<String,Float> usernameHash; //resource for getting names
	
	public fileTree(){
		tree = new TreeMap<Float,fileTransfer>(); // key: rating, value: file details
		usernameHash = new HashMap<String,Float>(); //key: name, value: rating
	}
	
	public void addToTree(fileTransfer newFile){
		usernameHash.put(newFile.getName(),newFile.getRating());
		tree.put(newFile.getRating(), newFile);
	}
	
	private float getRating(String user){ //retrieves user rating from HashMap
		return usernameHash.get(user);
	}
	
	public void setRating(String user,float newRating){
		float treeOldRating = usernameHash.get(user);
		fileTransfer tempValue = tree.get(treeOldRating);
		usernameHash.put(user,newRating);
		tree.remove(treeOldRating);
		tree.put(newRating,tempValue);
	}
	
	public int compareTo(fileTransfer newFile) {
		if (this.tree > newRating) return 1;
		else if (this < newRating) return -1;
		else if (this == newRating) return 0;
	} 
}
