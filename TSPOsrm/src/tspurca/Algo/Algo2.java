/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tspurca.Algo;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import tspurca.Osmpkg.*;

/**
 *
 * @author sunshine(OldSmile)
 */
public class Algo2 {
    
    public int id;
    public String nameTrack;
    
    public Algo2() {
         
    }
     
    /**
     * Algo alphabitique Oder using mysql query 
     * to get order by name save us a lot of time
     * @param BD
     * @param fileE
     * @param debut
     * @param NbVille
     * @throws IOException
     * @throws SQLException 
     */
    public void CAlphabetTrack(BaseDeDonnee BD, String fileE, int debut, int NbVille) throws IOException, SQLException{
        
        String format = " ORDER BY NomVille";
        
        BD.readBD_CXML_gpx(fileE, debut, NbVille, format);
    }
    
    /**
     * Algo find Nearest select 1 city find nerest ones to her
     * by lat and long add the nearest one to track if not exist 
     * then Select it and and repeat
     * @param from
     * @param distKm
     * @return 
     */
    public String CRequeteNearest(Ville_t from,int distKm){
        String req = "SELECT `Ville`.idV, NomVille,AsText(LatLong), (distm.dist) AS dist " +
                      "FROM `Ville` " +
                      "INNER JOIN ( " +
                        "SELECT idV, ( " +
                        "SQRT( POW( X( `LatLong` ) - " + from.lat + ", 2 ) + POW( Y( `LatLong` ) - "+ from.lon + ", 2 ) ) *100 ) AS dist " +
                        "FROM `Ville`  " +
                     ") distm ON `Ville`.idV = distm.idV " +
                     "WHERE" +
                     "(dist < "+ distKm +" ) AND ( LatLong != POINT("+from.lat+","+from.lon+") ) " +
                        "ORDER BY dist;";
        
        return req;
    }
    public void CNearestTrack(BaseDeDonnee BD, String fileE, int debut, int NbVille) throws IOException, SQLException{
        
        Ville_t v,vdest;
        int distKm = 5,cpt = 0;
        ResultSet rs;
        boolean  found;
        
        /* Select  First city as point of start*/
        Vector<Ville_t> listNP = new Vector<Ville_t>();
        
        String req = "SELECT idV, NomVille,AsText(LatLong),(idV) AS dist " +
                     "FROM `Ville`" +
                     "LIMIT 1;";
     
        rs = BD.RequetteEnvoiBD(req);
        
        if(rs.next()){
            
            v = new  Ville_t(rs.getString("NomVille"), rs.getString("AsText(LatLong)"),rs.getFloat("dist"));
            listNP.addElement(v);
            cpt++;

            while (cpt < NbVille) {    
                
                req = CRequeteNearest(v, distKm);
                rs = BD.RequetteEnvoiBD(req);

                found = false;
                
                if (rs != null) {
                     /* veifie les plus pes */
                    while (rs.next() && !found) {    
                        
                        vdest = new  Ville_t(rs.getString("NomVille"), rs.getString("AsText(LatLong)"),rs.getFloat("dist")); 
                        
                           if(!listNP.contains(vdest)){      
                                
                               listNP.addElement(vdest);
                               v = vdest;  
                               found = true;
                               cpt++;
                            }
                    }
                    
                    /* if not found look in next 5km range*/
                    if (!found) {
                        distKm = distKm + 5; 
                        System.out.println("Neast Ville : pas de ville dans le aion de "+distKm);
                    }
                                            
                    rs.close();
                }    
            }
            
            BD.readVec_CXML_gpx(fileE, listNP);
           
        } else {
           System.out.println("eeu Neast Ville  : Vide le lableau Ville"); 
        }
 
    }/* CNearestTrack*/
    
}
