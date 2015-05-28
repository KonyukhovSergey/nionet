package ru.serjik.nionet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ConnectionProvider
{
	private ByteBuffer readBuffer;
	private ByteBuffer sendBuffer;
	private BufferReader reader;

	private SocketChannel socket;

	private BufferQueue messages;

	public MessageListener messageListener;

	public ConnectionProvider(SocketChannel socket, int maxMessageSize, int queueSize, MessageListener messageListener)
	{
		this.socket = socket;
		this.messageListener = messageListener;
		readBuffer = ByteBuffer.allocate(maxMessageSize);
		sendBuffer = ByteBuffer.allocate(maxMessageSize);
		reader = new BufferReader(maxMessageSize);
		messages = new BufferQueue(queueSize);
		sendBuffer.limit(0);
	}

	public ConnectionProvider(SocketChannel socket, MessageListener messageListener)
	{
		this(socket, 4096, 4096 * 8, messageListener);
	}

	public void close()
	{
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
		}
	}

	public boolean isOpen()
	{
		return socket.isOpen();
	}

	public void send(byte[] buffer, int offset, int length)
	{
		messages.enqueue(buffer, offset, length);
	}

	public void send(byte[] data, int size)
	{
		messages.enqueue(data, 0, size);
	}

	public void send(byte[] data)
	{
		messages.enqueue(data, 0, data.length);
	}

	public void tick() throws IOException
	{
		recv();
		send();
	}

	private void send() throws IOException
	{
		if (sendBuffer.hasRemaining())
		{
			socket.write(sendBuffer);
		}

		while (sendBuffer.hasRemaining() == false && messages.hasData())
		{
			messages.dequeue(sendBuffer);
			socket.write(sendBuffer);
		}
	}

	private void recv() throws IOException
	{
		readBuffer.clear();

		int count = socket.read(readBuffer);

		if (count == -1)
		{
			socket.close();
			throw new IOException();
		}

		readBuffer.flip();

		if (count > 0)
		{
			readBuffer.limit(count);

			int size;

			while ((size = reader.read(readBuffer)) > 0)
			{
				messageListener.onMessage(this, reader.data(), size);
			}
		}
	}
}
