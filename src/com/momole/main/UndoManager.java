package com.momole.main;

import java.util.LinkedList;
import javafx.scene.web.*;

public class UndoManager implements Runnable{
	
	private LinkedList<String> leftList ; /* liste de gauche */
	private LinkedList<String> rightList ;  /* liste de droite */
	private long lTimeout; /* Délai avant le prochain check-up */
	private long lUptime; 
	private HTMLEditor editor;
	private boolean bRun;
	
	public UndoManager( HTMLEditor ed )
	{
		this.bRun = true;
		this.editor = ed;
		lUptime = System.currentTimeMillis();
		lTimeout = 30 ;
		leftList = new LinkedList<String>();
		rightList = new LinkedList<String>();
		
		(new Thread(this)).start();
	}
	
	public UndoManager( HTMLEditor ed , long timeout )
	{
		this.editor = ed ; 
		lTimeout=timeout ; 
	}

	public void undo()
	{
		if( leftList.isEmpty() )
			return ;
		
		String last = leftList.getLast();
		rightList.addLast(last);
		leftList.removeLast();
		
		if( !leftList.isEmpty() ) {
			last = leftList.getLast();
			editor.setHtmlText(last);	
		}
		
	}
	
	public void redo()
	{
		
		if( !rightList.isEmpty() ){
			String last = rightList.getLast();
			rightList.removeLast();
			leftList.addLast(last);
			editor.setHtmlText(last);
		}
	}
	
	@Override
	public void run() {
		
		try {
			
			while( this.bRun ){

				long elapsed = System.currentTimeMillis();
				String current = editor.getHtmlText();
				
				if( elapsed-lUptime > lTimeout) {
				
					if( leftList.isEmpty() ) { /* 1er état */
						leftList.addLast(current);
//						System.out.println("First : "+current);
					}else { 
						
						
						String leftLast = (!leftList.isEmpty()) ?  leftList.getLast() : null ;
						String rightLast = (!rightList.isEmpty()) ? rightList.getLast() : null;
						
						if( 	(leftLast==null || leftLast.compareToIgnoreCase(current)!=0 )&&
								(rightLast == null || rightLast.compareToIgnoreCase(current)!=0)) { /* En cas de changement */
							
							leftList.addLast(current);
							if( rightLast != null && rightLast.compareToIgnoreCase(current)!=0 )
								rightList.clear(); 
							
//							System.out.println("New state : "+current);
						}
					}
					lUptime = System.currentTimeMillis();
				}
				
				Thread.sleep( this.lTimeout+1000 );
			}			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void stop()
	{
		this.bRun = false;
	}
	
	
}
