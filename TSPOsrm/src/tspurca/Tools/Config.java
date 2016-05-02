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
public class Config {
    
    private final String urlOsrm;
    private final int tailleReq;
    private final int NbVille;

    public Config(String urlOsrm, int tailleReq, int NbVille) {
        this.urlOsrm = urlOsrm;
        this.tailleReq = tailleReq;
        this.NbVille = NbVille;
    }

    public Config(Config config) {
        this.urlOsrm = config.getUrlOsrm();
        this.tailleReq = config.getTailleReq();
        this.NbVille = config.getNbVille();
    }
    

    public String getUrlOsrm() {
        return urlOsrm;
    }

    public int getTailleReq() {
        return tailleReq;
    }

    public int getNbVille() {
        return NbVille;
    }
    
    
}
