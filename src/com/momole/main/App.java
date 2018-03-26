package com.momole.main;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;


import javafx.application.*;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.web.*;


import com.momole.ui.*;
import com.momole.sql.*;
import com.momole.ortho.*;


public class App extends Application
{
	private MenuBar mainMB ; 
	private Menu fileMenu ; 
	private MenuItem saveFileItem;
	private MenuItem openFileItem ; 
	private MenuItem saveAsItem ; 
	
	private Menu tabsMenu ; 
	private MenuItem addTabItem ; 
	private MenuItem loadTabItem ;  
	
	private Menu toolsMenu ;
	private MenuItem spellCheckItem ; 
	private MenuItem cnfDBItem ; 
	private MenuItem postItem ; 
	private Menu hlpMenu ; 
	private MenuItem aboutItem ; 
	
	private Menu editMenu ;
	private MenuItem undoItem ; 
	private MenuItem redoItem ; 
	
	// private HTMLEditor htmlEdit;
	private Stage primStage ; 
	private TabPane tabPane ;
	private LinkedList<HTMLEditor> editorsList;
	private LinkedList<MyDocument> documents;
	private LinkedList<UndoManager> undoManagers; 
	private ArrayList<String> dictionary;
	
	private TextArea 	txta_SQL; 
	private Button 		btn_SQLquery;
	private RadioButton rbtn_french;
	private RadioButton rbtn_english;
	
	
	@Override
	public void start( Stage primaryStage )
	{
		
		primaryStage.setTitle("Editeur_MIAGE_2018");
		this.primStage = primaryStage;
		
		undoManagers = new LinkedList<UndoManager>();
		editorsList = new LinkedList<HTMLEditor>();
		documents = new LinkedList<MyDocument>();
		
		dictionary = Corrector.loadDictionary( );
		if( dictionary == null ) {
			Alert alert = new Alert( AlertType.ERROR , "Le dictionnaire n'as pas pu être chargé. "
					+ "\nla Certaines fonctions de la verification orthographique seront indisponibles pendant cette session" , ButtonType.OK);
			alert.showAndWait();
		}
		
		
		/* Construire la scène */
		this.buildScene();
		
		this.primStage.show();	
	}
	
	private void buildMenubar()
	{
		/* Barre de menu */	
		mainMB = new MenuBar();

		/*crée le menu fichier */
		fileMenu = new Menu("_Fichier");
		
		mainMB.getMenus().add(fileMenu); /* Ajoute fichier à la barre */
		

		openFileItem = new MenuItem("_Ouvrir fichier"); /* Ajoute 'Ouvrir fichier ' à menu fichier */
		openFileItem.setOnAction( e -> itemClick_onOpenFile() ); /* lorsqu'on clique sur fichier 
																	il faudra exécuter la méthode 
																	itemClick_onOpenFile() */
		
		saveFileItem = new MenuItem("_Enregistrer");
		saveFileItem.setOnAction(e->itemClick_onSaveFile());
		
		saveAsItem = new MenuItem("_Enregistrer sous");
		saveAsItem.setOnAction( e->itemClick_onSaveFileAs() );
		
		fileMenu.getItems().addAll(openFileItem , 
				new SeparatorMenuItem(), saveFileItem , saveAsItem );
		
		
		editMenu = new Menu("_Edition");
		mainMB.getMenus().add(editMenu);
		undoItem = new MenuItem("Annuler");
		undoItem.setOnAction(e->itemClick_onUndo());
		
		redoItem = new MenuItem("Repéter");
		redoItem.setOnAction(e->itemClick_onRedo());
		editMenu.getItems().addAll(undoItem , redoItem);
		
		
		tabsMenu = new Menu("_Onglets");
		mainMB.getMenus().add( tabsMenu );
		
		addTabItem = new MenuItem( "Nouvel onglet" );
		addTabItem.setOnAction( e->itemClick_onAddTab() );
		
		loadTabItem = new MenuItem( "Ouvrir dans nouvel onglet" );
		loadTabItem.setOnAction(e->itemClick_onLoadInNewTab() );
		
		tabsMenu.getItems().addAll( addTabItem , loadTabItem);
		
		
		toolsMenu = new Menu("_Outils");
		mainMB.getMenus().add(toolsMenu);
		spellCheckItem = new MenuItem("Verifier l'orthographe");
		spellCheckItem.setOnAction(e->itemClick_onSpellCheck());
		
		cnfDBItem = new MenuItem("Configurer la base de données");
		cnfDBItem.setOnAction(e->itemClick_onConfigDB());
		
		postItem = new MenuItem("Publipostage");
		postItem.setOnAction( e->itemClick_onDoMailings() );
		toolsMenu.getItems().addAll( spellCheckItem , 
				new SeparatorMenuItem() , cnfDBItem , postItem);
		
		hlpMenu = new Menu("_?");
		mainMB.getMenus().add(hlpMenu);
		aboutItem = new MenuItem("A propos");
		hlpMenu.getItems().add( aboutItem );
	}
	
	private void buildScene()
	{
		Scene scene ; 
		VBox vbox ; 
		
		tabPane = new TabPane();
		
		this.addTab("Document 1");
		
		txta_SQL = new TextArea("Requête SQL");
		txta_SQL.setFont(new Font("Courier New",12));
		btn_SQLquery = new Button("Executer SQL");
		btn_SQLquery.setOnAction(e->btnClick_onSQLResult());
		ToggleGroup group = new ToggleGroup();
		
		rbtn_french = new RadioButton("Français");
		rbtn_french.setSelected(true);
		rbtn_french.setToggleGroup(group);
		
		rbtn_english = new RadioButton("Anglais");
		rbtn_english.setToggleGroup(group);
		
		
		/* positionnement */
		vbox = new VBox( );
		this.buildMenubar();
		
		vbox.getChildren().addAll( mainMB , tabPane ,txta_SQL , btn_SQLquery);
		HBox hbox = new HBox(20);
		hbox.getChildren().addAll(new Label("Langue du correcteur orthographique") , rbtn_french , rbtn_english);
		vbox.getChildren().add(hbox);
		scene = new Scene( vbox , 700 , 500 );
		
		
		/* Go !*/
		primStage.setScene(scene);
		primStage.setOnCloseRequest(e->{
			/*stopper les 'robots' des gestionnaires d'annulation*/
			for(int i=0;i<undoManagers.size();++i)
				undoManagers.get(i).stop();
			
		});
		
	}
	
	///////////////////EVENTS HANDLERS///////////////////
	public void itemClick_onAddTab()
	{
		this.addTab("Document " + (editorsList.size() + 1) );
	}
	
	
	public void itemClick_onOpenFile()
	{
		File f = this.OFN_load("Charger fichier");
		if( f == null)
			return ; 
		
		int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();

		MyDocument doc = documents.get(selectedIndex);
		
		if( doc.loadDocument( f.getPath() ) ) {
			HTMLEditor htmlEdit = editorsList.get(selectedIndex);
			htmlEdit.setHtmlText( doc.getHtml() );
			tabPane.getTabs().get(selectedIndex).setText(f.getName());
		}	
	}

	public void itemClick_onSaveFile()
	{
		this.saveTab(tabPane.getSelectionModel().getSelectedIndex());
	}
	
	/* Commande enregistrer sous */
	public void itemClick_onSaveFileAs()
	{
		this.saveTabAs( tabPane.getSelectionModel().getSelectedIndex() );
	}
	
	/* Commande ouvrir dans un nouvel onglet */
	public void itemClick_onLoadInNewTab( )
	{
		File f = this.OFN_load("Ouvrir dans un nouvel onglet");
		MyDocument doc = new MyDocument();
			
		if( doc.loadDocument( f.getPath() ) ) {
			
			HTMLEditor htmlEditor = new HTMLEditor();
			htmlEditor.setHtmlText( doc.getHtml() );
			this.addTab( f.getName() , htmlEditor);
			tabPane.getSelectionModel().select( editorsList.size()-1 ); /* sélectionner le nouvel onglet */			
		}
	}
	
	/* Commande configurer la base de données */
	
	public void itemClick_onConfigDB()
	{
		DatabaseConfigDialog dialog = new DatabaseConfigDialog();
		dialog.show();
	}
	
	/*Exécuter le SQL*/
	public void btnClick_onSQLResult()
	{
		String sql = txta_SQL.getText(); /* Recupérer la requête tapée par l'utilisateur */
		
		SQLQueryResultProvider queryRes = new SQLQueryResultProvider( );
		QueryResultDialog dialog = new QueryResultDialog( sql , queryRes ); /* Executer la requête puis l'afficher 
																				dans la boite de dialogue*/
		dialog.show();
		
	}
	
	public void itemClick_onDoMailings()
	{
		QueryDialog dialog = new QueryDialog();
		dialog.show();
		
		if( dialog.getResults() != null ) {
			ResultSet results = dialog.getResults();
			
			Mailings mailings = new Mailings( results , this.getTabHtml( tabPane.getSelectionModel().getSelectedIndex() ) );
			mailings.generateMails();
			
			ArrayList<String> mails = mailings.getMails();
			for( int i=0;i<mails.size(); ++i ) {
				HTMLEditor editor = new HTMLEditor();
				editor.setHtmlText( mails.get(i) );
				this.addTab( "Publi#"+i , editor  );
			}
		}
	}
	
	public void itemClick_onUndo()
	{
		int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
		undoManagers.get(selectedIndex).undo();
	}
	
	public void itemClick_onRedo()
	{
		int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
		undoManagers.get(selectedIndex).redo();
	}
	
	public void itemClick_onSpellCheck()
	{
		int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
		
		Corrector corr = new Corrector( editorsList.get(selectedIndex).getHtmlText() , this.dictionary , rbtn_english.isSelected() );
		if( !corr.launch() ) {
			Alert dial = new Alert( AlertType.WARNING  , "La vérification à été interrompue" , ButtonType.OK );
			dial.setTitle("Verification orthographique");
			dial.showAndWait();

		}else {
			
			Alert dial = new Alert( AlertType.INFORMATION  , "La verification est terminée" , ButtonType.OK );
			dial.setTitle("Verification orthographique");
			dial.showAndWait();
			editorsList.get(selectedIndex).setHtmlText(corr.getHtml());
		}		
		
	}
	
	///////////////////METHODES UTILITAIRES///////////////////

	private File OFN_load( String message ) 
	{
		
		FileChooser fileChooser= new FileChooser(); /* Créer la boite de dialogue de sélection de fichiers */
		
		/* Choisir les opérations */
		fileChooser.setTitle(message); 
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Fichiers HTML" ,"*.html" , "*.htm"),
				new FileChooser.ExtensionFilter("Tous les fichiers" ,"*.*" )
				);
		return fileChooser.showOpenDialog(primStage); /* afficher en tant que boite de dialogue d'ouverture de fichier */
	}
	
	private File OFN_save( String message )
	{

		FileChooser fileChooser= new FileChooser();
		fileChooser.setTitle(message);
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Fichiers HTML" ,"*.html" , "*.htm"),
				new FileChooser.ExtensionFilter("Tous les fichiers" ,"*.*" )
				);
		return fileChooser.showSaveDialog(primStage); /* Afficher en tant que boite de dialogue d'enregistrement de fichier */
	}
	
	private void addTab( String tabname )
	{
		addTab( tabname , new HTMLEditor() );
	}
	
	private void addTab( String tabname , HTMLEditor editor )
	{
		ObservableList<Tab> tabs = tabPane.getTabs();
		editorsList.add( editor ); /* ajoute l'éditeur(editor) à la liste des editeurs */
		
		tabs.add( new Tab( tabname , editor ) ); /* ajoute un nouvel onglet qui contient l'éditeur(editorà */
		
		documents.add(new MyDocument(  )); 			 /* Nouvelle interface de gestion des documents */
		undoManagers.add(new UndoManager( editor )); /* nouvelle interface de gestion des annulations et des repétitions */
		
	}
	
	private String getTabHtml( int tab )
	{
		
		return editorsList.get(tab).getHtmlText(); /* Recupère le texte de l'éditeur qui à pour indice tab */
	}
	
	
	private void saveTab( int tab )
	{
		if( documents.get(tab).mustSaveAs() ) { /* Doit-on enregistrer sous ? */ 
			
			this.saveTabAs( tab );
			
		}else if( documents.get(tab).mustSave(this.getTabHtml( tab )) ) { /* Doit-on faire une sauvegarde automatique */
			documents.get(tab).autoSave( ); /* Sauvegarde automatique */
		}else { }
	}
	
	private void saveTabAs( int tab )
	{
		/* Enregistrer le fichier */
		File f = this.OFN_save("Enregistrer document"); /* Demande le fichier à l'utilisateur */
		
		if( f==null ) return ; /* S'il annule on arrête */ 
		
		/* Enregistrer le document */
		HTMLEditor htmlEdit = editorsList.get(tab);
		documents.get(tab).setHtml(htmlEdit.getHtmlText());
		documents.get(tab).saveDocument( f.getPath() );
		
		ObservableList<Tab> tabs = tabPane.getTabs();
		tabs.get(tab).setText(f.getName());
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch( args );
	}

}
