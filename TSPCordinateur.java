/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import static java.lang.Thread.sleep;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import tspurca.Osmpkg.BaseDeDonnee;
import tspurca.Tools.TimerJob;

/**
 * TSPCordinateur  
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
         /******************************/
        /* VERIFICATION DES ARGUMENTS */
        /******************************/
        /* Clair */
        if(args.length != 5 && args.length != 6){
            System.err.println("USAGE : TSPCordinateur C_Port BD_url BD_USER idC [BD_MDP] [NOM_FOSM]:");
            System.err.println("C_Port : Cordinateur Port d'écoute");
            System.err.println("BD_url : (IPadresse/nom )de la base de données");
            System.err.println("BD_USER : nom d'utilisateur pour la base de données"); 
            System.err.println("BD_MDP : mot de passe pour la base de données");
            System.err.println("idC : numero id de configuration pour osrm ");
            System.err.println("NOM_FOSM : nom fichier osm ppour initialiser les villes(argument optionnel)");
            System.exit(-1);
        }
        
        /* init base donnee user et mot de passe */
                
        
        
        int portS = Integer.parseInt(args[0]);
        String db = args[1];
        String user = args[2];
        String pwd = args[3];
        int idC = Integer.parseInt(args[4]);
        String fileosm ="";
        if(args.length != 6)
            fileosm = args[5];
        
        /**/
        /*int portS = 59000;
        String db ="cresticloud.univ-reims.fr:3306/maindb";
        String user = "root";
        String pwd = "";
        int idC = 1;
        String fileosm ="C:\\Users\\sakhi003\\Documents\\Info0606_OSM_2015-2016_v2\\Info0606_OSM_2015-2016_v2\\Code\\NodesChampagnef.osm";*/
        
        /**/
        Commande cmd = new Commande();
        BaseDeDonnee BD = new BaseDeDonnee(db,user,pwd);
        InetSocketAddress socketS = new InetSocketAddress("localhost",portS);
        Job jb;
        
        try {
            ac = new Access(BD.dburl, idC, 1024, socketS);
            coordinateur = new Server(ac, idC, 5);
        
            
            total.start();
            
            coordinateur.start();
           
            jb = new Job(cmd.getCommandeInitVille(fileosm), "coordinateur", ac);
            jb.execute();
            /*
            jb = new Job(cmd.getCommandeInitdistance(), "coordinateur", ac);
            jb.execute();*/
           
             while(!ac.isQueueEmpty() || coordinateur.getNbrJobs() > 0){  
                sleep(5000);
            }        
    
            coordinateur.dostop();
            total.stop();
            System.out.println(total.toString());
           
        } catch (Exception ex) {
            Logger.getLogger(TSPCordinateur.class.getName()).log(Level.SEVERE, null, ex);
        }    

    }
        
    
}
