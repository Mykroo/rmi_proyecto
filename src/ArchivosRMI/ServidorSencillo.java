/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import java.rmi.*;
import java.io.*;
import java.rmi.registry.*;
import java.rmi.server.*;

/**
 *
 * @author mykro
 */
public class ServidorSencillo {

    public interface Server extends Remote {

        public String sayHello() throws RemoteException;

        public OutputStream getOutputStream(File f) throws IOException;

        public InputStream getInputStream(File f) throws IOException;
        
        public File[] listaArchivos() throws Exception;
        
        public String TextoStreamOut(String f,String fi) throws IOException;
    }

    public static class ServerImpl extends UnicastRemoteObject
            implements Server {

        public File[] listaArchivos() {
            System.out.println("Peticion lista de archivos");
            File folder = new File("archivos/");            
            File[] listaDeArchivs = folder.listFiles();
            for (File file : listaDeArchivs) {
                if (file.isFile()) {
                    //System.out.println(file.getName());                    
                }
            }            
            return listaDeArchivs;
        }
        public String TextoStreamOut(String f,String fi)throws IOException{
            String texto="";            
            String aux="";            
            BufferedReader br=new BufferedReader(new FileReader(f));
            System.out.println("Leyendo texto");
            while((aux= br.readLine()) != null){
                texto+=aux+"\n";
            }            
            br.close();
            return texto;
        }
        public String TextoStreamIn(String f)throws IOException{
            String texto="";            
            String aux="";            
//            PrintWriter br=new BufferedReader(new FileReader(f));
//            System.out.println("Leyendo texto");
//            while((aux= br.readLine()) != null){
//                texto+=aux+"\n";
//            }            
//            br.close();
            return texto;
        }
        public int getVersion(File f){
            return 1;
        }
        
        public OutputStream getOutputStream(File f) throws IOException {
            System.out.println("Peticion de descarga");
            return new RMIOutputStream(new RMIOutputStreamImpl(new FileOutputStream(f)));
        }

        public InputStream getInputStream(File f) throws IOException {
            System.out.println("Peticion de escritura");
            return new RMIInputStream(new RMIInputStreamImpl(new FileInputStream(f)));
        }

        Registry rmiRegistry;

        public ServerImpl() throws RemoteException {
            super();
        }

        public void start() throws Exception {
            rmiRegistry = LocateRegistry.createRegistry(1099);
            rmiRegistry.bind("server", this);            
            System.out.println("Server started");
        }

        public void stop() throws Exception {
            rmiRegistry.unbind("server");
            unexportObject(this, true);
            unexportObject(rmiRegistry, true);
            System.out.println("Server stopped");
        }

        public String sayHello() {
            System.out.println("Peticion de saludo");
            return "Hola clientes ";
        }

    }

    public static void main(String[] args) throws Exception {
        ServerImpl server = new ServerImpl();
        server.start();
        for (int i = 0; i > -1; i++) {
            System.out.println("Servidor en ejecucion por " + i * 5 + " segundos");
            Thread.sleep(5 * 1000); // run for 5 minutes            
        }
        server.stop();
    }

}
