package ru.serjik.nionet;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class BufferQueue
{
	private byte[] buffer;

	private int readed = 0;
	private int writed = 0;
	private int used = 0;

	public BufferQueue(int maximumCapacity)
	{
		buffer = new byte[maximumCapacity];
	}

	public void enqueue(byte[] data, int offset, int length)
	{
		write(length);
		ensureWrite(length);
		System.arraycopy(data, offset, buffer, writed, length);
		writed += length;
	}

	// public void enqueue(byte[] data, int length)
	// {
	// enqueue(data, 0, length);
	// }
	//
	// public void enqueue(byte[] data)
	// {
	// enqueue(data, 0, data.length);
	// }

	public int dequeue(byte[] buffer, int offset)
	{
		int length = readInt();
		ensureRead(length);
		System.arraycopy(this.buffer, readed, buffer, offset, length);
		readed += length;
		return length;
	}

	public void dequeue(ByteBuffer buffer)
	{
		int length = readInt();
		ensureRead(length);
		BufferWriter.write(buffer, this.buffer, readed, length);
		readed += length;
	}

	public int dequeue(byte[] buffer)
	{
		return dequeue(buffer, 0);
	}

	public byte[] dequeue()
	{
		int length = readInt();
		ensureRead(length);
		byte[] data = new byte[length];
		System.arraycopy(this.buffer, readed, data, 0, length);
		readed += length;
		return data;
	}

	private void write(int value)
	{
		ensureWrite(4);
		buffer[writed++] = (byte) (value & 0xff);
		value >>= 8;
		buffer[writed++] = (byte) (value & 0xff);
		value >>= 8;
		buffer[writed++] = (byte) (value & 0xff);
		value >>= 8;
		buffer[writed++] = (byte) value;
	}

	private int readInt()
	{
		ensureRead(4);
		int value = (buffer[readed + 3] & 0xFF) << 24 | (buffer[readed + 2] & 0xFF) << 16
				| (buffer[readed + 1] & 0xFF) << 8 | (buffer[readed + 0] & 0xFF);
		readed += 4;
		return value;
	}

	private void ensureWrite(int length)
	{
		if (writed + length > buffer.length)
		{
			used += buffer.length - writed;
			writed = 0;
		}

		if (used + length > buffer.length)
		{
			throw new BufferOverflowException();
		}

		used += length;
	}

	private void ensureRead(int length)
	{
		if (readed + length > buffer.length)
		{
			used -= buffer.length - readed;
			readed = 0;
		}

		if (used < length)
		{
			throw new BufferUnderflowException();
		}
		used -= length;
	}

	public boolean hasData()
	{
		return used > 0;
	}
}
