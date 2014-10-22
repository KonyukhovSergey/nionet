package ru.serjik.nionet;

public interface NioNetServerListener extends MessageListener
{
	void onAccept(ClientData client);
	void onDisconnect(ClientData client);
}
