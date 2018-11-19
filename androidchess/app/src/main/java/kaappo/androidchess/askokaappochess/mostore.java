package kaappo.androidchess.askokaappochess;
import java.util.*;

public class mostore
{
	HashMap hm;
	int iCount;
	
	mostore()
	{
		hm = new HashMap();
		iCount = 0;
	}
	
	void dump()
	{
		Set set = hm.entrySet();	
		Iterator i = set.iterator();
		System.out.println("Mostore dump:");
		while(i.hasNext()) 
		{
			Map.Entry me = (Map.Entry)i.next();
			System.out.print(me.getKey() + ": ");
			System.out.println(me.getValue());
		}
		System.out.println("Mostore dump over.");
	}
	
	void addItem(String sFEN, int iLev, String sMO)
	{
		String sKey = sFEN + ";" + iLev;
		hm.put(sKey,sMO);
		iCount++;
		
		//if (iLev >= 3) System.out.println ("DBG151205: mostore ADD SMOVORD iR:"+(iLev+1) + " :"+sMO+ " FEN:" + sFEN);
		
		/*
		String sNMO = (String)hm.get(sKey);
		if (!sNMO.equals(sMO))
		{
			System.out.println("DBG151130: MOSTORE ERROR!");
			System.out.println("DBG151130, key: " + sKey);
			System.out.println("DBG151130, sMO: " + sMO);
			System.out.println("DBG151130, sMO: " + sNMO);
		}
		*/
		/*if (iCount>=10)
		{
			System.out.println("Mostore filled to capacity.");
			dump();
		}*/
	}
	
	String getMoveOrder(String sFEN, int iLev)
	{
		String sKey = sFEN + ";" + iLev;
		return (String)hm.get(sKey);
	}
	
	public static void main (String args[])
	{
		mostore mos = new mostore();
		mos.addItem("eka",1,"eee");
		mos.addItem("toka",1,"tttt");
		mos.addItem("kolmas",1,"kkkkk");
		mos.dump();
		System.out.println(mos.getMoveOrder("eka",1));
		System.out.println(mos.getMoveOrder("kolmas",1));
	}
}