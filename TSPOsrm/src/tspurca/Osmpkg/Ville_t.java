/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Osmpkg;

/**
 *
 * @author sunshine
 */
public class Ville_t {
     public String NomVille ; 
     public String lat;
     public String lon ;
     public float dist ;
     
     public Ville_t(String NomVille ,String PtLatLon,float dist) {
         
        String s = PtLatLon.substring(PtLatLon.indexOf('(') + 1,PtLatLon.indexOf(')') - 1);
                        
        this.NomVille = NomVille;   
        
        this.lon = s.substring(0,s.indexOf(' ') - 1); 
        /* to change inverse for V2 when you load new data */
        this.lat = s.substring(s.indexOf(' ') + 1); 
        
        this.dist = dist;
         
    }
     
    public Ville_t(String NomVille ,String PtLatLon) {
                      
        this(NomVille, PtLatLon, 0);    
         
    } 
     
    public Ville_t(String NomVille ,String lat ,String lon ) {
         this.NomVille= NomVille;   this.lat= lat; this.lon= lon;this.dist= 0;  
         
    }
     
         
        /* oveide equal */

     @Override
     public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
     }


    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (!Ville_t.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Ville_t v = (Ville_t) obj;
         /* (this.NomVille.equals(v.NomVille))  */
         return (this.lat.equals(v.lat)) && (this.lon.equals(v.lon));
    } 
}
