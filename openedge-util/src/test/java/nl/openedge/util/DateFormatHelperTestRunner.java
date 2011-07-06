/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.util;

import java.util.Calendar;
import java.util.Date;

import nl.openedge.util.DateFormatHelper;

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
					//System.out.println(wait);
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

