package ru.serjik.nionet;

import java.nio.ByteBuffer;

public class BufferWriter
{
	public static final int write(ByteBuffer buffer, byte[] data, int offset, int size)
	{
		int position = 0;
		int xor;

		buffer.clear();

		while (position < size)
		{
			int length = size - position;

			if (length > 255)
			{
				length = 255;
			}

			buffer.put((byte) length);

			xor = 0;

			while (length > 0)
			{
				buffer.put(data[position + offset]);
				xor = (xor ^ (int) (data[position + offset] & 0xff));

				position++;
				length--;
			}

			buffer.put((byte) xor);
		}

		buffer.put((byte) 0);
		buffer.limit(buffer.position());
		buffer.position(0);

		return buffer.position();

	}
}
