/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import java.net.SocketAddress;
import java.util.Vector;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */

public final class ThreadPool {
    
    private Access ac = null;
    private final Vector<Worker> vecThr = new Vector<Worker>();
    private boolean fin = false;
    
    
    public ThreadPool(String dburl,int idC, int nbrThread,int maxSzqueue, SocketAddress socketS) throws Exception {
        
        this.ac = new Access(dburl, idC, maxSzqueue, socketS); 

        for (int i = 0; i < nbrThread; i++)           
            vecThr.add(new Worker(this.ac));
        
        this.start();
    }
    
    public ThreadPool(String access,int maxSzqueue, SocketAddress socketS) throws Exception {
        String[] tmp = access.split(",");
        String dburl = tmp[0];
        int idC = Integer.valueOf(tmp[1]);
        int nbrThread = Integer.valueOf(tmp[2]);
        
        this.ac = new Access(dburl, idC, maxSzqueue, socketS);

        for (int i = 0; i < nbrThread; i++)           
            vecThr.add(new Worker(this.ac));
        
        this.start();
    }
    
    public void start(){
        /* start threads */
        for(Worker wk: vecThr)wk.start();
    }
    
    public synchronized void addjob(String cmd){
        if(this.fin) throw new IllegalStateException(" Pool Thread est arreter");
        /* insert commande in queue*/
        this.ac.addtoQueue(cmd);
    }
    
    public synchronized String takejob() throws InterruptedException { 
        /* get commande from queue*/
        return (String) this.ac.getfromQueue();
    }
    
    public synchronized void stop(){
        /* stop threads */
        for(Worker wk: vecThr)wk.dostop();
        this.fin = true;
    }
    
    public String workersTimeSum(){
        String tm = "";
        TimerJob tdb = new TimerJob();
        TimerJob tps = new TimerJob();
        
        for(Worker wk: vecThr){
            tdb.addTime(wk.WBD);
            tps.addTime(wk.WPars);
        }
        return tdb.toString()+"\n"+tps.toString();
    } 
   
   public boolean isQueueEmpty() throws InterruptedException{
       return this.ac.isQueueEmpty();
   } 
}
