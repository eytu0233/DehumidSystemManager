package application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class PropertiesManager {

	private static final String CONFIG = "config.properties";
	private static final String IP = "IPs";
	private static final String ID = "ID";
	private static final String PW = "PW";

	private static PropertiesManager propertiesManager;

	private Properties properties = new Properties();

	private String propertyIP;
	private String propertyID;
	private String propertyPW;
	private TreeSet<String> IPs = new TreeSet<String>();

	public static PropertiesManager getInstance() {
		if (propertiesManager == null)
			propertiesManager = new PropertiesManager();
		return propertiesManager;
	}

	public PropertiesManager() {
		try {
			File config = new File(CONFIG);
			if(!config.exists()){
				config.createNewFile();				
			}
			properties.load(new FileInputStream(CONFIG));
			readIPs();
			readID();
			readPW();
		} catch (FileNotFoundException ex) {			
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void removeIP(String IP) {
		if (IPs.contains(IP)) {
			IPs.remove(IP);
		}
		writeIPs();
		readIPs();
	}

	public void addIP(String IP) {
		IPs.add(IP);
		writeIPs();
		readIPs();
	}

	private void writeIPs() {
		String concatIPs = "";
		for (String ip : IPs) {
			concatIPs += "@" + ip;
		}
		properties.setProperty(IP, concatIPs);
		try {
			properties.store(new FileWriter(CONFIG), "IPs write");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readIPs() {
		propertyIP = properties.getProperty(IP, "");
		for (String ip : propertyIP.split("@")) {
			if (!ip.isEmpty()) {
				IPs.add(ip);
			}
		}
	}
	
	public Set<String> getIPs(){
		return IPs;
	}	
	
	private void readID(){
		propertyID = properties.getProperty(ID, "pi");
	}
	
	public void updateID(String id){
		propertyID = id;
		properties.setProperty(ID, id);
		try {
			properties.store(new FileWriter(CONFIG), "ID write");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getID(){
		return this.propertyID;
	}
	
	private void readPW(){
		propertyPW = properties.getProperty(PW, "raspberry");
	}
	
	public void updatePW(String pw){
		propertyPW = pw;
		properties.setProperty(PW, pw);
		try {
			properties.store(new FileWriter(CONFIG), "PW write");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getPW(){
		return this.propertyPW;
	}

}
