package tp_tftp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientUDP {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, IOException {
        
        try {
            DatagramSocket ds = new DatagramSocket();
            InetAddress ia = InetAddress.getByName("127.0.0.1");
            byte [] data = "Hello".getBytes();
            DatagramPacket dp = new DatagramPacket(data, data.length, ia, 1596);
            ds.send(dp);
           /*DatagramPacket dsr = new DatagramPacket(new byte[1024],1024);
           ds.receive(dsr);
           System.out.println("Reçu : " + new String(dp.getData()));*/
            //ds.close();
        } catch (SocketException ex) {
            Logger.getLogger(ServeurUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
