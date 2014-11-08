package ru.serjik.nionet;

public interface ConnectionListener extends MessageListener
{
	void onConnect(ConnectionProvider client);

	void onDisconnect(ConnectionProvider client);
}
