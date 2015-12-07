/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author mykro
 */
public interface RMIInputStreamInterf extends Remote {
    
    public byte[] readBytes(int len) throws IOException, 
    RemoteException;
    public int read() throws IOException, RemoteException;
    public void close() throws IOException, RemoteException;

}

