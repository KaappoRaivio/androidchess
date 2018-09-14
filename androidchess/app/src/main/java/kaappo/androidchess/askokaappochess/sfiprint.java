
package kaappo.androidchess.askokaappochess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class sfiprint
{	
	public static void main (String args[]) throws Exception
	{
		BufferedReader br;
		try
		{
			 //br = new BufferedReader(new FileReader("sfi.txt"));
			 FileInputStream fis = new FileInputStream("sfi.txt");
			 br = new BufferedReader(new InputStreamReader(fis, "UTF-16"));
		}
		catch (Exception e)
		{
			System.out.println("No file sfi.txt. Exiting.");
			return;
		}
		
		System.out.println("------------");
		System.out.println("Sfiprint at " + new Timestamp(System.currentTimeMillis()));
		
		String sDir = null;
		int iInpSize = -1;
		int iSfSize = -1;
		
		String sctr = br.readLine();
		while (sctr != null)
		{
			//sctr = sctr.trim();
			//System.out.println("*"+sctr+"*");
			if (sctr.indexOf("Directory") != -1) 
			{
				if ((sDir != null)  && (iInpSize != -1))
				{
					printall(sDir,iSfSize,iInpSize);
					sDir = null;
					iInpSize = -1;
					iSfSize = -1;
				}
				String sComp[] = sctr.split(" +");
				sDir = sComp[2];
				//System.out.println(sDir);
			}
			
			if (sctr.indexOf("sfileinput.txt") != -1) 
			{
				String sComp[] = sctr.split(" +");
				String sNum = sComp[4];
				sNum = sNum.replaceAll(",","");
				iSfSize = new Integer(sNum);
			}
			
			if (sctr.indexOf("sfinput.txt") != -1) 
			{
				String sComp[] = sctr.split(" +");
				String sNum = sComp[4];
				sNum = sNum.replaceAll(",","");
				iInpSize = new Integer(sNum);
			}
			
			sctr = br.readLine();
		}
		if ((sDir != null)  && (iInpSize != -1)) printall(sDir,iSfSize,iInpSize);
		br.close();
		//Thread.sleep(60000);
	}
	
	static void printall(String sName, int i1, int i2)
	{
		String sOut = sName;
		while (sOut.length() < 25) sOut = sOut + " ";
		String sI1 = ""+i1;
		while (sI1.length() < 10) sI1 = " " + sI1;
		String sI2 = ""+i2;
		while (sI2.length() < 10) sI2 = " " + sI2;
		
		float fPerc = 100*((float)i1/(float)i2);
		String sPerc = ""+fPerc;
		while (sPerc.length() < 12) sPerc = " " + sPerc;
		
		int iDiff = i1-(int)(1.13*i2);
		String sDiff = ""+iDiff;
		while (sDiff.length() < 12) sDiff = " " + sDiff;
		
		System.out.println(sOut+sI1+sI2+sPerc+sDiff);
		
	}
}