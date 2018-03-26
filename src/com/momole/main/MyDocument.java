package com.momole.main;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
/**
 * @brief Classe encapsulant les fonctions d'un document 
 * */

public class MyDocument {
	
	private String html ; 
	private String filePath ; 
	
	public MyDocument( )
	{
		html = "";
		filePath = "";
	}
	
	
	public MyDocument( String path , String html )
	{
		this.filePath = path;
		this.html = html;
	}
	
	public void autoSave()
	{
		this.saveDocument(filePath);
	}
	
	public boolean loadDocument( String file )
	{
		try {
			
			FileInputStream fis = new FileInputStream( file ); /* Crée un lien entre le programme et fichier sur le disque */
			
			byte b[] = new byte[ fis.available() ];
			fis.read( b );  /* tout le fichier et le stock dans b */
			fis.close();
			
			html = new String( b , "UTF-8"); /* convertir l'ensemble des octets de b en chaine de caractères */
			
			filePath = file; /* spécifié qu'un fichier à été charger en stockant son chemin */
			
		} catch (FileNotFoundException e) {
			
			return false;
			
		} catch (UnsupportedEncodingException e) {

			return false;
		} catch (IOException e) {

			return false;
		}
		return true;
	}
	
	public void saveDocument( String file )
	{
		try {
			FileOutputStream fos = new FileOutputStream( file ); /* Crée un lien entre le programme et le fichier sur le disque */
			
			fos.write( html.getBytes("UTF-8") );  /* convertir le HTML en suite d'octets l'écrire dans le fichier */
			
			fos.close();
			
			filePath = file;	/* spécifié qu'un fichier à été charger en stockant son chemin */
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public Document getDOMDocument()
	{
		Document doc = Jsoup.parse(html);
		return doc;
		
	}
	
	public void storeDOMDocument( Document doc )
	{
		doc.outerHtml();
	}
	
	public boolean mustSave(String currentHtml)
	{
		return (currentHtml != html);
	}
	
	public boolean mustSaveAs( )
	{
		return filePath.isEmpty();
	}


	public String getHtml() {
		return html;
	}


	public void setHtml(String html) {
		this.html = html;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
}
