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

        public String TextoStreamOut(String f, String fi) throws IOException;

        public boolean TextoStreamIn(String f, String texto) throws IOException;
        
        public void renombra(String t) throws IOException;
        
        public int getVersion(String f) throws IOException;
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

        public String TextoStreamOut(String f, String fi) throws IOException {
            String texto = "";
            String aux = "";
            BufferedReader br = new BufferedReader(new FileReader(f));
            System.out.println("Leyendo texto");
            while ((aux = br.readLine()) != null) {
                texto += aux + "\n";
            }
            br.close();
            return texto;
        }

        public boolean TextoStreamIn(String f, String texto) throws IOException {
            if(checaUltimaVersion(f)==false){
            int numVersion = getVersion(f);
            String nuevoNombre = getNombreArchivo(f);
            numVersion++;
            System.out.println("La versión es: "+numVersion);
            nuevoNombre+="-"+Integer.toString(numVersion);
            System.out.println("El nuevo archivo será: "+nuevoNombre);
            File newArchivo = new File("archivos/"+nuevoNombre); 
            BufferedWriter pr = new BufferedWriter(new FileWriter(nuevoNombre));
            System.out.println("Guardando texto");
            pr.write(texto);
            pr.close();
            return true;
            }
            return false;
        }
        

        public int getVersion(String f) throws IOException{
            String[] version=f.split("-");
            return Integer.parseInt(version[1]);
        }
        
        public boolean checaUltimaVersion(String f) throws IOException{
            boolean band=false;
            File [] listaDeArchivos=listaArchivos();
            String nombreArchivo = getNombreArchivo(f);
            int numVersion = getVersion(f);
            for (File file : listaDeArchivos) {
               
                if (("archivos/"+getNombreArchivo(file.getName())).equals(nombreArchivo)) {
                    System.out.println("Hola");
                    if(getVersion(file.getName())>numVersion){
                    System.out.println("No puedes cargarlo, descarga la versión mas reciente");
                    band = true;
                    break;
                    }           
                }
            }
            return band;
        }
        
        public String getNombreArchivo(String f){
            String[] nombre=f.split("-");
            return nombre[0];
        }
        
        public void renombra(String t)throws IOException{
            System.out.println("El nombre del archivo es: "+t);
           
             File oldfile =new File(t);
                t=t+"-1";
                 System.out.println(t);
		File newfile =new File(t);
		
		if(oldfile.renameTo(newfile)){
			System.out.println("Rename succesful");
		}else{
			System.out.println("Rename failed");
		}
        }

        public OutputStream getOutputStream(File f) throws IOException {
            System.out.println("Peticion de escritura");
            File[] listaDeArchivos = listaArchivos();
            OutputStream auxiliar = null;
            boolean band = false;
            for (File file : listaDeArchivos) {
                if (file.getName().equals(f.getName())) {
                    System.out.println("No puedes cargarlo, descarga la versión mas reciente");
                    band = true;
                    break;
                }
            }

            if (band == false) {
                System.out.println("Se subirá al servidor");
                auxiliar = new RMIOutputStream(new RMIOutputStreamImpl(new FileOutputStream(f)));
            }

            return auxiliar;
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
