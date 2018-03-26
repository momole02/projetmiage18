package com.momole.main;

import java.util.ArrayList;

import java.sql.*;

/* Classe responsable du publipostage */
public class Mailings {
	
	private ResultSet results;  /* Resultat */
	private ArrayList<String> mails;
	private String template;
	
	public Mailings( ResultSet results , String template)
	{
		mails = new ArrayList<String>();
		
		this.results = results;
		this.template = template;
	}
	
	public void generateMails()
	{
		if( results != null ) {
			try {
				
				ResultSetMetaData metaData = results.getMetaData();
				while( results.next() ) {
					String mail = template;
					int columnCount = metaData.getColumnCount();
					for( int i=0;i<columnCount;++i ) {
						mail = mail.replace("$"+metaData.getColumnName(i+1), results.getString(i+1));
					}
					mails.add(mail);
				}
				
			} catch (SQLException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	ArrayList<String> getMails(){ return mails; }
}
