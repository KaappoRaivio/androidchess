package kaappo.androidchess.askokaappochess;
import java.util.*;

class mt_movefinder
{
	Vector v_tasks;
	
	mt_movefinder()
	{
		v_tasks = new Vector();
		
		mt_movefinder_ctrl_thread mtt = new mt_movefinder_ctrl_thread(this);
		mtt.start();
	}
	
	void dispose()
	{
		
	}
	
	void add_task(chessboard cb, int iColor, int iRounds, movevalue basemv, int iAlg, boolean bDebug)
	{
		mt_find_task mt = new mt_find_task();
		mt.cb_in = cb;
		mt.cb_out = null;
		mt.iColor = iColor;
		mt.iRounds = iRounds;
		mt.basemv = basemv;
		mt.iAlg = iAlg;
		mt.bDebug = bDebug;
		
		v_tasks.addElement(mt);
	}
	
	chessboard get_bestmove(int iColor, movevalue basemv)
	{
		try 
		{
			Thread.sleep(200);
			
			boolean bTodo = false;
			
			do 
			{ 
				bTodo = false;
				
				for (int i=0; i < v_tasks.size(); i++)
				{
					mt_find_task mtf = (mt_find_task)v_tasks.elementAt(i);
					if (mtf.lEndTime == 0) bTodo = true;
				}
				Thread.sleep(200);
			}  while (bTodo);
			
			for (int i=0; i < v_tasks.size(); i++)
			{
				mt_find_task mtf = (mt_find_task)v_tasks.elementAt(i);
				// put the comparison logic here...
			}
			
			
		}
		catch (Exception e)
		{
			System.out.println("get_bestmove() : que?");
			System.exit(0);
		}
		
		return null;
	}
	
	public static void main (String args[])
	{
		System.out.println("Start");
		
		mt_movefinder mtm = new mt_movefinder();
		
		System.out.println("A");
		
		for (int i = 0;i<20; i++)
		{
			mtm.add_task(null,0,0,null,0,false);
		}
		
		System.out.println("B");
		
		chessboard cb = mtm.get_bestmove(0,null);
		
		System.out.println("C");
	}
}

class mt_find_task
{
	chessboard cb_in;
	chessboard cb_out;
	int iColor;
	int iRounds;
	movevalue basemv;
	int iAlg;
	boolean bDebug;
	long lCreateTime;
	long lStartTime;
	long lEndTime;
	
	mt_find_task()
	{
		lCreateTime = System.currentTimeMillis();
		lStartTime = 0;
		lEndTime = 0;
	}
}

class mt_movefinder_ctrl_thread extends Thread implements Runnable
{
	mt_movefinder finder;
	int iActThr = 0;
	
	mt_movefinder_ctrl_thread(mt_movefinder m)
	{
		finder = m;
	}
	
	public void run()
	{
		
		while (true)
		{
			try
			{
				Thread.sleep(200);
				
				for (int i=0; i < finder.v_tasks.size(); i++)
				{
					mt_find_task mtf = (mt_find_task)finder.v_tasks.elementAt(i);
					if (mtf.lStartTime == 0)
					{
						if (iActThr < 4)
						{
							mt_movefinder_work_thread mwt = new mt_movefinder_work_thread(mtf,this);
							mwt.start();
							iActThr++;
						}
					}
				}
				
				if (iActThr == 0) return;
			}
			catch (Exception e)
			{
				System.out.println("Que?");
			}
			
		}
	}
}

class mt_movefinder_work_thread extends Thread implements Runnable
{
	mt_find_task m;
	mt_movefinder_ctrl_thread ctrl;
	
	mt_movefinder_work_thread(mt_find_task mtf, mt_movefinder_ctrl_thread c)
	{
		m = mtf;
		ctrl = c;
	}
	
	public void run ()
	{
		m.lStartTime = System.currentTimeMillis();
		
		try 
		{
			Thread.sleep((int)(1000*Math.random()));
		}
		catch (Exception e)
		{
		}
		m.lEndTime = System.currentTimeMillis();
		
		ctrl.iActThr--;
	}
}