/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import java.io.*;
import java.util.*;

/**
 *
 * @author mykro
 */
public class RMIPipe implements Serializable {

    final public static int BUF_SIZE = 1024 * 64;
    private static int keySeed = 0;    
    private static Hashtable<Integer, OutputStream>
            registry = new Hashtable<Integer, OutputStream>();   
    private transient int key;
    private transient InputStream in;
    private transient boolean isOutputRegistration;

    public RMIPipe(int key, InputStream in) {
        this.key = key;
        this.in = in;
        isOutputRegistration = false;
    }

    public RMIPipe(OutputStream out) {
        isOutputRegistration = true;
        synchronized (registry) {
            key = keySeed++;
            registry.put(key, out);
        }
    }

    public int getKey() {
        if (!isOutputRegistration)
            throw new IllegalArgumentException(
                "not an OutputStream registration");
        return key;
    }

    protected void finalize() {
        // just to be sure
        if (isOutputRegistration)
            registry.remove(key);
    }

    private void writeObject(ObjectOutputStream out) throws 
            IOException {
        out.writeInt(key);
        byte[] b = new byte[BUF_SIZE];
        int len;
        do {
            len = in.read(b);
            out.writeInt(len);
            if (len >= 0)
                out.write(b, 0, len);
        } while (len >= 0);
    }

    private void readObject(ObjectInputStream in) throws 
            IOException {
        int key = in.readInt();
        OutputStream out = registry.remove(key);
        byte[] b = new byte[BUF_SIZE];
        int len;
        do {
            len = in.readInt();
            if (len >= 0) {
                in.readFully(b, 0, len);
                out.write(b, 0, len);
            }
        } while (len >= 0);
    }

}