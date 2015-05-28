package ru.serjik.nionet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NioNetClient
{
	public static final int STATE_NONE = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;
	public static final int STATE_DISCONNECTED = 3;

	private ConnectionProvider connectionProvider;
	private SocketChannel socket;
	public int state = STATE_NONE;

	private ConnectionListener connectionListener;
	private String host;
	private int port;

	public NioNetClient(String host, int port, ConnectionListener connectionListener)
	{
		this.connectionListener = connectionListener;
		this.host = host;
		this.port = port;
	}

	public boolean tick()
	{
		switch (state)
		{
		case STATE_NONE:
			try
			{
				socket = SocketChannel.open();
				socket.configureBlocking(false);
				socket.socket().setTcpNoDelay(true);
				socket.socket().setKeepAlive(true);
				socket.connect(new InetSocketAddress(host, port));
				state = STATE_CONNECTING;
			}
			catch (IOException e)
			{
				diconnected();
			}
			break;

		case STATE_CONNECTING:
			try
			{
				if (socket.finishConnect())
				{
					connectionProvider = new ConnectionProvider(socket, connectionListener);
					state = STATE_CONNECTED;
					connectionListener.onConnect(connectionProvider);
				}
			}
			catch (IOException e)
			{
				diconnected();
			}
			break;

		case STATE_CONNECTED:
			try
			{
				connectionProvider.tick();
			}
			catch (IOException e)
			{
				diconnected();
			}
			break;

		case STATE_DISCONNECTED:
			return false;
		}
		return true;
	}

	private void diconnected()
	{
		state = STATE_DISCONNECTED;
		connectionListener.onDisconnect(connectionProvider);
	}

	public void close()
	{
		try
		{
			diconnected();
			socket.close();
		}
		catch (IOException e)
		{
		}
	}
}
