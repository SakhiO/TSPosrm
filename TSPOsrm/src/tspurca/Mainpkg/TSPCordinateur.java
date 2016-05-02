/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import static java.lang.Thread.sleep;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tspurca.Osmpkg.BaseDeDonnee;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */
public class TSPCordinateur {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /** initialistation */
        Server coordinateur;
        Access ac;
        TimerJob total = new TimerJob();
        
        /* init base donnee user et mot de passe */
        String db = "192.168.1.11:3306/maindb";
        String user = "osm";
        String pwd = "Info0606";
        String fileosm = "./data/NodesChampagnef.osm";
        int idC = 2;
        int portS = 59000;
        Commande cmd = new Commande();
        BaseDeDonnee BD = new BaseDeDonnee(db,user,pwd);
        InetSocketAddress socketS = new InetSocketAddress("192.168.1.42",59000);
        
        try {
            ac = new Access(BD.dburl, idC, 1024, socketS);
            coordinateur = new Server(ac, idC, 2);
        
            
            total.start();
            
            coordinateur.start();

            Job jb = new Job(cmd.getCommandeInitdistance(), "coordinateur", ac);
            
            

            while(!ac.isQueueEmpty()){  
                sleep(5000);
            }
            coordinateur.dostop();
            total.stop();
            System.out.println(coordinateur.TimeDB.toString()+"\n"+coordinateur.TimePars.toString());
            System.out.println(total.toString());
            
        } catch (Exception ex) {
            Logger.getLogger(TSPCordinateur.class.getName()).log(Level.SEVERE, null, ex);
        }    

    }
        
    
}
