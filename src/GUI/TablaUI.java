/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import ArchivosRMI.ServidorSencillo.Server;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mykro
 */
public class TablaUI extends javax.swing.JFrame {

    /**
     * Creates new form TablaUI
     *
     * @param serv
     */
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
        copia(new FileInputStream(src),
                server.getOutputStream(dest));
    }

    public static void download(Server server, File src, File dest) throws IOException {
        copia(server.getInputStream(src), new FileOutputStream(dest));
        long len = src.length();
        long t = 0;
        t = System.currentTimeMillis();
        t = (System.currentTimeMillis() - t) / 1000;
        System.out.println("download: " + (len / 1 / 1000000d)
                + " MB/s");
    }

    public void coneccionServer(String serv) {
        System.out.println("aqui estoy " + serv);
        System.out.println("rmi://" + serv + "/server");
        try {
            String url = "rmi://" + serv + "/server";
            System.out.println(url);
            server = (Server) Naming.lookup(url);
            System.out.println(server.sayHello());
            File[] lista_archivos;
            lista_archivos = server.listaArchivos();
            Thread.sleep((int) (Math.random() * 1000) / 5); // run for 5 minutes
            System.out.println("Conectando al servidor");
            System.out.println("Lista de archivos");
            Date fecha = new Date();
            for (File f : lista_archivos) {
                //if (f.isFile()) {
                //System.out.println(file.getName());                    
                //System.out.println(f.getPath());
                datos.addRow(new Object[]{f.getName(), "1.0", fecha});
                //}
            }
            System.out.println(" -----------Server says: " + server.sayHello());

        } catch (Exception e) {
            System.out.println("Error de conexion al servidor");
        }
    }

    public void initTabla() {
        String[] columnNames = {"Archivo ",
            "Version",
            "Ultima escritura"};

        Object[][] data = {};
        datos = new DefaultTableModel(data, columnNames);
        tablaDatos.setModel(datos);
    }

    public TablaUI(String ipServ) throws Exception {

//        datos.addColumn("Nombre ");
//        datos.addColumn("Version ");
//        datos.addColumn("Ultima actualización");
        //datos.addRow();       
        //System.out.println("constructor ui tabla: " + ipServ);
        initComponents();
        this.setLocation(500, 100);
        temp_path = System.getProperty("user.language");
        home = System.getProperty("user.home");
        initTabla();
        coneccionServer(ipServ);
        //System.out.println("Direccion del servidor en TablaUI: " + ipServ);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDatos = new javax.swing.JTable();
        nuevoArchivo = new javax.swing.JButton();
        nuevoArchivo2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tablaDatos.setAutoCreateRowSorter(true);
        tablaDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nombre", "Version", "ultima"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tablaDatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaDatosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaDatos);

        nuevoArchivo.setText("Agregar archivo al servidor");
        nuevoArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoArchivoActionPerformed(evt);
            }
        });

        nuevoArchivo2.setText("Agregar archivo al servidor");
        nuevoArchivo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoArchivo2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevoArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(nuevoArchivo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoArchivo)
                    .addComponent(nuevoArchivo2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nuevoArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoArchivoActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser(home + "/Pictures/");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                upload(server, fc.getSelectedFile(), new File("archivos/" + fc.getSelectedFile().getName()));
            } catch (IOException ex) {
                Logger.getLogger(TablaUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_nuevoArchivoActionPerformed

    private void tablaDatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaDatosMouseClicked
        String archivo = tablaDatos.getValueAt(tablaDatos.getSelectedRow(), 0).toString();
        try {
            download(server, new File("archivos/" + archivo), new File(home + "/Desktop/" + archivo));

        } catch (IOException ex) {
            Logger.getLogger(TablaUI.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en la descarga del archivo");
        }
            // do some actions here, for example
        // print first column value from selected row
//            String[] props=System.getProperties().toString().split(", ");
//            for (String prop : props) {
//                System.out.println(prop);
//            }                  
        System.out.println(" click en la fila " + tablaDatos.getSelectedRow());
    }//GEN-LAST:event_tablaDatosMouseClicked

    private void nuevoArchivo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoArchivo2ActionPerformed
        // TODO add your handling code here:    
        String url = "rmi://192.168.1.77/escritura";
        System.out.println(url);
        try {
            server = (Server) Naming.lookup(url);
        } catch (Exception ex) {
            Logger.getLogger(TablaUI.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Servidor de escritura no encontrado");
        }
        JFileChooser fc = new JFileChooser(home + "/Pictures/");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                upload(server, fc.getSelectedFile(), new File("archivos/" + fc.getSelectedFile().getName()));
            } catch (IOException ex) {
                Logger.getLogger(TablaUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_nuevoArchivo2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TablaUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TablaUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TablaUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TablaUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TablaUI("192.168.1.77").setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(TablaUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton nuevoArchivo;
    private javax.swing.JButton nuevoArchivo2;
    private javax.swing.JTable tablaDatos;
    // End of variables declaration//GEN-END:variables
    private DefaultTableModel datos;
    private Server server;
    private String temp_path;
    private String home;
}
