package nl.openedge.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Deze test bewijst dat dat DateFormatHelper thread safe is
 * 
 * @author Eelco Hillenius
 */
public final class DateFormatHelperTestRunner
{

	private static int year = 1945;

	private static int month = 3;

	private static int date = 1;

	/**
	 * Construct
	 */
	public DateFormatHelperTestRunner()
	{

	}

	/**
	 * test threads
	 */
	public void test()
	{
		try
		{
			for (int i = 0; i < 200; i++)
			{
				DateInputThread t = new DateInputThread();
				t.start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * main hook
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		new DateFormatHelperTestRunner().test();
	}

	/*
	 * test thread
	 */
	class DateInputThread extends Thread
	{
		/**
		 * run thread
		 */
		@Override
		public void run()
		{
			String input = "010445";
			try
			{
				for (int i = 0; i < 1000; i++)
				{
					Date dat = DateFormatHelper.fallbackParse(input);
					Calendar cal = Calendar.getInstance(); // 01-04-1945
					cal.setTime(dat);
					int y = cal.get(Calendar.YEAR);
					int m = cal.get(Calendar.MONTH);
					int d = cal.get(Calendar.DATE);

					if (y != year)
					{
						throw new Exception(y + " != " + year);
					}
					if (m != month)
					{
						throw new Exception(m + " != " + month);
					}
					if (d != date)
					{
						throw new Exception(d + " != " + date);
					}

					// test of er geen deadlocks kunnen voorkomen
					DateFormatHelper.format(dat);
					DateFormatHelper.format(dat.getTime());
					DateFormatHelper.format("dd-M-yyyy", dat);
					DateFormatHelper.format("dd-M-yyyy", dat.getTime());

					long wait = (long) (Math.random() * 100);
					// System.out.println(wait);
					Thread.sleep(wait);

				}
				System.out.println(this + " done without errors");
			}
			catch (Exception e)
			{
				System.out.println("testing " + this + " failed");
				e.printStackTrace();
			}

		}
	}

}
