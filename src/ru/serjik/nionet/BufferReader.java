package ru.serjik.nionet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BufferReader
{
	private static final int STATE_LENGTH = 1;
	private static final int STATE_DATA = 2;
	private static final int STATE_CHECKSUMM = 3;

	private static final Charset utf8 = Charset.forName("UTF-8");

	private byte[] data;
	private int size = 0;

	private int xor = 0;
	private int blockLength;
	private int state = STATE_LENGTH;

	public BufferReader(int capacity)
	{
		data = new byte[capacity];
	}

	public String read(ByteBuffer buffer)
	{
		while (buffer.remaining() > 0)
		{
			int value = buffer.get();

			switch (state)
			{
			case STATE_LENGTH:
				blockLength = value;
				if (blockLength == 0)
				{
					String result = new String(data, 0, size, utf8);
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
				blockLength--;
				if (blockLength == 0)
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

		return null;
	}

}
