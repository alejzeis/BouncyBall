package io.github.jython234.BouncyBall.network;

import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Represents a Client connected to an external server
 */
public class Client {
    @Getter private final ClientManager manager;
    @Getter private final int clientId;

    private Queue<byte[]> outboundQueue = new ArrayDeque<>();
    private DatagramSocket socket;

    @Getter private Server server;
    @Getter private InetSocketAddress clientAddress;

    public Client(Server server, InetSocketAddress clientAddress, ClientManager manager, int clientId) {
        this.manager = manager;
        this.clientAddress = clientAddress;
        this.clientId = clientId;

        try {
            socket = new DatagramSocket(clientId);
            socket.setSoTimeout(1);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
        for(int i = 0; i < 100; i++) {
            try {
                socket.receive(packet);
                manager.getProxy().sendPacket(Arrays.copyOf(packet.getData(), packet.getLength()), clientAddress);
            } catch (SocketTimeoutException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < 100 && !outboundQueue.isEmpty(); i++) {
            byte[] data = outboundQueue.remove();
            try {
                socket.send(new DatagramPacket(data, data.length, server.getAddress()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
