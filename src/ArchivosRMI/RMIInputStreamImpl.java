/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import static javax.management.Query.*;

/**
 *
 * @author mykro
 */

public class RMIInputStreamImpl implements RMIInputStreamInterf {

    private InputStream in;
    private byte[] b;

    public RMIInputStreamImpl(InputStream in) throws IOException {
        this.in = in;
        UnicastRemoteObject.exportObject(this, 1099);
    }

    public void close() throws IOException, RemoteException {
        in.close();
    }

    public int read() throws IOException, RemoteException {
        return in.read();
    }

    public byte[] readBytes(int len) throws IOException, 
            RemoteException {
        if (b == null || b.length != len)
            b = new byte[len];
            
        int len2 = in.read(b);
        if (len2 &lt; 0)
            return null; // EOF reached
        
        if (len2 != len) {
            // copy bytes to byte[] of correct length and return it
            byte[] b2 = new byte[len2];
            System.arraycopy(b, 0, b2, 0, len2);
            return b2;
        }
        else
            return b;
    }
}