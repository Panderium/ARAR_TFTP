package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Observable;

public class Client extends Observable  implements Runnable {

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

    public static final byte ZERO = 0;

    InetAddress inetAddress;

    private DatagramSocket socket;
    private DatagramPacket packet;
    private static final int DATA_SIZE = 512;
    private int port;

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.sendFile("127.0.0.1", 69, "fichier_gros.txt");
        //client.receiveFile("127.0.0.1", 69,"fichier_gros.txt", "grosDL.txt");
    }

    //rajouter une valeur de retourCrEm
    public void sendFile(String serverIP, int serverPort, String filename) throws FileNotFoundException, IOException {
        
        this.port = serverPort;
        
        // Créer DatagramSocket
        socket = new DatagramSocket();
        
        // Envoyer packet WRQ
        inetAddress = InetAddress.getByName(serverIP);
        byte[] data = createRequestPacket(WRQ, filename, "octet");
        packet = new DatagramPacket(data, data.length, inetAddress, serverPort);
        socket.send(packet);

        // Recevoir ACK pour confirmation envoi fichier
        socket.receive(packet);
        byte[] receivedACK = packet.getData();
        serverPort = packet.getPort();

        if (isFirstACK(receivedACK)) {
            FileInputStream file = null;
            byte[] ack = new byte[2];
            // Lecture fichier local
            try {

                file = new FileInputStream(filename);

            } catch (FileNotFoundException e) {
                System.err.println("Erreur ouverture fichier : " + e);
                return;
            }

            ack[0] = ZERO;
            ack[1] = (byte) 1;
            boolean eof = false;
            int nbRead = DATA_SIZE; // Par défaut, si on n'entre pas dans le eof, c'est qu'on a lu 512 char

            // Envoi de données tant qu'on a pas atteint la fin de fichier
            do {
                byte[] dataRead = new byte[DATA_SIZE];

                // Lecture du fichier
                int byteRead = 0;
                for (int i = 0; i < DATA_SIZE && byteRead != -1; i++) {
                    byteRead = file.read();
                    if (byteRead != -1) {
                        dataRead[i] = (byte) byteRead;
                    } else {
                        // Dernier paquet à envoyer
                        eof = true;
                        nbRead = i; // Sert pour créer le tableau final de bytes
                        //System.out.println(i + " caractères lus");
                    }
                }

                // Copie uniquement des bytes nécessaires
                byte[] toSend = Arrays.copyOf(dataRead, nbRead);

                // Envoyer packet DATA
                byte[] datagram = createDataPacket(ack, toSend);
                packet = new DatagramPacket(datagram, datagram.length, inetAddress, serverPort);
                socket.send(packet);

                /*System.out.print("Envoyé : ");
                printBytes(datagram);*/
                // Recevoir packet ACK
                socket.receive(packet);
                byte[] receivedPacket = packet.getData();
                if (isType(receivedPacket, ACK)) { // Si le serveur renvoie un ACK
                    byte[] received = {receivedPacket[2], receivedPacket[3]};
                    System.out.println("ACK RECU : " + receivedPacket[2] + " | " + receivedPacket[3]);
                    addACK(ack, received);
                } else if (isType(receivedPacket, ERROR)) {
                    // On envoie le code d'erreur la fonction explicitant l'erreur
                    manageError(receivedPacket[3]);
                }
            } while (!eof);

            // Fermeture fichier local
            if (file != null) {
                file.close();
            }
        } else if (isType(receivedACK, ERROR)) {
            // On envoie le code d'erreur la fonction explicitant l'erreur
            manageError(receivedACK[3]);
        } else {
            System.err.println("PAS RECU ACK, code reçu : " + receivedACK[1]);
        }
        socket.close();
    }

    public void receiveFile(String serverIP, int serverPort, String filename, String localName) throws FileNotFoundException, SocketException, UnknownHostException, IOException {

        this.port = serverPort;
        
        byte[] ack = new byte[2]; // Pour contenir le numero d'ACK
        socket = new DatagramSocket();
        inetAddress = InetAddress.getByName(serverIP);
        // Créer DatagramSocket
        byte[] rrq = createRequestPacket(RRQ, filename, "octet");
        packet = new DatagramPacket(rrq, rrq.length, inetAddress, serverPort);
        // Envoyer packet RRQ
        socket.send(packet);

        //Crée le fichier local ou écrase si existant
        FileOutputStream localFile = new FileOutputStream(localName);

        //init ack
        ack[0] = ZERO;
        ack[1] = (byte) 1;
        DatagramPacket data;
        byte[] ack2Send;
        do {
            byte[] bufferOutputFile = new byte[516];
            // Réception packet DATA
            data = new DatagramPacket(bufferOutputFile, bufferOutputFile.length);
            socket.receive(data);
            serverPort = data.getPort();

            //écriture des données si le packet est de type DATA
            if (isType(data.getData(), DATA)) {
                for (int i = 4; i < data.getLength(); i++) {
                    localFile.write(data.getData()[i]);
                }
                ack2Send = createACKPacket(ack);
                packet = new DatagramPacket(ack2Send, ack2Send.length, inetAddress, serverPort);
                socket.send(packet);
                
                System.out.println("ACK : " + ack[0] + " | " + ack[1]);
                
                //preparation prochain ack
                ack[1]++;
                if (ack[1] == 0) {
                    ack[0]++;
                }
            } else if (isType(data.getData(), ERROR)) {
                manageError(data.getData()[3]);
            }
        } while (data.getLength() >= 512);

        // Fermeture fichier local
        localFile.close();
    }

    // Fonction pour créer le packet ACK
    private byte[] createACKPacket(byte[] ackNumber) {
        byte[] tab = new byte[4];

        tab[0] = 0;
        tab[1] = ACK;

        tab[2] = ackNumber[0];
        tab[3] = ackNumber[1];

        return tab;
    }

    // Fonction pour créer le packet request RRQ/WRQ
    private byte[] createRequestPacket(final byte code, final String filename, final String mode) {
        int tabSize = 2 + filename.length() + 1 + mode.length() + 1;
        byte[] tab = new byte[tabSize];
        int cursor = 0;

        // CODE
        tab[0] = ZERO;
        tab[1] = code;

        // FILENAME
        for (int i = 0; i < filename.length(); i++) {
            tab[i + 2] = (byte) filename.charAt(i);
            cursor = i + 2;
        }

        // ZERO
        cursor++;
        tab[cursor] = ZERO;
        cursor++;

        // MODE
        for (int i = 0; i < mode.length(); i++) {
            tab[cursor] = (byte) mode.charAt(i);
            cursor++;
        }

        // ZERO
        tab[cursor] = ZERO;

        return tab;
    }

    // Fonction pour créer le packet DATA
    private byte[] createDataPacket(byte[] ack, byte[] data) {
        int tabSize = 2 + 2 + data.length;
        byte[] tab = new byte[tabSize];

        // Code
        tab[0] = ZERO;
        tab[1] = DATA;

        // ACK
        tab[2] = ack[0];
        tab[3] = ack[1];

        // Data à partir de tab[4]
        System.arraycopy(data, 0, tab, 4, data.length);

        return tab;
    }

    // Fonction pour gérer les erreurs
    private void manageError(byte code) {
        System.err.print("ERREUR : ");
        switch (code) {
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

    // Fonction qui ajoute un à l'ACK reçu
    private void addACK(byte[] ack, byte[] received) {
        /*int number = received[0] * 128 + received[1];
        number++;
        ack[0] = (byte) (number / 128);
        ack[1] = (byte) (number % 128);*/
        ack[1] = (byte) (received[1] + 1);
        if (ack[1] == 0) {
            ack[0]++;
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
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
        }
        System.out.println();
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public DatagramPacket getPacket() {
        return packet;
    }

    public void setPacket(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void run() {
       System.out.println("Running");
    }
    
    
}
