package com.momole.ui;

import java.util.List;

import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
/**  
 * Classe implémentant la boite de dialogue 
 * d'affichage d'erreur d'orthographe
 * 
 * @author Marc Arnaud
 * 
 * */
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OrthoErrorDialog {
	
	private Stage stage;
	
	private TextArea txta_errorText; /* Zone de texte pour afficher le texte */
	private TextArea txta_errorDetails; /* détails sur l'erreur */
	private ListView<String> lview_proposals; /* Liste affichant les propositions de remplacement */
	
	private Button btn_replace; /* Bouton remplacer */
	private Button btn_ignore; 	/* Bouton ignorer */
	private Button btn_cancel; 	/* Bouton annuler */
	private String text ;
	private boolean replace;
	private boolean stop ;
	private int iFrom;
	private int iTo ; 
	
	
	public OrthoErrorDialog( String text , String details , List<String> suggested , int from , int to )
	{
		stage = new Stage();
		stage.setTitle("Correcteur orthographique");
		stage.initModality(Modality.APPLICATION_MODAL);
		
		this.text = text;
		this.iFrom = from ; 
		this.iTo = to;
		
		/* instancier les controles */
		btn_replace = new Button( "Remplacer le mot" );
		btn_replace.setOnAction(e->btnClick_onReplace());
		
		btn_ignore = new Button("Ignorer");
		btn_ignore.setOnAction( e->btnClick_onIgnore() );
		
		btn_cancel = new Button("Annuler la correction");
		btn_cancel.setOnAction( e->btnClick_onStop() );

		txta_errorText = new TextArea();
		txta_errorText.setText(text);
		txta_errorText.selectRange( this.iFrom  ,this.iTo );
		
		txta_errorDetails = new TextArea( details );
		
		lview_proposals = new ListView<String>();
		for( String proposal : suggested ) {
			
			lview_proposals.getItems().add(proposal);
			
		}
		
		/* layout */
		VBox buttonsVBox = new VBox(20);
		buttonsVBox.getChildren().addAll(new Label("Options:") , btn_replace , btn_ignore ) ; 
		
		VBox textVBox = new VBox(7);
		textVBox.getChildren().addAll( 
				new Label("Texte avec erreurs : ") , txta_errorText , 
				new Label("Détails : ") , txta_errorDetails,
				new Label("Propositions:"),	lview_proposals , btn_cancel);
		
		HBox mainHBox = new HBox(10);
		mainHBox.getChildren().addAll( textVBox , buttonsVBox );
		
		/* Création de la scene */
		Scene scene = new Scene( mainHBox , 650 , 450 );
		stage.setScene(scene);
		stage.setResizable(false);
		
	}
	
	public void btnClick_onReplace()
	{
		String word = lview_proposals.getSelectionModel().getSelectedItem(); /* recupère le mot séléectionné */
		if( word == null ) { /* aucun mot n'est sélectionné */
			Alert alert = new Alert( AlertType.WARNING , "Choisissez un mot" , ButtonType.OK );
			alert.showAndWait();
			
		}else { /* remplacer  */
			
			text = text.replace(text.substring( this.iFrom , this.iTo ) , word);
			stage.close(); /* fermer la fenetre */
			this.replace = true;
		}
	}
	
	public void btnClick_onIgnore()
	{
		stage.close();
	}
	
	public void btnClick_onStop()
	{
		this.stop = true;
		stage.close();
	}
	
	public void show()
	{
		stage.showAndWait();
	}
	
	
	public boolean doReplace()
	{
		return this.replace;
	}
	
	public boolean doStop()
	{
		return this.stop;
	}
	
	public String getText()
	{
		return text;
	}
	
}
