package kaappo.androidchess.askokaappochess;

public class tester
{
	public static void main (String args[])
	{
		bishop b = new bishop(2,7,1);
		king k = new king(5,4,0);
		move m = new move (6,3,false,0,k);
		
		System.out.println("Tester " + b.moveinline(m,k) + "," + b.couldhit(m.xtar, m.ytar));
	}
}