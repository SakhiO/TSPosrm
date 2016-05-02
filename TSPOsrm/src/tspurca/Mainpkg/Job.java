/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tspurca.Mainpkg;

import java.io.IOException;
import java.sql.SQLException;
import org.jdom2.JDOMException;
import tspurca.Tools.TimerJob;

/**
 *
 * @author sunshine
 */
public class Job {

    private final Commande cmd;
    private final Routines routine;
    public final String nameThr;
    
    
   public Job(String cmd, String nameThr, Access ac) {
        this.cmd = new Commande(cmd);
        this.nameThr = nameThr;
        this.routine = new Routines(ac);
    }
    
    public void execute() throws IOException, SQLException, JDOMException, InterruptedException, Exception{
            
        /* init jobs*/
            
            switch(this.cmd.idcmd){
                case 0:
                    System.out.println("init ville Producer - START "+this.nameThr);
                    this.routine.initVille(this.cmd.filename, this.nameThr);
                    System.out.println("init ville Producer - END "+this.nameThr);
                    break;
                case 1:
                    System.out.println("init distance Producer - START "+this.nameThr);
                    this.routine.initDistance();
                    System.out.println("init distance Producer - END "+this.nameThr);
                    break;
                
                case 2:     
                    switch(this.cmd.routine){
                           case 0:
                                 System.out.println("job : ville ");
                                 break;
                           case 1:
                                 System.out.println("job : Fill Diagonal n :"+this.cmd.n+"- START "+this.nameThr);
                                 this.routine.jobDiagonal(this.cmd.n, this.nameThr);
                                 System.out.println("job : Fill Diagonal n :"+this.cmd.n+"- END "+this.nameThr);
                                 break;
                           case 2:
                                 System.out.println("job : Fill UPPER INDEXs(n m):("+this.cmd.n+","+this.cmd.m+")- START "+this.nameThr);
                                 this.routine.jobUpper(this.cmd.n, this.cmd.m, this.nameThr);
                                 System.out.println("job : Fill UPPER INDEXs(n m):("+this.cmd.n+","+this.cmd.m+")- END "+this.nameThr);
                                 break;
                      }
                      break;  
                default :
                        throw new Exception("Erreur dans la commande commande "+this.cmd.toString());
            }/* End of Switch */

    }

    public Routines getRoutine() {
        return this.routine;
    }
    
    

}/* End of Class */
