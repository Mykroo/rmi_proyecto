/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import java.io.*;
import java.rmi.Naming;
import ArchivosRMI.ServidorSencillo.Server;

/**
 *
 * @author mykro
 */
public class ClienteIO {
    
    final public static int BUFFER_TAM = 1024 * 64;
    
    public static void copia(InputStream in, OutputStream out) 
            throws IOException {
        System.out.println("using byte[] read/write");
        byte[] b = new byte[BUFFER_TAM];
        int len;
        while ((len = in.read(b)) >= 0) {
            out.write(b, 0, len);
        }
        in.close();
        out.close();
    }
    
    public static void upload(Server server, File src, File dest) throws IOException {
        copia (new FileInputStream(src), 
        server.getOutputStream(dest));
    }

    public static void download(Server server, File src,File dest) throws IOException {
        copia (server.getInputStream(src), 
        new FileOutputStream(dest));
    }

    public static void main(String[] args) throws Exception {
        
        String url = "rmi://192.168.1.77/server";
        Server server = (Server) Naming.lookup(url);
        String img_test="/home/mykro/Desktop/pito1.jpg";
        String img_dwn="archivos/test1.jpg";
        String img_up=img_test;

        System.out.println("El Servidor te saluda : " + server.sayHello());
        System.out.println("");
        File testFile = new File(img_dwn);
        long len = testFile.length();
        
        long t=0;
        t = System.currentTimeMillis();
        download(server, testFile, new File(img_test));
        t = (System.currentTimeMillis() - t) / 1000;
        System.out.println("download: " + (len / 1 /1000000d) + 
            " MB/s");
        
        t = System.currentTimeMillis();
        upload(server, new File(img_dwn), new File("archivos/universe.jpg"));
        t = (System.currentTimeMillis() - t) / 1000;
        System.out.println("upload: " + (len / 1 / 1000000d) + 
            " MB/s");
    }
}