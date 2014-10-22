package ru.serjik.nionet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ClientAcceptor
{
	private ServerSocketChannel serverSocket = ServerSocketChannel.open();

	public ClientAcceptor(int port) throws IOException
	{
		serverSocket.socket().bind(new InetSocketAddress(port));
		serverSocket.configureBlocking(false);
	}

	public SocketChannel accept() throws IOException
	{
		SocketChannel socketChannel = serverSocket.accept();

		if (socketChannel != null)
		{
			socketChannel.configureBlocking(false);
			socketChannel.socket().setTcpNoDelay(true);
			socketChannel.socket().setKeepAlive(true);
			return socketChannel;
		}

		return null;
	}

	public void close() throws IOException
	{
		serverSocket.close();
	}
}
