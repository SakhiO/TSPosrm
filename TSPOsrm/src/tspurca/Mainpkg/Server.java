/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */


public class Server extends Thread {

    private String dburl;
    private int idC;
    private int maxWorkers;
    public final Access ac;
    private DatagramSocket socketS;
    public TimerJob TimeDB;
    public TimerJob TimePars;
    private int nbrJobs;
    private boolean fin = false;
   
    byte[]  recvBuf;

    public Server(Access ac, int idC, int maxWorkers) throws Exception {

        try {
            this.ac = ac;
            this.socketS = new DatagramSocket(this.ac.socketS);
            this.dburl = this.ac.BD.dburl;
            this.idC = idC;
            this.maxWorkers = maxWorkers; 
            
            this.nbrJobs = 0;
            this.recvBuf = new byte[1024*4]; /*max of datagramme packet*/
            this.TimeDB = new TimerJob();
            this.TimePars = new TimerJob();
        } catch (SocketException ex) {
            throw new Exception("Erreur in initiazation of The Server ", ex);
        }
    }


    @Override
    public void run() {
        
        Commande cmd = new Commande();
        String responce;
        while((!isFin()) || (this.nbrJobs > 0)){
            try {
                this.recvBuf = new byte[1024*4];
                DatagramPacket packet = new DatagramPacket(this.recvBuf, this.recvBuf.length);
                socketS.receive(packet);
                Request req = new Request(packet);
                //System.out.println(" esponse :" + new String(packet.getData()).replaceAll("\0", ""));
                responce="";
                /* send request end to handlers*/
                if(isFin())
                    req.End();
                
                switch(req.getType()){
                    
                    case "CONNECT":
                        
                        responce = cmd.getResponseConnectOK(this.dburl, this.idC, this.maxWorkers);
                        packet = req.getPackerRep(responce);
                        socketS.send(packet);
                        
                        break; 
                        
                    case "JOB":
                        
                        if(!this.ac.isQueueEmpty()){
                            String tmp = (String)this.ac.getfromQueue();
                            responce = cmd.getResponseJob(tmp);
                        }
                        else{
                            responce = cmd.getResponseKO("sleep",5000);
                        }    
                        
                        packet = req.getPackerRep(responce);
                        socketS.send(packet);
                        break;
                        
                    case "LOG":
                        /* handle log add time*/
                        String[] tmp = req.getRequest().split("&");
                        this.TimeDB.addTime(new TimerJob(tmp[0]));
                        this.TimePars.addTime(new TimerJob(tmp[1]));
                        System.out.println("log :"+Arrays.toString(tmp));
                        /* a job done - 1*/
                        this.nbrJobs--;
                        break;
                    
                    case "KO":
                        System.err.println("Erreur job failed"+req.getSocketCInfo());
                        this.ac.addtoQueue(req.getRequest());
                        break;
                        
                    /* in case end or else send end */
                    case "END":    
                    default :
                        responce = cmd.getResponseKO("stop",0);
                        packet = req.getPackerRep(responce);
                        socketS.send(packet);
                }
                
            } catch (Exception ex) {
                /* in case Exception*/
                ex.printStackTrace();
            }
        }
    }
    
    
    public synchronized void dostop() throws InterruptedException{

        this.fin = true;
    }
    
    public synchronized boolean isFin() {
        return this.fin;
    }
    
    
    public synchronized void addjob(String cmd){
        if(this.fin) throw new IllegalStateException(" Pool Thread est arreter");
        /* insert commande in queue*/
        this.ac.addtoQueue(cmd);
        this.nbrJobs++;
    }
    
    public synchronized String takejob() throws InterruptedException { 
        /* get commande from queue*/
        return (String) this.ac.getfromQueue();
    }

    public int getNbrJobs() {
        return nbrJobs;
    }
    
   /**
    * Classe Request to 
    */
   private class Request {
       
       private final SocketAddress socketC;
       private String type;
       private final String request;
       private byte[] sendBuf;
    /**
     * pars request so to be eazier to handle request
     * @param rcvp 
     */
        public Request(DatagramPacket rcvp){
            this.socketC = rcvp.getSocketAddress();
            String quee = new String(rcvp.getData()).replaceAll("\0", "");
            String tmp[] = quee.split("@");
            this.type = tmp[0];
            if (tmp.length>1) {
                this.request = tmp[1];   
            }
            else
                this.request ="";
            this.sendBuf = new byte[1024*4];
        }
        
        public void End() {
            this.type = "END";
        }   

        public String getType() {
            return type;
        }

    
        public String getRequest() {
            return this.request;
        }
    
        /**
         * get datagrampacket as responce to clien request from cmd 
         * @param cmd commande to put in packet client
         * @return datagrampacket
         * @throws SocketException 
         */
        public DatagramPacket getPackerRep(String cmd) throws SocketException{
            DatagramPacket p;
            this.sendBuf = cmd.getBytes();
            p = new DatagramPacket(this.sendBuf, this.sendBuf.length, this.socketC);
            return p;
        }

        public String getSocketCInfo() {
            return socketC.toString();
        }
    
        
    
   }

    
}
