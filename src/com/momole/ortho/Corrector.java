package com.momole.ortho;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.languagetool.JLanguageTool;
import org.languagetool.language.English;
import org.languagetool.language.French;
import org.languagetool.rules.RuleMatch;

import com.momole.ui.OrthoErrorDialog;


public class Corrector {
	
	private String html ; 
	private Document document ; 
	private ArrayList<TextNode> textNodes; 
	private ArrayList<String> dict ;
	private boolean bUseEnglish; 
	
	
	public Corrector( String html , ArrayList<String> dict , boolean useEnglish )
	{
		this.bUseEnglish = useEnglish; /* utiliser l'anglais au lieu du français */
		this.dict = dict;
		this.html = html ; 
		
		this.document = Jsoup.parse(html); /* charger l'arbre DOM corresponsant */
		textNodes = new ArrayList<TextNode>();
		
		this.extract(document.body());
		
	}
	
	/**
	 * @brief Extrait les noeud textes du HTML
	 * */
	private void extract( Node root )
	{
		int childrenCount = root.childNodeSize();
		for( int i=0;i<childrenCount;++i ) { /* parcourir tout les enfants */
		
			Node child = root.childNode(i);
			
			if( child instanceof TextNode ) { /* si on tombe sur un noeud texte */
				
				String content = ((TextNode)child).text(); /* on recupère son conten */
				content = content.trim();
	
				if( !content.isEmpty() ) { /*et s'il n'est pas que blanc */
//					System.out.println("#Text : " + content ); 
					textNodes.add((TextNode) child); /* l'ajouter */
				}
				
			}else {
				this.extract(child);
			}
		}
	}
	
	/**
	 * @brief Lancer la correction */
	
	public boolean launch()
	{
		
		JLanguageTool langTool = null ; 
		if( bUseEnglish )	langTool = new JLanguageTool( new English() ); /* utiliser le dico anglais */
		else langTool = new JLanguageTool( new French() ); /* utiliser le dico français */
		
		for( TextNode node : textNodes ){ /* pour chaque noeud texte trouvé */
			
			
			try {
				boolean replaced = true;
				while( replaced ){
					
					replaced = false;
					
					List<RuleMatch> errors = langTool.check(node.text()); /* Verifier les erreurs */
					
					for(RuleMatch err : errors) { /* pour chaque erreure trouvée */
						
						List<String> suggested = err.getSuggestedReplacements(); /* charger les s uggestions de langageTool */
						String word = node.text().substring( err.getFromPos() , err.getToPos() ); 
						
						if( suggested.isEmpty() && !this.bUseEnglish ) /* ne pas utiliser le dictionnaire(français) si on est en anglais  */  
							suggested = this.searchSuggested(word, dict); /* Charger nos suggestions */
						
						/* Afficher l'erreur et les suggestions dans la boite de dialogue */
						OrthoErrorDialog dial = new OrthoErrorDialog( node.text() ,  err.getMessage(),
								 suggested,err.getFromPos(), err.getToPos() );
						dial.show();
						
						if( dial.doStop() ) /* lorsque l'utilisateur décide d'arrêter la verification */
							return false;
						
						if( dial.doReplace() ){ /* lorsque l'utilisateur remplace un mot
													(il faut renvoyer le noeud entier à la méthode check() */
							
							replaced=true;
							node.text( dial.getText() );
							break;
						}
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
		}
		/* prendre le document ou l'on à fait les corrections */
		html = document.html();
		return true;
	}
	
	private ArrayList<String> searchSuggested( String str , ArrayList<String> dictionary )
	{
		
		ArrayList<String> result = new ArrayList<String>();
		String textSoundex = Corrector.soundex(str); /* code phonétique du mot fourni*/
		for( String word : dictionary ) {
			String wordSoundex = Corrector.soundex(word); /* code phonétique du mot dans le dictionnaire*/
			
			if( wordSoundex.compareToIgnoreCase(textSoundex) == 0) { /* comparer les deux codes phonétiques */
				result.add(word); /* proposer le mot */
			}
		}

		return result;
		
	}
	
	public static ArrayList<String> loadDictionary()
	{
		String line = "";
		ArrayList<String> dic = new ArrayList<String>(); 
		
		try {
			BufferedReader reader = new BufferedReader( new FileReader("dic.txt") );
			
			while((line=reader.readLine()) != null) {
				dic.add(line);
			}
			
			reader.close();
			
			return dic;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private static char upper( char ch )
	{
		
		ch = Character.toLowerCase(ch);
		
		if( "aàäâ".lastIndexOf(ch)!=-1 ) 	return 'A';
		if( "eéèêë".lastIndexOf(ch) != -1) 	return 'E';
		if( "iïî".lastIndexOf(ch) != -1 ) 	return 'I';
		if( "oöô".lastIndexOf(ch) != -1 ) 	return 'O';
		if( "uùûü".lastIndexOf(ch) != -1 ) 	return 'U';
		if( "cç".lastIndexOf(ch) != -1 ) 	return 'C';
		
		return Character.toUpperCase(ch);
 	}
	
	public static String soundex(String str)
	{
		String trimmed; 
		String noVowel; 
		String sound; 
		ArrayList<Integer> list; 
		
		trimmed = str.trim(); /* retirer les espaces */
		noVowel = ""+trimmed.charAt(0);
		for( int i=1;i<trimmed.length();++i ) {
			
			char ch = upper( trimmed.charAt(i) );
			if( " AEIYOUHW".lastIndexOf(ch) == -1 ) { /* si ch n'est pas une voyelle ou H ou W ou espace*/
				noVowel = noVowel + ch ; 
			}
		}
		
		list = new ArrayList<Integer>();
		sound = ""+noVowel.charAt(0);
		int i=0;
		for( i=1;i< Math.min(noVowel.length(), 4) ;++i ) { /* transformer chaque lettre en chiffre phonetique */
			
			char ch = noVowel.charAt(i);
			Integer num = 0 ; 
			if( "BP".lastIndexOf(ch)!=-1 ) 			num=1;
			else if( "CKQ".lastIndexOf(ch) !=-1 ) 	num=2;
			else if( "DT".lastIndexOf(ch)  !=-1 ) 	num=3;
			else if( "L".lastIndexOf(ch)   !=-1 ) 	num=4;
			else if( "MN".lastIndexOf(ch)  !=-1 ) 	num=5;
			else if( "R".lastIndexOf(ch)   !=-1 ) 	num=6;
			else if( "GJ".lastIndexOf(ch)  !=-1 ) 	num=7;
			else if( "XZS".lastIndexOf(ch) !=-1 ) 	num=8;
			else if( "FV".lastIndexOf(ch)  !=-1 ) 	num=9;
			else ; 
			
			if( !list.contains(num) ) {
				sound = sound + num;
				list.add(num);
			}
		}
		
		if( i<4 )  /* on à traité moins de 4 éléments */	
			for(int j=i;j<4;++j) sound = sound + "0";
		
		return sound ;
	}

	public String getHtml()
	{
		return html;
	}
}
