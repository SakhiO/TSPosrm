/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import tspurca.Osmpkg.BaseDeDonnee;
import tspurca.Tools.Config;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */
public class Access {
    
        
    public BaseDeDonnee BD;
    public Config config;
    private BlockingQueue queue = null;
    public SocketAddress socketS;

    public Access(String dburl, int idC, int maxSzqueue, SocketAddress socketS) throws SQLException, Exception {
        this.BD = new BaseDeDonnee(dburl);
        
        BD.connectBD();
        config = new Config(BD.GetConfig(idC));
        BD.closeBD();
        
        this.queue = new ArrayBlockingQueue(maxSzqueue);
        this.socketS = socketS;
    }

    public Access(Access ac) {
        this.BD = ac.BD;
        this.config = new Config(ac.config);
        this.queue = ac.getQueue();
        this.socketS = ac.socketS;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public BlockingQueue getQueue() {
        return queue;
    }
   
    
    public void addtoQueue(Object o) {
        this.queue.add(o);
    }
    
    public Object getfromQueue() throws InterruptedException {
        return this.queue.take();
    }
    
   public boolean isQueueEmpty() throws InterruptedException {
        return this.queue.isEmpty();
    } 
    
    
}
