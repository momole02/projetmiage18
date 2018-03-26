package com.momole.ui;


import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import com.momole.sql.DatabaseConfig ; 

public class DatabaseConfigDialog {
	
	private Stage stage ;
	private TextField txtf_host;
	private TextField txtf_database;
	private TextField txtf_user;
	private PasswordField passf_password;
	private Button btn_validate;
	
	private DatabaseConfig config;
	private final int STAGE_WIDTH = 400; 
	
	public DatabaseConfigDialog()
	{
		
		config = new DatabaseConfig();
		config.load();
		
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		stage.setTitle("Configuration de la base données");
		
		txtf_host = new TextField( config.getHost() );
		txtf_host.setMinWidth(2*STAGE_WIDTH/3);
		txtf_user = new TextField( config.getUser() );
		txtf_user.setMinWidth(2*STAGE_WIDTH/3);
		passf_password = new PasswordField();
		passf_password.setText(config.getPassword());
		passf_password.setMinWidth(2*STAGE_WIDTH/3);
		txtf_database = new TextField( config.getDatabase() );
		txtf_database.setMinWidth(2*STAGE_WIDTH/3);
		
		btn_validate = new Button("Valider");
		btn_validate.setOnAction(e->btnClick_onValidate());
		
		VBox vbox = new VBox(15);

		vbox.getChildren().addAll(
				new Label("Editez les paramètres de conexion à la base de données"),
				this.makeFormHBox("Hôte de connexion : ", txtf_host),
				this.makeFormHBox("Utilisateur : ", txtf_user),
				this.makeFormHBox("Mot de passe : ", passf_password),
				this.makeFormHBox("Base de données : ", txtf_database),
				btn_validate
				);

		Scene scene = new Scene( vbox , STAGE_WIDTH , 300 );
		
		stage.setScene(scene);
	}
	
	public void show()
	{
		stage.showAndWait();
	}
	
	////////////////EVENT HANDLER////////////////////////
	
	public void btnClick_onValidate()
	{
		config.setHost( txtf_host.getText() );
		config.setUser( txtf_user.getText() );
		config.setPassword( passf_password.getText() );
		config.setDatabase( txtf_database.getText() );
		config.export();
		stage.close();
	}

	private HBox makeFormHBox( String label , Node node )
	{
		HBox hbox = new HBox();
		
		Label lab = new Label(label);
		lab.setMinWidth(STAGE_WIDTH/3);
		hbox.getChildren().add( lab );
		hbox.getChildren().add(node);
		return hbox;
	}
	
	
}
