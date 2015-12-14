/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import java.rmi.Naming;
import ArchivosRMI.ServidorSencillo.Server;
import java.net.InetAddress;

/**
 *
 * @author mykro
 */
public class ClienteSencillo {

    public static void main(String[] args) throws Exception {

        String url = "rmi://192.168.1.68/server";        
        String hostname = InetAddress.getLocalHost().getHostAddress();
        System.out.println("this host IP is " + hostname);
        Server server = (Server) Naming.lookup(url);
        for (int i = 0; i > -1; i++) {
            Thread.sleep((int) (Math.random() * 2 * 1000)); // run for 5 minutes
            System.out.print("Peticion al servidor: " + i);
            System.out.println(" -----------El Servidor te saluda: " + server.sayHello());
        }
    }

}
