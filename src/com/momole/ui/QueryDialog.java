package com.momole.ui;

import javafx.stage.*;

import java.sql.ResultSet;

import com.momole.sql.SQLQueryResultProvider;

import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class QueryDialog {
	
	private Stage stage ; 
	private TextArea txta_sql ; 
	private Button btn_query;
	private Label lab_error;
	private ResultSet results;
	private boolean bDataAvailable; 
	
	public QueryDialog()
	{
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Nouvelle requête SQL");
		
		bDataAvailable=false;
		
		txta_sql = new TextArea();
		btn_query = new Button("Exécuter SQL");
		btn_query.setOnAction(e->btnClick_onExecute());
		
		lab_error = new Label();
		
		VBox vbox = new VBox();
		vbox.getChildren().add( new Label("Requête : ") );
		vbox.getChildren().addAll( txta_sql , btn_query , lab_error);
		
		Scene scene = new Scene( vbox , 300 , 400 );
		
		stage.setScene(scene);
	}
	
	public void show()
	{
		stage.showAndWait();
	}
	
	public void btnClick_onExecute()
	{
		SQLQueryResultProvider provider = new SQLQueryResultProvider();
		ResultSet results = provider.rawQuery( txta_sql.getText() );
		if( results != null ) {/*tester la requête*/
			bDataAvailable = true;
			this.results = results;
			stage.close();
		}else {
			
			if( provider.getLastSQLError().isEmpty() )
				lab_error.setText("Requête sans données résultat");
			else 
				lab_error.setText( provider.getLastSQLError() );
		}
	}
	
	public ResultSet getResults() 
	{
		if( bDataAvailable )
			return results;
		
		return null ; 
	}
}
