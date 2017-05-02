package tp_tftp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServeurUDP {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, IOException {
        boolean flag = true;
        while(flag){
            try {
            DatagramSocket ds = new DatagramSocket(1596);
            byte [] data = new byte[2000];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            ds.receive(dp);
            System.out.println("Reçu : " + new String(dp.getData()));
            ds.close();
            } catch (SocketException ex) {
                Logger.getLogger(ClientUDP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
}
