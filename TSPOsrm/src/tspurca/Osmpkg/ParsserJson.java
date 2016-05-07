/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Osmpkg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import tspurca.Tools.Config;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */
public class ParsserJson {
    
    public String urlosrm;
    public int tailleReq;
    public int NbVille;
    private String result;    
    public TimerJob tj = new TimerJob();

    public ParsserJson(Config config) {
        
        this.urlosrm = config.getUrlOsrm();
        this.tailleReq = config.getTailleReq();
        this.NbVille = config.getNbVille();

    }
    
public String assemble2Query(String src, String dest){
    
    String query="";
    
    String[] tmps = src.split("@");
    String[] tmpd = dest.split("@");
    
    if((!tmps[0].equals(""))&&(!tmpd[0].equals("")))
        query = tmps[0]+";"+tmpd[0]+"?sources="+tmps[1]+"&destinations="+tmpd[1];
    
    return query;
}    
    
/**
 * Send query to osrm defined and put it to result
 * @param myurl
 * @throws MalformedURLException
 * @throws IOException 
 */    
    public void EnvoieRequette(String myurl) throws Exception {
        try {            
            myurl = this.urlosrm + myurl;
            URL url = new URL(myurl);
            TimerJob tmp = new TimerJob();
            
            tmp.start();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            tmp.stop();
            
            this.tj.addTime(tmp);
            /*
            * InputStreamOperations est une classe complémentaire:
            * Elle contient une méthode InputStreamToString.
            */
            result = InputStreamOperations.InputStreamToString(inputStream);
        } catch (Exception ex) {
            throw new Exception(" Erreur in Sending Request Osrm", ex);
        }
          
       
    }

    

/**
 * 
 * @param debut
 * @param writer
 * @throws Exception 
 */
public void TableauDistanceOSRMRepD(int debut, Writer writer) throws  Exception{
    
    
    int indep,inda;
    long id=0;
    
    
        
         JSONObject jsonObject = new JSONObject(this.result);
         JSONArray distance = jsonObject.getJSONArray("durations");
         
         
         for (int src = 0; src< distance.length(); src++) {
            /* get table duration src(l) to destinations */
            JSONArray attribut = distance.getJSONArray(src);
            
            for (int dest = src; dest< attribut.length(); dest++){
                
                //récupération des chiffre du tableau
                long dis = attribut.getInt(dest);

                indep =  debut + src;
                inda =  debut + dest;
                id = ((this.NbVille* indep)+(inda));
                
                writer.append(id+";"+indep+";"+inda+";"+dis+"\r\n");

            }

        }

 }

public void TableauDistanceOSRMRepU(int debutS,int debutD, Writer writer) throws JSONException, IOException{
    
    
    int indep,inda;
    long id=0;

         JSONObject jsonObject = new JSONObject(this.result);
         JSONArray distance = jsonObject.getJSONArray("durations");
         
         
         for (int src = 0; src< distance.length(); src++) {
            /* get table duration src(l) to destinations */
            JSONArray attribut = distance.getJSONArray(src);
            
            for (int dest = 0; dest< attribut.length(); dest++){
                
                //récupération des chiffre du tableau
                long dis = TimeUnit.SECONDS.toMinutes(attribut.getInt(dest));

                indep =  debutS + src;
                inda =  debutD + dest;
                id = ((this.NbVille* indep)+(inda));
                
                writer.append(id+";"+indep+";"+inda+";"+dis+"\r\n");

            }

        }
 }
    
}
