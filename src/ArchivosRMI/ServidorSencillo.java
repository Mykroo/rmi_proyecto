/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArchivosRMI;

import GUI.TablaUI;
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
//            for (File file : listaDeArchivs) {
//                if (file.isFile()) {
//                    //System.out.println(file.getName());                    
//                }
//            }
            return listaDeArchivs;
        }

        public boolean buscaArchivo(String nom) {
            boolean existe = false;
            System.out.println("Buscando " + nom);
            for (File f : this.listaArchivos()) {
//                System.out.println(f.getName().equals(nom));
                if (f.getName().equals(nom)) {
                    existe = true;
                    break;
                }
            }
            return existe;
        }

        private void sincronizar() {

            try {
                //System.out.println("arch.txt"+buscaArchivo("arch.txt"));
                Server serv2;
                String url = "rmi://192.168.43.173/server";
//                String url = "rmi://192.168.43.173/server";
                System.out.println(url);
                serv2 = (Server) Naming.lookup(url);
                //System.out.println(serv2.sayHello());
                File[] lista_archivos;
                lista_archivos = serv2.listaArchivos();
                System.out.println("------------********************Checando sincronizacion*"+lista_archivos.length+"***********------------------------");
                int i=0;
                for (File arch_2 : lista_archivos) {
                    if (!buscaArchivo(arch_2.getName())) {
//                        TablaUI.download(serv2, new File("archivos/"+arch_2.getName()), new File("archivos/"+arch_2));
                        System.out.println("Falta archivo " + arch_2.getName() + " en este servidor");
                        i++;
                    }
                }System.out.println("Faltan "+i);
//                i=0;
//                for (File arch_loc : this.listaArchivos()) {
//                    if (buscaArchivo(arch_loc.getName())) {
////                        TablaUI.upload(serv2, new File("archivos/"+arch_loc.getName()), new File("archivos/"+arch_loc.getName()));
//                        System.out.println("Falta archivo " + arch_loc.getName() + "en el otro");
//                        i++;                                
//                    }
//                }System.out.println("Faltan "+i +" en el otro");
                if (this.listaArchivos().length == lista_archivos.length) {
                    System.out.println("------------***************************Sincronizando************------------------------");
                } else {
                    System.out.println("Ya sincronizados");
                }
                Thread.sleep((int) (Math.random() * 1000) / 5); // run for 5 minutes
                System.out.println("Conectando al servidor");
                System.out.println("Lista de archivos");
                System.out.println(" -----------Server says: " + serv2.sayHello());
            } catch (Exception e) {
                System.out.println("Error de conexion al servidor");
            }
        }

        public String TextoStreamOut(String f, String fi) throws IOException {
            sincronizar();
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
            if (checaUltimaVersion(f) == false) {
                int numVersion = getVersion(f);
                String nuevoNombre = getNombreArchivo(f);
                numVersion++;
                System.out.println("La versión es: " + numVersion);
                nuevoNombre += "-" + Integer.toString(numVersion);
                System.out.println("El nuevo archivo será: " + nuevoNombre);
                File newArchivo = new File("archivos/" + nuevoNombre);
                BufferedWriter pr = new BufferedWriter(new FileWriter(nuevoNombre));
                System.out.println("Guardando texto");
                pr.write(texto);
                pr.close();
                return true;
            }
            return false;
        }

        public int getVersion(String f) throws IOException {
            String[] version = f.split("-");
            return Integer.parseInt(version[1]);
        }

        public boolean checaUltimaVersion(String f) throws IOException {
            boolean band = false;
            File[] listaDeArchivos = listaArchivos();
            String nombreArchivo = getNombreArchivo(f);
            int numVersion = getVersion(f);
            for (File file : listaDeArchivos) {

                if (("archivos/" + getNombreArchivo(file.getName())).equals(nombreArchivo)) {
                    System.out.println("Hola");
                    if (getVersion(file.getName()) > numVersion) {
                        System.out.println("No puedes cargarlo, descarga la versión mas reciente");
                        band = true;
                        break;
                    }
                }
            }
            return band;
        }

        public String getNombreArchivo(String f) {
            String[] nombre = f.split("-");
            return nombre[0];
        }

        public void renombra(String t) throws IOException {
            System.out.println("El nombre del archivo es: " + t);

            File oldfile = new File(t);
            t = t + "-1";
            System.out.println(t);
            File newfile = new File(t);

            if (oldfile.renameTo(newfile)) {
                System.out.println("Rename succesful");
            } else {
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
