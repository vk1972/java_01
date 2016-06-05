package ie.dd.xml;

import java.io.File;


public class XMLConnection {

	private String foldername;
	
	private XMLConnection(){};
	
	public XMLConnection(String foldername){
		this.foldername = foldername;
	}
	
	public File getConnection(){
        File f = new File(foldername);
	    return f;
	}
}
