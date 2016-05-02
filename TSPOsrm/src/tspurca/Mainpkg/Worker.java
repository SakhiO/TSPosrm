/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */
public class Worker extends Thread{
    
    //private BlockingQueue queue = null;
    private boolean fin = false;
    public  String cmd;
    private final Access ac;
    private final DatagramSocket socketW;
    private byte[]  sendBuf;
    public TimerJob WBD = new TimerJob();
    public TimerJob WPars = new TimerJob();
    
    public Worker(Access ac) throws SocketException{
        this.socketW = new DatagramSocket();
        this.ac = new Access(ac);
        this.sendBuf = new byte[1024*4];
        
    }
    
    @Override
    public void run() {
        Commande cmd = new Commande();
        String log="";
        DatagramPacket packet;
        try {
            this.ac.BD.connectBD();
            
            while(!isFin()){
                
                this.ac.BD.tj = new TimerJob();
                
                try {
                    /* process un travaille */
                    /* we pass the commande, thread name and access*/
                    this.cmd = (String)this.ac.getfromQueue();
                    Job  jb = new Job(this.cmd,this.getName(), this.ac);
                    jb.execute();
                    this.WBD.addTime(this.ac.BD.tj);
                    this.WPars.addTime(jb.getRoutine().tjPars);
                    log = cmd.getReporttLog(this.WBD,this.WPars);
                    
                } catch (Exception e) {
                    System.err.println("Erreur in Execute Job at Thread"+this.getName());
                    try {
                        this.ac.addtoQueue(this.cmd);
                    } catch (Exception ee) {
                        System.err.println("Erreur in job put it back to queue final");
                        System.err.println(ee.toString());
                        log = cmd.getRequestcmdKO(this.cmd);
                    }
                    System.err.println("Putting Back Commande to queue cmd :"+this.cmd);
                    //System.err.println(e.toString());
                    e.printStackTrace();
                    
                }
                finally{
                    this.sendBuf = log.getBytes();
                    packet = new DatagramPacket(this.sendBuf, this.sendBuf.length,this.ac.socketS);
                    this.socketW.send(packet);
                }    
            }
            
            this.ac.BD.closeBD();
        } catch (Exception ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void dostop(){
        this.fin = true;
        /* changer fin*/
        this.interrupt();
    }

    public synchronized boolean isFin() {
        return this.fin;
    }
    
}
