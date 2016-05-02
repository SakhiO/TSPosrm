/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */


public class Commande {
    
   public int idcmd;
   public int routine; /* D : diagonal / U : upper matrix / V : Ville*/
   public int n;
   public int m;
   public String filename;
   
   
   public Commande(String cmd){
        String s[] = cmd.split(",");
        switch(s[0]){
            case "init":
                       switch(s[1]){
                           case "ville":
                                        this.idcmd = 0;
                                        this.filename = s[2];
                                        break;
                           case "distance":
                                        this.idcmd = 1;
                                        break; 
                           default : 
                                    this.idcmd = -1;
                       }
                       break;
                
            case "job":
                       this.idcmd = 2;
                       switch(s[1]){
                           case "V":
                                        this.routine = 0; 
                                        break;
                           case "D":
                                        this.routine = 1;
                                        /*start at n*/
                                        this.n = Integer.parseInt(s[2]);
                                        break;
                           case "U":
                                        this.routine = 2;
                                        /* (n m) coordinates of sub tabels  */
                                        this.n = Integer.parseInt(s[2]);
                                        this.m = Integer.parseInt(s[3]);
                                        break;
                               
                           default : 
                                    this.idcmd = -1;
                       }
                       break;
           default : 
                   this.idcmd = -1;
        }
   
    }

    public Commande() {
        this.idcmd = -1;
    }

    @Override
    public String toString() {
        return "cmd : "+this.idcmd+" routine :"+this.routine; 
    }
    
/*********  
 *                  commands and requests 
 *          between handler and his workers
 */
    
    /**
     * fill table Ville with cities Name Longitude and latitude 
     * @param fileosm  file preprocessed with osmosis and osmfilter
     * @return 
     */
    public String getCommandeInitVille(String fileosm){
        return "init,ville,"+fileosm;
    }
    
    public String getCommandeInitdistance(){
        return "init,distance";
    }

    public String getCommandeJobD(int start, String stat){
        return "job,D,"+String.valueOf(start)+",0,"+stat;
    }
    
    public String getCommandeJobU(int n, int m, String stat){
        return "job,U,"+String.valueOf(n)+","+String.valueOf(m)+","+stat;
    }

    
    
/*********  
 *           Network commands and requests 
 *          between server and his worker handlers
 */
    
                /** Request */

    
    /**
     *  RequestConnect
     * @return 
     */
    public String getRequestConnect(){
        return "CONNECT@";
    }
    
    
    /**
     *  RequestJob
     * @return 
     */
    public String getRequestJob(){
        return "JOB@";
    }    
    
    /**
     *  send as notice put dont wait on response
     * @param tdb time execute request to database
     * @param tpars time response from osrm server
     * @return 
     */
    public String getReporttLog(TimerJob tdb, TimerJob tpars){
        return "LOG@"+tdb.toString()+"&"+tpars.toString();
    }
    
    /**
     * in case worker faild to do job
     * @param cmd
     * @return 
     */
    public String getRequestcmdKO(String cmd){
        return "KO@"+cmd;
    }
    
            /** Response */

    
    
    /**
     * Send to remote handler tools to access needed for work
     * @param dburl
     * @param idC
     * @param nbrThread
     * @return 
     */
    public String getResponseConnectOK(String dburl, int idC, int nbrThread ){
        return "OK@"+dburl+","+idC+","+nbrThread;
    }
    
    
    /**
     * Responce to send a job for the handler requesting
     * @param cmd a job to do
     * @return 
     */
    public String getResponseJob(String cmd){
        return "OK@"+cmd;
    }
    
    
    /**
     * In case negetive respose  for request
     * @param order "stop" or "sleep" handler for amount of time
     * @param timeSleep
     * @return 
     */
    public String getResponseKO(String order, int timeSleep){
        return "KO@"+order+","+timeSleep;
    }
    
    
}
