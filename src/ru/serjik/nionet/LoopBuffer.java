package ru.serjik.nionet;

import java.nio.BufferOverflowException;

public class LoopBuffer
{
	private byte[] buffer;

	private int readPosition = 0;
	private int writePosition = 0;
	private int dequeuePosition = 0;

	public LoopBuffer(int maximumCapacity)
	{
		buffer = new byte[maximumCapacity];
	}

	public void enqueue(byte[] data, int offset, int size)
	{
		if (buffer.length - writePosition < size)
		{
			writePosition = 0;
			
			if (readPosition - writePosition < size)
			{
				throw new BufferOverflowException();
			}
		}
	}

	public int dequeue()
	{
		return 0;
	}

	public int offset()
	{
		return dequeuePosition;
	}

	public byte[] data()
	{
		return buffer;
	}

	private void write(int value)
	{
		buffer[writePosition++] = (byte) (value & 0xff);
		value >>= 8;
		buffer[writePosition++] = (byte) (value & 0xff);
		value >>= 8;
		buffer[writePosition++] = (byte) (value & 0xff);
		value >>= 8;
		buffer[writePosition++] = (byte) value;
	}

	private int readInt()
	{
		int value = (buffer[readPosition + 3] << 24) | (buffer[readPosition + 2] << 16)
				| (buffer[readPosition + 1] << 8) | (buffer[readPosition + 0]);
		readPosition += 4;
		return value;
	}
}
