package kaappo.androidchess.askokaappochess;

import java.util.*;
import java.io.*;

public class regbest
{
	static final int VECTOR_SIZE = 10;
	static final int DBG_LEVEL = 5;
	
	Vector v;
	PrintWriter pw;
	
	regbest()
	{
		v = new Vector();
		for (int i=0;i<VECTOR_SIZE;i++) v.addElement(null);
		//printDbg("regbest created.");
	}
	
	synchronized void  setBest(movevalue mv, int iRounds, int iTurn, int iAlg, int iColor)
	//void setBest(movevalue mv, int iRounds, int iTurn, int iAlg, int iColor)
	{
		rb_entry rbe = (rb_entry)v.elementAt(iRounds);
		
		//System.out.println("REGB SET BEST INSIDE!");
		
		if (rbe == null)
		{
			rbe = new rb_entry(mv,iRounds,iTurn, iAlg, iColor);
			if (iRounds >= DBG_LEVEL) printDbg("setBest NEW ENTRY:"+iRounds+": mv:" +mv.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
			v.set(iRounds,rbe);
		}
		else
		{
			if (!rbe.mv.sRoute.equals(mv.sRoute))
			{
				if (iRounds >= DBG_LEVEL) printDbg("setBest repl OLD ENTRY:"+iRounds+": mv:" +mv.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
			
				rbe.mv = mv.copy();
				rbe.iRounds = iRounds;
				rbe.iTurn = iTurn;
				rbe.iAlg = iAlg;
			}
		}
	}
	
	synchronized void resetBest(int iRounds)
	//void resetBest(int iRounds)
	{
		v.set(iRounds,null);
	}
	
	synchronized movevalue getBestMv(int iRounds)
	{
		rb_entry rbe = (rb_entry)v.elementAt(iRounds);
		movevalue mv = rbe.mv.copy();
		return mv;
	}
	
	synchronized boolean bBranchIsDone(int iRounds, int iTurn, int iAlg, int iColor)
	//boolean bBranchIsDone(int iRounds, int iTurn, int iAlg, int iColor)
	{
		movevalue mvh, mvl;
		rb_entry reh, rel;
		
		reh = (rb_entry)v.elementAt(iRounds+1);
		rel = (rb_entry)v.elementAt(iRounds);
		
		if ((reh == null) || (rel == null)) return false;
		
		mvh = reh.mv;
		mvl = rel.mv;
		
		//System.out.println("DBG151013 (BIDCHECK): iC:" +iColor +" iT:"+iTurn+ " "+ mvl.sRoute + " > " + mvh.sRoute);
		
		if (mvl.isBetterthan(mvh,iColor,iAlg,iTurn,iRounds)) 
		{
			if (iRounds >= DBG_LEVEL) printDbg("Branching step 1 passed with info:" + mvl.sDBG);
			
			if ((mvl.equalStates(mvh)) && (mvl.equalBalance(iAlg,iTurn,mvh)) && (mvl.iCalcValue(iAlg,iTurn) == mvh.iCalcValue(iAlg,iTurn))) return false;
			
			//System.out.println("DBG151013 (BRANCHFIN): iC:" +iColor +" iT:"+iTurn+ " "+ mvl.sRoute + " > " + mvh.sRoute);
			
			if (iRounds >= DBG_LEVEL) printDbg("Branch done. iC:" +iColor+ " iT:" + iTurn+" r:" + iRounds + " " +mvl.dumpstr(iAlg,movevalue.DUMPMODE_SHORT) + " ibt " + mvh.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
			return true;
		}
		else return false;
		
	}
	
	synchronized void printDbg(String s)
	//void printDbg(String s)
	{
		//System.out.println("regbest:printdbg:" + s);
		
		try
		{
			if (pw == null)
			{
				pw = new PrintWriter(new BufferedWriter(new FileWriter("regbest.out", true)));
			}
			//System.out.println("regbest:printdbg a");
			pw.println(s);
			pw.close();
			pw = null;
			//System.out.println("regbest:printdbg b");			
		}
		catch (IOException e) 
		{
			//exception handling left as an exercise for the reader
			System.out.println("IOException at regbest.printDbg() (A)");
			throw new RuntimeException(e);
		}
	}
	
	synchronized regbest copy()
	//regbest copy()
	{
		regbest rb = new regbest();
		for (int i=0;i<VECTOR_SIZE;i++)
		{
			rb_entry rbe = (rb_entry)v.elementAt(i);
			if (rbe != null)
			{
				rb_entry rbe_new = new rb_entry(rbe.mv,rbe.iRounds,rbe.iTurn,rbe.iAlg,rbe.iColor);
				rb.v.set(i,rbe_new);
			}
			else rb.v.set(i,null);
		}
		
		return rb;
	}
	
	boolean rb_exists(int iLevel)
	{
		rb_entry r = (rb_entry)v.elementAt(iLevel);
        return r != null;
	}
}

class rb_entry
{
	movevalue mv;
	int iRounds;
	int iTurn;
	int iAlg;
	int iColor;
	
	rb_entry(movevalue m, int iR, int iT, int iA, int iC)
	{
		mv = m.copy();
		iRounds = iR;
		iTurn = iT;
		iAlg = iA;
		iColor = iC;
	}
}