/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */
public class Client extends Thread {
    
    
    private int portC;
    private DatagramSocket socketC;
    
    private final SocketAddress socketS;
    
    private byte[]  recvBuf;
    
    private int nbrJobs;
    private boolean fin = false;
    private ThreadPool pool;

    public Client(SocketAddress socketS) throws SocketException {
        this.socketC = new DatagramSocket();
        this.socketS = socketS;
        this.recvBuf = new byte[1024*4];
        this.nbrJobs = 0;
    }
    
    
    
    
    @Override
    public void run() {
        
        Commande cmd = new Commande();
        
        
        try {
            /* initialise handler by requesting access config*/      
            
            String response = cmd.getRequestConnect();
            this.recvBuf = response.getBytes();
            DatagramPacket packet = new DatagramPacket(this.recvBuf, this.recvBuf.length, socketS);
            socketC.send(packet);
            /* recv response */
            packet = new DatagramPacket(this.recvBuf, this.recvBuf.length);
            socketC.receive(packet);
            Response res = new Response(socketS, packet);  
            
            if(res.getType().equals("OK")){
                
                this.pool = new ThreadPool(res.getResponse(),1024, socketS);
                
                while((!isFin()) || (this.nbrJobs > 0)){
                    try {
                        /* Request jobs*/
                        packet = res.getPackerReq(cmd.getRequestJob());
                        socketC.send(packet);
                        /* Receive Response for job*/
                        packet = new DatagramPacket(this.recvBuf, this.recvBuf.length);
                        socketC.receive(packet);
                        res = new Response(socketS, packet);

                        switch(res.getType()){

                            case "OK":
                                /* add job to do pool */
                                response = res.getResponse();
                                pool.addjob(response);
                                this.nbrJobs++;
                                break; 

                            case "KO":
                                response = res.getResponse();
                                String[] tmp = response.split(",");
                                
                                if(tmp[0].equals("sleep")){
                                    sleep(Long.valueOf(tmp[1]));
                                }
                                else{
                                    this.dostop();
                                }    
                                break;
    
                            default :
                                
                        }
                        
                        /* send request end to handlers*/
                        if(isFin()){
                            while(!pool.isQueueEmpty()){   
                                    response = cmd.getRequestcmdKO(pool.takejob());
                                    packet = res.getPackerReq(response);
                                    socketC.send(packet);
                            }
                        }    

                    } catch (Exception ex) {
                        /* in case Exception*/
                        ex.printStackTrace();
                    }
                }/* End of while */
          }/* End if ok access */
          else
              throw new Exception("Request Access Denied from Server ");
        } catch (Exception ex) {
            System.err.println("Erreur in intialisation");
            ex.printStackTrace();
            this.dostop();
        }
    }
    
    
    public synchronized void dostop(){
        pool.stop();
        this.fin = true;
    }
    
    public synchronized boolean isFin() {
        return this.fin;
    }
    
    private class Response {
       
       private final SocketAddress socketS;
       private String type;
       private String response;
       private byte[] sendBuf;
       
        
    
    /**
     * pars request so to be eazier to handle response
     * @param rcvp 
     */
        public Response(SocketAddress socketS,DatagramPacket rcvp){
            this.socketS = socketS;
            String tmp[] = new String(rcvp.getData()).split("@");
            this.type = tmp[0];
            this.response = tmp[1];
            this.sendBuf = new byte[1024*4];
        }
 
        public void End() {
            this.type = "END";
        }

        public String getType() {
            return type;
        }

    
        public String getResponse() {
            return this.response;
        }
    
        /**
         * get datagrampacket as request to Server for cmd 
         * @param cmd commande to put in packet 
         * @return datagrampacket
         * @throws SocketException 
         */
        public DatagramPacket getPackerReq(String cmd) throws SocketException{
            DatagramPacket p;
            this.sendBuf = cmd.getBytes();
            p = new DatagramPacket(this.sendBuf, this.sendBuf.length, this.socketS);
            return p;
        }

        public String getSocketCInfo() {
            return socketC.toString();
        }
    
}
    
}
