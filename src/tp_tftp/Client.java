package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    
    public static final byte RRQ = 1;
    public static final byte WRQ = 2;
    public static final byte DATA = 3;
    public static final byte ACK = 4;
    public static final byte ERROR = 5;
    
    private static final String IP = "127.0.0.1";
    private static final int port = 75;
    
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress inetAddress = InetAddress.getByName(IP);
        byte [] data = "Hello".getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, port);
        socket.send(packet);
    }
    
    private void sendFile(String filename) {
        // Ouverture fichier local filename
        
        // Si réussi
        // Créer DatagramSocket
        
        // Envoyer packet donnée avec WRQ
        // Recevoir ACK pour confirmation
        // Refaire ces étapes tant qu'on a des données à envoyer
        
        // Fermeture fichier local
    }
    
    private void receiveFile(String filename) {
        // Ouverture fichier filename
        
    }
    
}