package client;

import java.io.File;
import java.io.FileInputStream;
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
    
    public static final byte ERROR_UNDEFINED = 0;
    public static final byte ERROR_FILE_NOT_FOUND = 1;
    public static final byte ERROR_VIOLATION = 2;
    public static final byte ERROR_FULL = 3;
    public static final byte ERROR_ILLEGAL = 4;
    public static final byte ERROR_UNKNOWN = 5;
    public static final byte ERROR_FILE_EXISTS = 6;
    public static final byte ERROR_USER = 7;
    
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
        
        // Créer DatagramSocket
        socket = new DatagramSocket();
        
        // Envoyer packet WRQ
        inetAddress = InetAddress.getByName(IP);
        byte[] data = createRequestPacket(WRQ, filename, "octet");
        packet = new DatagramPacket(data, data.length, inetAddress, PORT);
        socket.send(packet);
    
        // Recevoir ACK pour confirmation envoi fichier
        socket.receive(packet);
        byte[] receivedACK = packet.getData();
        if(isFirstACK(receivedACK)) {
            File f;          // Pour connaître la taille ensuite
            FileInputStream file = null;
            int block = 0;
            long fileLength = 0;
            // Lecture fichier local
            try {
            f = new File(filename);
            fileLength = f.length();

            file = new FileInputStream(f);

            } catch(FileNotFoundException e) {
                System.err.println("Erreur ouverture fichier : " + e);
            }
            // Calcul du nombre de paquet à envoyer
            block = (int)fileLength / DATA_SIZE + 1;
            System.out.println("Block : " + block);
            
            do
            {
                // Envoyer packet DATA

                // Recevoir packet ACK

                //if(isType(received, ACK)) // Si le serveur renvoie un ACK
                    block--;
            }while(block > 0);

            // Fermeture fichier local
            file.close();
        }
        else if(isType(receivedACK, ERROR)) {
            // On a pas reçu un ACK
            System.err.print("ERREUR : ");
            // On envoie le code d'erreur la fonction explicitant l'erreur
            manageError(receivedACK[3]);
        }
        else        {
            System.err.println("PAS RECU ACK, code reçu : " + receivedACK[1]);
        }
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
    
    // Fonction pour gérer les erreurs
    private void manageError(byte code) {
        switch(code)
        {
            case ERROR_UNDEFINED:
                System.err.println("Erreur indéfinie");
                break;
            case ERROR_FILE_NOT_FOUND:
                System.err.println("Fichier non trouvé");
                break;
            case ERROR_VIOLATION:
                System.err.println("Violation d'accès");
                break;
            case ERROR_FULL:
                System.err.println("Disque plein ou répartition dépassée");
                break;
            case ERROR_ILLEGAL:
                System.err.println("Opération TFTP illégale");
                break;
            case ERROR_UNKNOWN:
                System.err.println("Transfert ID inconnu");
                break;
            case ERROR_FILE_EXISTS:
                System.err.println("Fichier déjà existant");
                break;
            case ERROR_USER:
                System.err.println("Aucun utilisateur");
                break;
        }
    }
    
    // Fonction qui vérifie si le packet est du type indiqué
    private boolean isType(byte[] packet, byte type) {
        return (packet[0] == 0 && packet[1] == type);
    }
    // Fonction qui vérifie si le packet est de type ACK
    private boolean isFirstACK(byte[] packet) {
        return (packet[0] == 0 && packet[1] == ACK
                && packet[2] == 0 && packet[3] == 0);
    }
    
    // Fonction pour afficher un tableau d'octet (pour tester essentiellement)
    private void printBytes(byte[] array) {
        for(int i = 0; i < array.length; i++)
            System.out.print(array[i]);
    }
}