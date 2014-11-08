package ru.serjik.nionet.tests;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import ru.serjik.nionet.BufferQueue;

public class LoopBufferTest
{
	private Random rnd = new Random(System.currentTimeMillis());

	@Test
	public void defaults()
	{
		BufferQueue loopBuffer = new BufferQueue(512);

		try
		{
			byte[] tmp = new byte[10];
			loopBuffer.dequeue(tmp, 0);
			Assert.fail();
		}
		catch (BufferUnderflowException e)
		{
		}

		try
		{
			byte[] tmp = new byte[253];
			loopBuffer.enqueue(tmp, 0, tmp.length);
			loopBuffer.enqueue(tmp, 0, tmp.length);
			Assert.fail();
		}
		catch (BufferOverflowException e)
		{
		}
	}

	@Test
	public void hasData()
	{
		BufferQueue loopBuffer = new BufferQueue(512);

		byte[] tmp = new byte[32];

		for (int i = 0; i < 100000; i++)
		{
			Assert.assertFalse(loopBuffer.hasData());
			loopBuffer.enqueue(tmp, 0, rnd.nextInt(tmp.length));
			Assert.assertTrue(loopBuffer.hasData());
			loopBuffer.dequeue(tmp);
		}

		Assert.assertFalse(loopBuffer.hasData());
	}

	@Test
	public void usage()
	{
		Queue<byte[]> etalon = new LinkedList<byte[]>();
		BufferQueue loopBuffer = new BufferQueue(30000);

		for (int i = 0; i < 40; i++)
		{
			byte[] data = new byte[rnd.nextInt(512) + 32];
			rnd.nextBytes(data);
			etalon.add(data);
			loopBuffer.enqueue(data, 0, data.length);
		}

		byte[] tmp = new byte[1024];

		for (int i = 0; i < 100000; i++)
		{
			byte[] data = new byte[rnd.nextInt(768) + 32];
			rnd.nextBytes(data);
			etalon.add(data);
			loopBuffer.enqueue(data, 0, data.length);

			byte[] etalonData = etalon.remove();
			int lenght = loopBuffer.dequeue(tmp, 0);

			Assert.assertEquals(lenght, etalonData.length);

			for (int j = 0; j < lenght; j++)
			{
				Assert.assertEquals(etalonData[j], tmp[j]);
			}
		}
	}

	@Test
	public void speedEtalon()
	{
		Queue<byte[]> etalon = new LinkedList<byte[]>();

		for (int i = 0; i < 1000000; i++)
		{
			byte[] data = new byte[rnd.nextInt(768) + 32];
			// rnd.nextBytes(data);
			etalon.add(data);

			etalon.remove();
		}
	}

	@Test
	public void speedLoopBuffer()
	{
		BufferQueue loopBuffer = new BufferQueue(30000);
		byte[] tmp = new byte[1024];

		// byte[] data = new byte[rnd.nextInt(768) + 32];

		for (int i = 0; i < 1000000; i++)
		{
			// rnd.nextBytes(data);
			loopBuffer.enqueue(tmp, 0, rnd.nextInt(768) + 32);
			loopBuffer.dequeue(tmp, 0);
		}
	}
}
