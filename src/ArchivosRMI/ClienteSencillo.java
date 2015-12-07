/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import java.rmi.Naming;
import ArchivosRMI.ServidorSencillo.Server;

/**
 *
 * @author mykro
 */
public class ClienteSencillo {
    
    public static void main(String[] args) throws Exception {
        
        String url = "rmi://localhost/server";
        Server server = (Server) Naming.lookup(url);
        for (int i = 0; i > -1; i++) {
            Thread.sleep((int) (Math.random()*10 *1000)/5); // run for 5 minutes
            System.out.print("Peticion al servidor: "+i);
            System.out.println(" -----------Server says: " + server.sayHello());
        }        
    }

}