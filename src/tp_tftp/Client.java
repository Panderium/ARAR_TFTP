package client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    InetAddress inetAddress;
    private static final int PORT = 69;
    
    private DatagramSocket socket;
    private DatagramPacket packet;
    private static final int DATA_SIZE = 512;
    
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.sendFile("fichier.txt");
    }
    
    private void sendFile(String filename) throws FileNotFoundException, IOException {
        byte[] receivedACK;
        // Ouverture fichier local filename
        FileOutputStream file = new FileOutputStream(filename);
        
        // Créer DatagramSocket
        socket = new DatagramSocket();
        
        // Envoyer packet WRQ
        inetAddress = InetAddress.getByName(IP);
        byte[] data = createRequestPacket(WRQ, filename, "octet");
        packet = new DatagramPacket(data, data.length, inetAddress, PORT);
        socket.send(packet);
    
        // Recevoir ACK pour confirmation envoi fichier
        socket.receive(packet);
        receivedACK = packet.getData();
        if(receivedACK[0] == 0 && receivedACK[1] == ACK
                && receivedACK[2] == 0 && receivedACK[3] == 0) {
            
        }
        else {
            // On a pas reçu un ACK
            System.out.println("PAS RECU ACK : " + receivedACK.length + " caractères.");
            for(int i = 0; i < receivedACK.length; i++)
                System.out.println(i + " : " + (int)receivedACK[0]);
            // Renvoyer packet WRQ ?
        }
        
        // Lecture fichier local (test si < DATA_SIZE)
        // Envoyer packet DATA
        // Recevoir packet ACK
        // Refaire ces étapes tant qu'on a des données à envoyer
        
        // Fermeture fichier local
    }
    
    private void receiveFile(String filename) throws FileNotFoundException {
        byte[] ack = new byte[2]; // Pour contenir le numero d'ACK
        // Création fichier local filename
        
        // Créer DatagramSocket
        byte[] data = createRequestPacket(RRQ, filename, "octet");
        
        // Envoyer packet RRQ
        // Réception packet DATA
        // Ecriture données dans fichier local
        // Envoi packet ACK pour confirmation
        // Refaire tant qu'on a des données à recevoir
        
        // Fermeture fichier local
    }
    
    
    // Fonction pour créer le packet ACK
    
    // Fonction pour créer le packet DATA
    
    // Fonction pour créer le packet request RRQ/WRQ
    private byte[] createRequestPacket(final byte code, final String filename, final String mode) {
        int tabSize = 2 + filename.length() + 1 + mode.length() + 1;
        byte[] tab = new byte[tabSize];
        int cursor = 0;
        byte zero = 0;
        
        // CODE
        tab[0] = zero;
        tab[1] = code;
        
        // FILENAME
        for(int i = 0; i < filename.length(); i++) {
            tab[i + 2] = (byte)filename.charAt(i);
            cursor = i + 2;
        }

        // ZERO
        cursor++;
        tab[cursor] = zero;
        cursor++;
    
        // MODE
        for(int i = 0; i < mode.length(); i++) {
            tab[cursor] = (byte)mode.charAt(i);
            cursor++;
        }
        
        // ZERO
        tab[cursor] = zero;
        
        return tab;
    }
}