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
        
        InetSocketAddress socketS = new InetSocketAddress("localhost",59000);
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
