package com.momole.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;



/*
 * Permet une meilleure gestion des requêtes 
 * SQL et de leurs résultats
 *  */
public class SQLQueryResultProvider {
	
	private Connection connection = null ; 
	private Statement statement = null ;
	private DatabaseConfig conf ; 
	private String lastSQLError ;
	private float flConnectionSecs;
	private float flQuerySecs;
	private int iAffectedRows;
	
	public SQLQueryResultProvider()
	{
		lastSQLError = "";
		
		
		/* charger la configuration */
		conf = new DatabaseConfig();
		conf.load(); 
		
		/* former une URL du type jdbc:mysql://hote/base?user=nom_utilisateur&password=mot_de_passe 
		 * d'indiquer à java ou se trouve notre base de données */
		String url = "jdbc:mysql://";
		url += conf.getHost();
		url += "/"+conf.getDatabase();
		url += "?user="+conf.getUser();
		if( !conf.getPassword().isEmpty() ) url += "&password="+conf.getPassword();
		
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver"); /* Verifier la présence du Driver MySQL */
			
			System.out.println("Connexion SQL : url="+url); /* on affiche l'URL */
			
			long t0 = System.currentTimeMillis();  /*chronométrer le temps de connexion*/
				
			connection = DriverManager.getConnection(url);	/* effectuer la connexion */
			long t1 = System.currentTimeMillis();
			
			flConnectionSecs = ((float)(t1-t0))/1000; /*stocker le temps de connexion*/
			
		} catch (ClassNotFoundException e) {
			
			lastSQLError = e.getMessage();
			e.printStackTrace();
		} catch (SQLException e) {
			
			lastSQLError = e.getMessage();
			e.printStackTrace();
			
		}
	}	
	
	/* Effectue une requête */
	public ResultSet rawQuery( String query )
	{
		try {
			
			Statement statement = connection.createStatement(); /* créer la requête */
			
			long t0 = System.currentTimeMillis(); /* chronométrer le temps d'exécution */
			statement.execute(query); 						/* exécuter la requête */
			ResultSet results = statement.getResultSet(); /* Recupérer le resultat */
			long t1 = System.currentTimeMillis();
			
			flQuerySecs = ((float)(t1-t0))/1000;
			iAffectedRows = statement.getUpdateCount();
			if(iAffectedRows == -1) iAffectedRows = 0;
			
			return results;
			
		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			lastSQLError = e.getMessage();
			return null ;
		}
	}
	
	/* Code de test JDBC */
	public void queryTest()
	{
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			
			connection = DriverManager.getConnection("jdbc:mysql://localhost/projetmiage?user=root");
			
			statement = connection.createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM projetmiage.etudiant ");
			
			String str = SQLQueryResultProvider.autoFormatResults(results);
			System.out.println(str);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	

	
	/*
	 * Effectue le formatage d'un ensemble de resultat
	 * sous forme de tableau
	 *  */
	public static String autoFormatResults( ResultSet result)
	{
		try {
			
			String format = new String();
			
			ResultSetMetaData metaData = result.getMetaData();
			
			int columnCount = metaData.getColumnCount();
			ArrayList<String> data = new ArrayList<String>(); 
			int columnsWidths[] = new int[columnCount];
			
			/* ajouter les colonnes */
			for(int i=0;i<columnCount;++i) {
				String name =metaData.getColumnName(i+1); 
				data.add( name );
				columnsWidths[i]=name.length(); /* intialiser les tailles */
			}
			
			if(result != null) {
				/* ajouter les données*/
				while( result.next() ) {
					for( int i=0;i<columnCount;++i ) {
						String value = result.getString( i+1 );
						if( value == null ) value = "null";
						if( value.length() > columnsWidths[i] ) 
							columnsWidths[i]=value.length();
						
						data.add( value );
					}
				}
			}
			
			for( int l=0;l<data.size()/columnCount;++l ) {

				for( int i=0;i<columnCount;++i ) {
					format += "+";
					for( int k=0;k<columnsWidths[i]+5;++k ) {
						format += "-";
					}
				}
				
				format+="+\n";
				for( int i=0;i<columnCount;++i ) {
					String value = data.get( l*columnCount+ i);
					format += "|" + value;
					for( int k=0;k<5+columnsWidths[i]-value.length();++k ) {
						format += " ";
					}
				}
				format+="|\n";
			}
			
			for( int i=0;i<columnCount;++i ) {
				format += "+";
				for( int k=0;k<columnsWidths[i]+5;++k ) {
					format += "-";
				}
			}
			format += "+";
			
			return format;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}

	
	public DatabaseConfig getConf() {
		return conf;
	}

	public String getLastSQLError() {
		return lastSQLError;
	}

	public float getConnectionTime() {
		return flConnectionSecs;
	}

	public float getQueryTime() {
		return flQuerySecs;
	}
	
	public int getAffectedRows() {
		return iAffectedRows;
	}

		
}
