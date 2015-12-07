/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import java.io.*;

/**
 *
 * @author mykro
 */
public class RMIInputStream extends InputStream implements
        Serializable {

    RMIInputStreamInterf in;

    public RMIInputStream(RMIInputStreamInterf in) {
        this.in = in;
    }

    public int read() throws IOException {
        return in.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        byte[] b2 = in.readBytes(len);
        if (b2 == null) {
            return -1;
        }
        int i = b2.length;
        System.arraycopy(b2, 0, b, off, i);
        return i;
    }

    public void close() throws IOException {
        super.close();
    }

}
