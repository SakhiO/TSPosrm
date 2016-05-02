/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Tools;

/**
 *
 * @author sunshine
 */
public class TimerJob {
   private long tmpecouler;
   private int cmptrequete; 

    public TimerJob() {
        this.tmpecouler = 0;
        this.cmptrequete = 0;
    }
    
    public TimerJob(String sTjob){
        String[] tmp = sTjob.split(",");
        this.tmpecouler = Long.valueOf(tmp[0]);
        this.cmptrequete = Integer.valueOf(tmp[1]);
    }
    
    public void start(){
        this.tmpecouler = System.currentTimeMillis();
    }
    
    public void stop(){
        this.tmpecouler = System.currentTimeMillis() - this.tmpecouler;
        this.cmptrequete++;
    }
   
    public void addTime(TimerJob tj){
        this.tmpecouler += tj.getTmpecouler();
        this.cmptrequete += tj.getCmptrequete();
    }

    public void setTmpecouler(long tmpecouler) {
        this.tmpecouler = tmpecouler;
    }

    public void setCmptrequete(int cmptrequete) {
        this.cmptrequete = cmptrequete;
    }

    public long getTmpecouler() {
        return tmpecouler;
    }

    public int getCmptrequete() {
        return cmptrequete;
    }

    @Override
    public String toString() {
        return tmpecouler + "," + cmptrequete;
    }
   
   
}
