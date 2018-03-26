package com.momole.ui;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.sql.*;

import com.momole.sql.*;

public class QueryResultDialog {
	
	private Stage stage ; 
	private TextArea txta_queryResView;
	private TextArea txta_queryStr;
	private TextArea txta_databaseInfo;
	
	public QueryResultDialog( String query , SQLQueryResultProvider provider )
	{
		stage = new Stage();
		stage.initModality( Modality.APPLICATION_MODAL );
		stage.setTitle("Résultats d'une requête SQL");
		
		txta_databaseInfo = new TextArea();
		txta_databaseInfo.setText(
				"Hôte : " + provider.getConf().getHost()+"\n"+
				"Utilisateur : " + provider.getConf().getUser()+"\n"+
				"Mot de passe : " + provider.getConf().getPassword()+"\n"+
				"Base de données : " + provider.getConf().getDatabase()+"\n"
				);
		txta_databaseInfo.setEditable(false);
		txta_databaseInfo.setMinHeight(100);
		txta_databaseInfo.setFont(new Font("Courier New" , 12)) ;
		
		txta_queryStr = new TextArea();
		txta_queryStr.setMinHeight(100);
		txta_queryStr.setText(query);
		txta_queryStr.setEditable(false);
		txta_queryStr.setFont(new Font("Courier New", 12));
		
		txta_queryResView = new TextArea();
		txta_queryResView.setMinHeight(300);
		txta_queryResView.setFont(new Font("Courier New", 12));
		txta_queryResView.setEditable(false);
		if( !provider.getLastSQLError().isEmpty() ) {
		
			txta_queryResView.setText( provider.getLastSQLError() );
		
		}else {
			ResultSet results = provider.rawQuery(query); 
			if( results != null )
				txta_queryResView.setText( SQLQueryResultProvider.autoFormatResults(results) );
			
			else
				txta_queryResView.setText( provider.getLastSQLError() );
			
			String others = "\n\nLignes affectées : "+provider.getAffectedRows()+
					"\nTemps requête : "+provider.getQueryTime()+" secs " + 
					"\nTemps connexion : "+provider.getConnectionTime()+" secs ";
			
			txta_queryResView.setText( txta_queryResView.getText() + others );
		}
		
		VBox vbox = new VBox();
		
		vbox.getChildren().add( new Label("Infos de la base de données:") );
		vbox.getChildren().add( txta_databaseInfo );
		vbox.getChildren().add( new Label("Requête : ") );
		vbox.getChildren().add( txta_queryStr );
		vbox.getChildren().add( new Label("Résultats: ") );
		vbox.getChildren().add( txta_queryResView );
		
		Scene scene = new Scene( vbox , 400 , 560 );
		
		stage.setScene(scene);
		
		
	}
	
	public void show()
	{
		stage.showAndWait();
	}
	
}
