package ru.serjik.nionet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NioNetClient
{
	public static final int STATE_CONNECTING = 0;
	public static final int STATE_CONNECTED = 1;
	public static final int STATE_DISCONNECTED = 2;

	private ClientData clientData;
	private SocketChannel socket;
	public int state = STATE_CONNECTING;

	private NioNetClientListener clientListener;

	public NioNetClient(String host, int port, NioNetClientListener clientListener)
	{
		this.clientListener = clientListener;

		try
		{
			socket = SocketChannel.open();
			socket.configureBlocking(false);
			socket.socket().setTcpNoDelay(true);
			socket.socket().setKeepAlive(true);
			socket.connect(new InetSocketAddress(host, port));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			state = STATE_DISCONNECTED;
			clientListener.onDisconnect();
		}
	}

	public void tick()
	{
		switch (state)
		{
		case STATE_CONNECTING:
			try
			{
				if (socket.finishConnect())
				{
					clientData = new ClientData(socket);
					state = STATE_CONNECTED;
					clientListener.onConnect();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				state = STATE_DISCONNECTED;
			}
			break;

		case STATE_CONNECTED:
			try
			{
				if (clientData.recv(clientListener) == false)
				{
					state = STATE_DISCONNECTED;
					clientListener.onDisconnect();
					break;
				}
				clientData.send();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				state = STATE_DISCONNECTED;
				clientListener.onDisconnect();
			}
			break;

		case 2:
			break;
		}
	}

	public void send(String message)
	{
		if (state == STATE_CONNECTED)
		{
			clientData.send(message);
		}
	}
}
