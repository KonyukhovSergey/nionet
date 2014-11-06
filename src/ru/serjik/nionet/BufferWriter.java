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
			int blockLength = size - position;

			if (blockLength > 255)
			{
				blockLength = 255;
			}

			buffer.put((byte) blockLength);

			xor = 0;

			while (blockLength > 0)
			{
				buffer.put(data[position + offset]);
				xor = (xor ^ (int) (data[position + offset] & 0xff));

				position++;
				blockLength--;
			}

			buffer.put((byte) xor);
		}

		buffer.put((byte) 0);
		buffer.limit(buffer.position());
		buffer.position(0);

		return buffer.position();

	}
}
