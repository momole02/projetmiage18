package com.momole.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/* Configuration de la base de données */
public class DatabaseConfig {
	
	private String host;
	private String user; 
	private String password; 
	private String database;
	
	public DatabaseConfig()
	{
		this.host = "localhost";
		this.user = "root";
		this.password = "";
		this.database = "projetmiage";
	}
	
	public DatabaseConfig( String host , String user , String password , String database )
	{
		this.host = host;
		this.user = user;
		this.password = password;
		this.database = database;
	}
	
	/* Exporte les données de connexion */
	public void export()
	{
		try {
			
			FileOutputStream fos = new FileOutputStream( new File(".sql") ); /* Ouvre le fichier .sql en mode ecriture */
			fos.write( host.length() ); 
			fos.write(host.getBytes("UTF-8")); /* ecrit l'hote */
			fos.write( user.length() );
			fos.write(user.getBytes("UTF-8")); /* ecrit le nom d'utilisateur */
			fos.write( password.length() );
			fos.write(password.getBytes("UTF-8")); /* ecrit le mot de passe */
			fos.write( database.length() ) ;
			fos.write(database.getBytes("UTF-8")); /* ecrit le nom de la base de données */
			
			fos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Charge le fichier */
	public boolean load()
	{
		try {
			
			int length = 0;
			byte b[] ;
			FileInputStream fis = new FileInputStream( new File(".sql") );
			
			length = fis.read();
			b = new byte[length];
			fis.read(b);
			host = new String( b , "UTF-8" ); 		/* charge l'hote  */
			
			length = fis.read();
			b = new byte[length];
			fis.read(b);
			user = new String( b , "UTF-8" ); 		/*  '' l'utilisateur*/
			
			length = fis.read();
			b = new byte[length];
			fis.read(b);
			password = new String( b , "UTF-8" ); 	/*  '' le mot de passe*/
			
			length = fis.read();
			b = new byte[length];
			fis.read(b);
			database = new String( b , "UTF-8" ); 	/* '' la base de données */
			
			fis.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return true;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
	
	
}
