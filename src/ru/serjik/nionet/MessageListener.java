package ru.serjik.nionet;

public interface MessageListener
{
	void onMessage(ConnectionProvider client, final byte[] data, int size);
}
