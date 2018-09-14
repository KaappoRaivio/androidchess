package kaappo.androidchess.askokaappochess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class pgnplay
{
	public static void main (String args[]) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		
		String sctr = br.readLine();
		String ob = "";
		while (sctr != null)
		{
			sctr = sctr.trim();
			playit(sctr);
			sctr = br.readLine();
		}
		
		
	}
	
	
	public static void playit(String sGame) {
		chessboard cb = new chessboard();
		cb.init();
		cb.redoVectorsAndCoverages(piece.WHITE, movevalue.ALG_SUPER_PRUNING_KINGCFIX);
		cb.iMoveCounter=1;
		
		String sGameComp[] = sGame.split(" ");
		
		int iMove = 1;
		int iErr = 0;
		while(iMove < 17)
		{
			//System.out.println("Move:" + sGameComp[iMove*3-3]);
			//System.out.println("White:" + sGameComp[iMove*3-2]);
			//System.out.println("Black:" + sGameComp[iMove*3-1]);
			
			String sWhiteMove = sGameComp[iMove*3-2];
			String sBlackMove = sGameComp[iMove*3-1];
			
			if ((sWhiteMove.length() == 0) || (sBlackMove.length() == 0))
			{
				System.out.println("iMove:" + iMove);
				System.out.println("WhiteMove:<" + sWhiteMove + ">");
				System.out.println("BlackMove:<" + sBlackMove + ">");
				//System.exit(0);
			}
			
			cb.domove_bylib(sWhiteMove,piece.WHITE);
			cb=cb.copy();
			
			cb.redoVectorsAndCoverages(piece.BLACK, movevalue.ALG_SUPER_PRUNING_KINGCFIX);
			cb.dump();
			System.out.println(cb.FEN());
			
			String sLibMove = enginerunner.getMove(cb.FEN());
			if ((sLibMove == null) && (iMove > 1))
			{
				System.out.println("Unknown pos. Turn BLACK. iMove:" + iMove);
				cb.dump();
				System.out.println(cb.FEN());
				play.printFENEntry(cb.FEN());
				if (iMove>1) iErr++;
				if (iErr > 2) iMove = 17;
			}
			
			if (iMove < 17)
			{
			
				cb.domove_bylib(sBlackMove,piece.BLACK);
				cb=cb.copy();
				
				cb.redoVectorsAndCoverages(piece.WHITE, movevalue.ALG_SUPER_PRUNING_KINGCFIX);
				cb.dump();
				System.out.println(cb.FEN());
				
				
				
				sLibMove = enginerunner.getMove(cb.FEN());
				if ((sLibMove == null) && (iMove > 1))
				{
					System.out.println("Unknown pos. Turn WHITE. iMove:" + iMove);
					cb.dump();
					System.out.println(cb.FEN());
					play.printFENEntry(cb.FEN());
					if (iMove>1) iErr++;
					if (iErr > 2) iMove = 17;
				}
			}
			//cb.dump();
			
			cb.iMoveCounter++;
			iMove++;
		}
		
		
	}
}