      /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sunshine
 */
public class TSPClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /******************************/
        /* VERIFICATION DES ARGUMENTS */
        /******************************/
        /* Clair */
        if(args.length != 2 ){
            System.err.println("USAGE : TSPClient Crd_IP Crd_Port :");
            System.err.println("Crd_IP : Cordinateur IPadresse");
            System.err.println("Crd_Port : Cordinateur Port");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[1]);
        InetSocketAddress socketS = new InetSocketAddress(args[0],port);
        /*int port = 59000;
        InetSocketAddress socketS = new InetSocketAddress("localhost",port);*/
        try {
        
            Client handlerW = new Client(socketS);
            handlerW.start();
            
            while(true){  
                
            }
        } catch (Exception ex) {
            Logger.getLogger(TSPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
