/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import java.rmi.Naming;
import ArchivosRMI.TestServer.Server;

/**
 *
 * @author mykro
 */
public class TestClient {
    
    public static void main(String[] args) throws Exception {
        
        String url = "rmi://localhost/server";
        Server server = (Server) Naming.lookup(url);

        System.out.println("Server says: " + server.sayHello());
    }

}