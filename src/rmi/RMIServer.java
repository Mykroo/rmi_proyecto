/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
import static java.lang.System.setSecurityManager;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
/**
 *
 * @author Miguel
 */
public class RMIServer{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        Ejecutor imple=new ImplementacionEjecutor();
        Registry registry=LocateRegistry.createRegistry(55555);
        registry.rebind("ServicioFiltro", imple);
    }
    
}
