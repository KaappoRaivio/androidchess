package kaappo.androidchess.askokaappochess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class blana
{
	static int fb_level = -1;
	static int fb_color = -1;
	static int ROUND_DURATION = 600;
	static final int BLANA_STARTLEVEL = 3;
	
	public static void main (String args[]) throws Exception
	{
		System.out.println("Blana starts");
		if (args.length == 0)
		{
			read_firstblood();
			run_simus();
		}
		else if (args[0].equalsIgnoreCase("fen"))
		{
			System.out.println(".. by fen: <" + args[1]+">");
			chessboard cb = new chessboard();
			cb.init_from_FEN(args[1]);
			boolean bQuick2 = false;
			boolean bQuick3 = false;
			if ((args.length>2) && (args[2].equalsIgnoreCase("Q2"))) bQuick2 = true;
			if ((args.length>2) && (args[2].equalsIgnoreCase("Q3"))) bQuick3 = true;
			//System.out.println("iMoveCounter:" + cb.iMoveCounter);
			//cb.dump();
			//System.out.println("New FEN:" + cb.FEN());
			switch (cb.iMoveCounter+1)
			{
				case 2:
					if (bQuick2) ROUND_DURATION = 180;
					if (bQuick3) ROUND_DURATION = 540;
					break;
				
				case 3:
					ROUND_DURATION = 1620;
					if (bQuick2 || bQuick3) ROUND_DURATION = ROUND_DURATION / 3;
					break;
					
				case 4:
					ROUND_DURATION = 540;
					if (bQuick2 || bQuick3) ROUND_DURATION = ROUND_DURATION / 3;
					break;

				case 5:
					ROUND_DURATION = 180;
					if (bQuick2 || bQuick3) ROUND_DURATION = ROUND_DURATION / 3;
					break;
				
				case 6:
				case 7:
				case 8:
					ROUND_DURATION = 60;
					if (bQuick2) ROUND_DURATION = ROUND_DURATION / 3;
					break;
					
				default:
					ROUND_DURATION = 1;
					break;
				
			}
			System.out.println("ROUND_DURATION: " + ROUND_DURATION);
			//simulate(cb.FEN(),cb.iFileCol,true);
			simulate(args[1],cb.iFileCol,true);
			
		}
	}
	
	public static void read_firstblood() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader("firstblood.dat"));
		String sctr = br.readLine();
		int lc = 1;
		while (sctr != null)
		{
			sctr = sctr.trim();
			if (lc==1)
			{
				if (sctr.equalsIgnoreCase("WHITE")) fb_color = piece.WHITE;
				if (sctr.equalsIgnoreCase("BLACK")) fb_color = piece.BLACK;
			}
			
			if (sctr.indexOf("MC:") != -1)
			{
				String scp[] = sctr.split(":");
				fb_level = new Integer(scp[1]).intValue();
			}
			
			sctr = br.readLine();
		}
		
		br.close();
		System.out.println("read_firstblood done. fb_level:" + fb_level + " fb_color:" + fb_color);
		
		
	}
	
	public static void run_simus() throws Exception
	{
		simulate("firstblood.dat",fb_color, false);
		for (int sl=fb_level-1;sl>BLANA_STARTLEVEL-1;sl--) 
		{
			String fname = "blood"+sl+".dat";
			simulate(fname,fb_color, false);
		}
	}
	
	public static void simulate(String fname, int fb_color, boolean bFENFlag) throws Exception
	{
		System.out.println("simulate: " + fname + " color:" + fb_color);
		
		long lStartTime = System.currentTimeMillis();
		
		Process p = Runtime.getRuntime().exec("cmd /c copy " + fname + " fb0.dat");
		int iRet = p.waitFor();
		
		p = Runtime.getRuntime().exec("cmd /c del sfinput.txt");
		iRet = p.waitFor();
		
		System.out.println("File copied and input deleted. Starting simulation at: " + new Timestamp(lStartTime));
		
		long lNow = System.currentTimeMillis();
		while (lNow < lStartTime + ROUND_DURATION*1000)
		{
			System.out.print("#");
			String sRun;
			switch (fb_color)
			{
				case piece.WHITE:
					sRun = "cmd /c java play filesimux:fb0.dat 1:1 42:1098 > run.out";
					if (bFENFlag) sRun = "cmd /c java play filesimufen:" + (char)34 + fname + (char)34 +" 1:1 42:1098 > run.out";
					p = Runtime.getRuntime().exec(sRun);
					iRet = p.waitFor();
					break;
					
				case piece.BLACK:
					sRun = "cmd /c java play filesimux:fb0.dat 1:1 1098:42 > run.out";
					if (bFENFlag)
					{
						sRun = "cmd /c java play filesimufen:" + (char)34 + fname + (char)34 +" 1:1 1098:42 > run.out";
						//System.out.println("FENRUN!");
						//System.out.println(sRun);
					}
					p = Runtime.getRuntime().exec(sRun);
					iRet = p.waitFor();
					break;
					
					
				default:
					System.out.println("simulate: fb_color: " + fb_color);
					throw new RuntimeException("simulate: fb_color: " + fb_color);
			}
			
			//System.out.println("Prior ER");
			p = Runtime.getRuntime().exec("cmd /c java enginerunner > er.out");
			//System.out.println("After ER exec");
			iRet = p.waitFor();
			//System.out.println("After ER wait");
			
			lNow = System.currentTimeMillis();
		}
		System.out.println();
		long lAMRUStart = System.currentTimeMillis();
		System.out.print("AMRU Starts at: " +new Timestamp(lAMRUStart) + ".. ");
		
		p = Runtime.getRuntime().exec("cmd /c del sfinput.txt");
		iRet = p.waitFor();
		
		System.out.print("a");
		
		p = Runtime.getRuntime().exec("cmd /c copy anymove.txt sfinput.txt");
		iRet = p.waitFor();
		
		System.out.print("b");
		
		p = Runtime.getRuntime().exec("cmd /c del eru.out");
		iRet = p.waitFor();
		
		System.out.print("b2");
		
		p = Runtime.getRuntime().exec("cmd /c java enginerunner > eru.out");
		iRet = p.waitFor();
		
		System.out.print("c");
		
		p = Runtime.getRuntime().exec("cmd /c del anymove.txt");
		iRet = p.waitFor();
		
		System.out.print("d");
		
		long lAMRUDone = System.currentTimeMillis();
		System.out.println("AMRU Ready at: " +new Timestamp(lAMRUDone));
		
	}
}