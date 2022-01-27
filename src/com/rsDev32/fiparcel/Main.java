package com.rsDev32.fiparcel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Main {
	private static String userName;
	private static File recieveFolder;
	
	public static void main(String args[]) {
		try {
			init();
			new Window();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Permissions Denied!\nTry running as administrator");
			e.printStackTrace();
		}
	}
	
	public static void init() throws IOException {
		File appDir=new File(System.getenv("LOCALAPPDATA")+File.separator+"FiParcel");
		appDir.mkdirs();
		File confFile=new File(appDir+File.separator+"user.conf");
		if(!confFile.exists()) {
			DataOutputStream dout=new DataOutputStream(new FileOutputStream(confFile));
			dout.writeUTF(System.getProperty("user.name"));
			dout.writeUTF(System.getProperty("user.home")+File.separator+"Documents"+File.separator+"FiParcel");
			dout.close();
		}
		DataInputStream din=new DataInputStream(new FileInputStream(confFile));
		userName=din.readUTF();
		recieveFolder=new File(din.readUTF());
		recieveFolder.mkdirs();
		din.close();
	}
	
	public static String getUserName() {
		return userName;
	}
	
	public static File getShareFolder() {
		return recieveFolder;
	}
}
