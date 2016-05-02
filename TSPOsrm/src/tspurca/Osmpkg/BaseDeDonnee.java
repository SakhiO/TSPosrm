/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Osmpkg;

import tspurca.Tools.Config;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */
public class BaseDeDonnee {
    public final String dburl;
    private Connection con ;
    public TimerJob tj = new TimerJob();

    
/**
 * 
 * @param db
 * @param user
 * @param pwd 
 */    
    public BaseDeDonnee(String db, String user, String pwd) {
        this.dburl = "jdbc:mysql://"+db+"?" +
                                   "user="+user+"&password="+pwd;
    }
    
    public BaseDeDonnee(String dburl) {
        this.dburl = dburl;
    }
 
    
/**
 * 
 * @param input
 * @return 
 */    
    public static String normalize(String input) {
         return Normalizer.normalize(input, Normalizer.Form.NFD);
    }


/**
 * 
 * @throws Exception 
 */
    public void connectBD() throws Exception{
            
            try {

              Class.forName("com.mysql.jdbc.Driver").newInstance();

              this.con = DriverManager.getConnection(this.dburl);

            } catch (Exception ex) {
                throw new Exception("Erreur Connect to data Base", ex);
            }
    }


/**
 * 
* @throws java.lang.Exception
 */
    public void closeBD() throws Exception{

        try {
            this.con.close();            
        }
        catch (SQLException e) {
            throw new Exception("Erreur in deconect from DB ",e);

        }  
    }


/**
 * 
 * @param tab
 * @throws Exception 
 */
    public void truncateTable(String tab) throws Exception{

        String requete = "TRUNCATE TABLE " + tab;
        TimerJob tmp = new TimerJob();
        try {

            Statement stmt = this.con.createStatement();
            tmp.start();
            stmt.executeUpdate(requete);
            tmp.stop();
            /* add to general time job */
            this.tj.addTime(tmp);
        }
        catch (SQLException e){
            throw new Exception("Erreur TRUNCATE TABLE ",e);
        } 

    } 

/**
 * 
 * @param requete
 * @throws Exception 
 */
    public void RequetteInsertBD(String requete) throws Exception{

        
        try {
            TimerJob tmp = new TimerJob();
            
            Statement stmt = this.con.createStatement();

            tmp.start();
            int nbMaj = stmt.executeUpdate(requete);
            tmp.stop();
            
            /* add to general time job */
            this.tj.addTime(tmp);
            
        }
        catch (SQLException e){
            throw new Exception("Erreur Lors Insert "+ e.getMessage());
        }    

    }

/**
 * 
 * @param reqSqlV
 * @throws Exception 
 */
    public void RequetteInsertDist(String reqSqlV) throws Exception{

        String requete  = "INSERT INTO distance (idT, Vdepart, Vdest, Temps) VALUES "+reqSqlV+";";

        RequetteInsertBD(requete);    

    }
    
    public void RequetteInsertDistt(String filedata) throws Exception{

        String requete  = "LOAD DATA LOCAL INFILE '" + filedata
                + "' INTO TABLE distance"
                + " FIELDS TERMINATED BY ';'"
                + " LINES TERMINATED BY '\r\n'"
                + " (idT, Vdepart, Vdest, Temps); ";
        
        RequetteInsertBD(requete);    

    }

/**
 * 
 * 
 * @param reqSqlV
 * @throws Exception 
 */
    public void RequetteInsert(String reqSqlV) throws Exception{
        String requete  = "INSERT INTO Ville (idV,NomVille,LatLong) VALUES "+reqSqlV;

        RequetteInsertBD(requete);    

    }


/**
 * Insert to Table Ville from file 
 * @param filedata
 * @throws Exception 
 */
    public void RequetteInsertV(String filedata) throws Exception{       
        String requete  = "LOAD DATA LOCAL INFILE '" + filedata
                + "' INTO TABLE Ville"
                + " FIELDS TERMINATED BY ';'"
                + " LINES TERMINATED BY '\r\n'"
                + " (idV,NomVille,@long,@lat) "
                + " SET LatLong := POINT(@long,@lat);";

        RequetteInsertBD(requete);   
    }


/**
 * 
 * @return 
 */
    public float GetTimeEcoule(){

        return (this.tj.getTmpecouler()/this.tj.getCmptrequete());
    }


/* fo ALgo neast */
/**
 * 
 * @param req
 * @return
 * @throws SQLException 
 */
    public ResultSet RequetteEnvoiBD(String req) throws SQLException{
        
        ResultSet resultats = null;
        String requete = req;
        TimerJob tmp = new TimerJob();
        
        Statement stmt = this.con.createStatement();

        tmp.start();                
        resultats = stmt.executeQuery(requete);
        tmp.stop();

        /* add to general time job */
        this.tj.addTime(tmp);

        return  resultats;
    }


/**
 * 
 * @param DEBUT
 * @param FIN
 * @param NbVille
 * @param format
 * @return
 * @throws SQLException 
 */
    public ResultSet RequetteSelectionVF(int DEBUT ,int FIN, int NbVille, String format) throws SQLException{

        String requete = "SELECT NomVille, AsText(LatLong) FROM Ville WHERE Ville.idV BETWEEN " + String.valueOf(DEBUT) +" AND "+String.valueOf(FIN)+format;

        return  RequetteEnvoiBD(requete);
    }


/**
 * get cities from  debut to debut + NbVille
 * @param debut
 * @param NbVille
 * @return
 * @throws SQLException 
 */
    public ResultSet RequetteSelectionV(int debut, int NbVille) throws SQLException{
    
        String requete = "SELECT NomVille, AsText(LatLong) FROM Ville  ORDER BY Ville.idV LIMIT "+ String.valueOf(debut) +", "+String.valueOf(NbVille)+";";

        return  RequetteEnvoiBD(requete);           
    }

/**
 * 
 * @param idC
 * @return
 * @throws SQLException
 * @throws Exception 
 */
    public Config GetConfig(int idC) throws SQLException, Exception{      

        Config config;

        String requete = "SELECT *"
                + " FROM `config`,(SELECT (COUNT(*)) AS NbVille FROM `Ville`) AS countV"
                + " WHERE `config`.idC = "+idC+" ;";

           ResultSet rs = RequetteEnvoiBD(requete);

           if(rs.next()){
               config = new Config(rs.getString("urlOsrm"),rs.getInt("tReq"),rs.getInt("NbVille"));        
           }
           else {
               rs.close();
               throw new Exception("Erreur get config "+requete);   
           }


           rs.close();

       return config; 
    }


/**
 * 
 * @param rs
 * @throws SQLException 
 */
    public void ParcourDonnee(ResultSet rs) throws SQLException{ //parcours des données retournées

            while (rs.next()) {
                Ville_t v = new  Ville_t(rs.getString("NomVille"), rs.getString("AsText(LatLong)")); 
                System.out.println("READ : "+v.NomVille+" "+ v.lat+" "+ v.lon);                    

            }

            rs.close();          
     }


      
/**
 * Prepare query for osrm 
 * @param debut point of start
 * @param NbVille until debut + NbVille -1
 * @param paramid parameter for sources or destinations query
 * @return Longitude and latitude list of cities
 * @throws SQLException 
 */
    public String RequetteOSRMBD(int debut, int NbVille, int paramid) throws SQLException{

        String myUrl = "";
        String param = "@";/* @ to seperate point from paramters*/
        int id;
        
        ResultSet rs = RequetteSelectionV(debut, NbVille);
        id =id = paramid;
         Ville_t v ;
                 
        while (rs.next() && !rs.isLast()) {

            v = new  Ville_t(rs.getString("NomVille"), rs.getString("AsText(LatLong)"),0); 
           
                myUrl = myUrl + v.lon + "," + v.lat + ";";
                param += String.valueOf(id) + ";";
            
            id++;
        }
        v = new  Ville_t(rs.getString("NomVille"), rs.getString("AsText(LatLong)"),0);
        myUrl = myUrl + v.lon + "," + v.lat;
        param += String.valueOf(id);
                
        rs.close();
        
        /* all are source and destination param = "" */
        if(paramid == -1){
            return myUrl;
        }
        /* else as defined */
            return myUrl+param;
            
    }
    
    
/**
 * 
 * @param debut
 * @param NbVille
 * @throws SQLException 
 */      
    public void AfficheDBVille(int debut, int NbVille) throws SQLException{

        ResultSet rs = RequetteSelectionV(debut, NbVille);
        if (rs!=null) {
            ParcourDonnee(rs);
        } else {
            System.err.println("eeu no esult ");
        }    

    }
 
    
/**
 * 
 * @param req
 * @throws SQLException 
 */ 
    public void RequetteInsertDistance(String req) throws SQLException{

        String requete  = "INSERT INTO distance (idT, Vdepart, Vdest, Temps) VALUES "+ req +";";

        Statement stmt = this.con.createStatement();
        int nbMaj = stmt.executeUpdate(requete);
        System.out.println("nbMaj: " +nbMaj);

    }

    
/* Parcourir osm file and insert it to the tmp file 
 *  that will be loaded to the database 
 */
/**
 * 
 * @param NomFicher
 * @param ExitFile
 * @throws IOException
 * @throws SQLException
 * @throws JDOMException 
 */
 public void ParcoursXMLinsertF(String NomFicher,File ExitFile) throws IOException, SQLException, JDOMException{
        
        
        List ListeNode;
        Iterator k;
        String NomVille = null;
        String lon ,lat;  
        Document document = null;
        Element racine; 
       
        
        //On crée une instance de SAXBuilder
        SAXBuilder sxb = new SAXBuilder();

        //On récuppere un document JDOM avec en argument le fichier XML
        //Le parsing est terminé ;)
        document = sxb.build(new File(NomFicher));
   
        
        /* open stream for exit file */
  
            //On initialise l'élément racine avec l'élément racine du document.
            if (document !=null){
                racine = document.getRootElement();
                Element node = racine.getChild("node");

                //Parcourir le fichier xml et enregistrement dans un fichier.
                ListeNode = racine.getChildren("node");
                k = ListeNode.iterator();
                
                int NbVille = 0;
                
                
                /* extract all nodes to file (ville)*/
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(ExitFile), "utf-8"))) {
                    
                    while(k.hasNext())
                    {
                        node = (Element)k.next();
                        lat = node.getAttributeValue("lat");
                        lon = node.getAttributeValue("lon");
                        Element tag = node.getChild("tag");

                        if(tag.getAttributeValue("k").equals("name")){
                            /* seperate special symbols from carecters then remove them */
                            NomVille = normalize(tag.getAttributeValue("v"));
                            NomVille = NomVille.replaceAll("[^\\p{ASCII}]", "").replaceAll("[- ']", "_");
                        }

                        //System.out.println("mySql : " + reqSqlV + "NbVille : " + NbVille);
                        /* add row to file*/
                        writer.append(NbVille+";"+NomVille+";"+lon+";"+lat+"\r\n");

                        NbVille++; 
                    }               
   
                    writer.flush();
                    writer.close(); 
                    
               }  /* end of try open stream to exit file */       
                       
        }/* end of if get root element*/ 
 }

 
 
 
    /* Exit File gpx */
/**
 * 
 * @param file
 * @param debut
 * @param NbVille
 * @param format
 * @throws IOException
 * @throws SQLException 
 */      
    public void readBD_CXML_gpx(String file, int debut, int NbVille, String format) throws IOException, SQLException{
        int cmpt = 0; 
        int fin = debut + NbVille - 1;
        
        File ExitFile = new File(file);
        if(!ExitFile.exists()) {
            ExitFile.createNewFile();
        } 
        
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"SAKHI-OUSSAMA\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
        String name = "\n<trk>\n<trkseg>\n";
        String segments = "";
        String footer = "\n</trkseg>\n</trk></gpx>";
        
        
        if(this.con!= null){
            try {
                ResultSet rs = RequetteSelectionVF(debut, fin, NbVille, format);
                
                if (rs!=null) {
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file), "utf-8"))) {  
                        int i = NbVille;
                        while (rs.next()) {  
                  
                            Ville_t v = new  Ville_t(rs.getString("NomVille"), rs.getString("AsText(LatLong)"),0); 
                            segments += "<trkpt lat=\"" + v.lat + "\" lon=\"" +v.lon + "\"/>\n";
           
                            // System.out.println()
                        }
                        
                        writer.append(header);
                        writer.append(name);
                        writer.append(segments);
                        writer.append(footer);
                        writer.flush();
                        writer.close();
                        
                    }   
              
                    rs.close();
                } 
                else {
                    System.err.println("eeu no esult ");
                    System.exit(0);
                }    
               
            } catch (SQLException ex) {
                Logger.getLogger(BaseDeDonnee.class.getName()).log(Level.SEVERE, null, ex);
            }  
        }
        else{
           System.err.println("eeu not connected ");
           System.exit(0);
        }    
   
    }


    
/**
 * 
 * @param file
 * @param vec
 * @throws IOException
 * @throws SQLException 
 */
public void readVec_CXML_gpx(String file, Vector<Ville_t> vec) throws IOException, SQLException{
        
        Ville_t v;
        
        File ExitFile = new File(file);
        if(!ExitFile.exists()) {
            ExitFile.createNewFile();
        } 
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"SAKHI-OUSSAMA\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
        String name = "\n<trk>\n<trkseg>\n";
        String segments = "";
        String footer = "\n</trkseg>\n</trk></gpx>";
        
       
               
                
                
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(file), "utf-8"))) {  
      
                        Iterator it = vec.iterator();
                        while (it.hasNext()) {
                            
                            v = (Ville_t) it.next();
                            segments += "<trkpt lat=\"" + v.lat + "\" lon=\"" +v.lon + "\"/>\n";
           
                            // System.out.println()
                        }
                        
                        writer.append(header);
                        writer.append(name);
                        writer.append(segments);
                        writer.append(footer);
                        writer.flush();
                        writer.close();
                        
                    }   
              
                       
        
   
    }
    
}
