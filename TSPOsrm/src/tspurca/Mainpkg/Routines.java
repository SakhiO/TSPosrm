/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.jdom2.JDOMException;
import tspurca.Osmpkg.ParsserJson;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */
public class Routines {
    private final Access ac;
    public TimerJob tjBD;
    public TimerJob tjPars;
    
    
    public Routines(Access ac) {
        this.ac = ac;
        this.tjBD = new TimerJob();
        this.tjPars = new TimerJob();
    }
    
        
    
/**
 * 
 * @param fileosm
 * @param nameThr executing the routine
 * @throws IOException
 * @throws SQLException
 * @throws JDOMException
 * @throws Exception 
 */     
    public void initVille(String fileosm, String nameThr) throws IOException, SQLException, JDOMException, Exception{
        
        
        
        String filedata = "./tmp/"+nameThr+"osm.txt";
        
        /* Create tmp file for the Thread work*/
        
        File ExitFile = new File(filedata);
        if(ExitFile.exists()) {
            ExitFile.delete();
        } 
        ExitFile.createNewFile();
       
        this.ac.BD.connectBD();
        
        /* Extract cities from osm to tmp file Then Load to DB */
        this.ac.BD.ParcoursXMLinsertF(fileosm, ExitFile);
        this.ac.BD.RequetteInsertV(filedata);
        this.ac.BD.closeBD();
        
        /* Clean Resources */
        
        /* delete tmp file*/
        ExitFile.delete();
        
    }
    
    
    
    
/**
 * 
 * @throws SQLException
 * @throws InterruptedException
 * @throws Exception 
 */    
    public void initDistance() throws SQLException, InterruptedException, Exception{
        
        Commande cmd = new Commande(""); 
        
        /* init default configuration */
        int tailleRequete = this.ac.config.getTailleReq(); //Nombre de ville total pour l'envoi de la requête
        int tailleM = this.ac.config.getNbVille();
        int Nbtours = (tailleM / tailleRequete);
        
        if ((tailleM % tailleRequete)!= 0) {
            Nbtours++;
        }
 
        /* Produce diagonal jobs */ 
        for (int i = 0; i < Nbtours; i++){
            this.ac.addtoQueue(cmd.getCommandeJobD(i,"O"));
            
        }
        /* Produce Upper jobs */
        for (int i = 0; i < Nbtours-1; i++) {
            for (int j = i+1; j < Nbtours; j++){
                this.ac.addtoQueue(cmd.getCommandeJobU(i,j,"O"));      
                
            }         
        }
        //System.out.println("Produced Jobs To Fill GENERAL UPPER MATRIX done");
        
        /* Clean Resources */
    
    }
    
    
    public void jobVille(String OsmFile){
        
    
    }

    
/**
 * 
 * @param Debut
 * @param nameThr
 * @throws IOException
 * @throws SQLException
 * @throws Exception 
 */
    public void jobDiagonal(int Debut, String nameThr) throws IOException, SQLException, Exception{
          
        String filedata = "./tmp/"+nameThr+Debut+"D.txt";
        
        /* Create tmp file for the Thread */
        
        File ExitFile = new File(filedata);
        if(ExitFile.exists()) {
            ExitFile.delete();
        } 
        ExitFile.createNewFile();
        
        
        
        /* init default configuration */
        /* Nombre de ville total pour l'envoi de la requête */
        int tailleRequete = this.ac.config.getTailleReq();
        int start = Debut * tailleRequete;
        ParsserJson Pars = new ParsserJson(this.ac.config);
        
        /* Prepare the query for Osrm case Diagonal param== -1 for all cities*/
        String queryOSRM = this.ac.BD.RequetteOSRMBD(start, tailleRequete, -1);
            
        if ((!queryOSRM.equals(""))) {
            
           /* send query to osrm */
           Pars.EnvoieRequette(queryOSRM);
           /* pars responce to Exitfile */
           Pars.TableauDistanceOSRMRepD(start, ExitFile);
           /* insert to Table distance*/
           this.ac.BD.RequetteInsertDistt(filedata);
        }
        else
            throw new Exception("Erreur Query result from DB is Empty");
        
        //this.tjBD.addTime(this.ac.BD.tj);
        this.tjPars.addTime(Pars.tj);
        
        /* Clean Resources */ 
        
        ExitFile.delete();
        
    }
    
    

/**
 * 
 * @param n
 * @param m
 * @param nameThr
 * @throws IOException
 * @throws Exception 
 */    
    public void jobUpper(int n, int m, String nameThr) throws IOException, Exception{
        String filedata = "./tmp/"+nameThr+n+m+"U.txt";
        
        /* Create tmp file for the Thread */
        
        File ExitFile = new File(filedata);
        if(ExitFile.exists()) {
            ExitFile.delete();
        } 
        ExitFile.createNewFile();
        
        /**
         * init default configuration
         * tailleRequete : 
         * startS : for sources
         * startD : for destinations
         */
        int tailleRequete = this.ac.config.getTailleReq();
        int tailleReqS2 = tailleRequete / 2;
        
       
        ParsserJson Pars = new ParsserJson(this.ac.config);
        
        /* Prepare the query for Osrm case Diagonal param== -1 for all cities*/
        /* get Sub table cities*/
        int startS = n * tailleReqS2;
        int startD;
        for (int i = 0; i < 2; i++) {
            
            String querySrc = this.ac.BD.RequetteOSRMBD(startS, tailleReqS2, 0);
            startD =m * tailleReqS2;
            for (int j = 0; j < 2; j++) {
                String queryDest = this.ac.BD.RequetteOSRMBD(startD, tailleReqS2, tailleReqS2);
                
                if (!queryDest.equals("")) {
                    String queryOSRM = Pars.assemble2Query(querySrc, queryDest);
                    /* send query to osrm */
                    Pars.EnvoieRequette(queryOSRM);
                    /* pars responce to Exitfile */
                    Pars.TableauDistanceOSRMRepU(startS, startD, ExitFile);
                    
                }
                        
                startD += tailleReqS2; 
            }   
            startS +=tailleReqS2;
        }
        /* insert to Table distance*/
        this.ac.BD.RequetteInsertDistt(filedata);
        //this.tjBD.addTime(this.ac.BD.tj);
        this.tjPars.addTime(Pars.tj);
        
        /* Clean Resources */ 
        
        ExitFile.delete();

    }

}
