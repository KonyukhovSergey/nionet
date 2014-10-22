package ru.serjik.nionet;

public interface NioNetClientListener extends MessageListener
{
	void onConnect();
	void onDisconnect();
}
