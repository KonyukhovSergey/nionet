package ru.serjik.nionet;

import java.nio.ByteBuffer;

public class BufferReader
{
	private static final int STATE_LENGTH = 1;
	private static final int STATE_DATA = 2;
	private static final int STATE_CHECKSUMM = 3;

	private byte[] data;
	private int size = 0;

	private int xor = 0;
	private int length;
	private int state = STATE_LENGTH;

	public BufferReader(int capacity)
	{
		data = new byte[capacity];
	}

	public byte[] data()
	{
		return data;
	}

	public int read(ByteBuffer buffer)
	{
		while (buffer.remaining() > 0)
		{
			int value = buffer.get();

			switch (state)
			{
			case STATE_LENGTH:
				length = value;
				if (length == 0)
				{
					int result = size;
					size = 0;
					return result;
				}
				else
				{
					state = STATE_DATA;
					xor = 0;
				}
				break;

			case STATE_DATA:
				data[size] = (byte) value;
				size++;
				xor = (xor ^ (int) (value & 0xff));
				length--;
				if (length == 0)
				{
					state = STATE_CHECKSUMM;
				}
				break;

			case STATE_CHECKSUMM:
				if (xor != value)
				{
					System.out.println("error checksumm");
				}
				state = STATE_LENGTH;
				break;
			}
		}

		return 0;
	}
}
