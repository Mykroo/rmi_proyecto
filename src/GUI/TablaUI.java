/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import ArchivosRMI.ServidorSencillo.Server;
import java.io.*;
import java.rmi.Naming;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mykro
 */
public class TablaUI extends javax.swing.JFrame {

    /**
     * Creates new form TablaUI
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
        copia (new FileInputStream(src), 
        server.getOutputStream(dest));
    }

    public static void download(Server server, File src,File dest) throws IOException {
        copia (server.getInputStream(src), 
        new FileOutputStream(dest));
    }
    public void coneccionServer(String serv) {
        System.out.println("aqui estoy " + serv);
        System.out.println("rmi://"+serv+"/server");        
        try {          
            String url = "rmi://"+serv+"/server";
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

    public void llenaTabla() {
        String[] columnNames = {"Archivo ",
            "Version",
            "Ultima escritura"};

        Object[][] data = {
            
        };

        datos = new DefaultTableModel(data, columnNames);
        tablaDatos.setModel(datos);
    }

    public TablaUI(String ipServ) throws Exception {

//        datos.addColumn("Nombre ");
//        datos.addColumn("Version ");
//        datos.addColumn("Ultima actualización");
        //datos.addRow();       
        System.out.println("constructor ui tabla: " + ipServ);
        initComponents();
        llenaTabla();
        coneccionServer(ipServ);
        System.out.println("Direccion del servidor en TablaUI: " + ipServ);

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
        jScrollPane2.setViewportView(tablaDatos);

        nuevoArchivo.setText("Agregar archivo al servidor");
        nuevoArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoArchivoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(nuevoArchivo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nuevoArchivo)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nuevoArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoArchivoActionPerformed
        // TODO add your handling code here:
        JFileChooser fc=new JFileChooser("/home/mykro/Pictures/");
        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            try {
                upload(server, fc.getSelectedFile(), new File("archivos/"+fc.getSelectedFile().getName()));
            } catch (IOException ex) {
                Logger.getLogger(TablaUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }//GEN-LAST:event_nuevoArchivoActionPerformed

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
                    new TablaUI("1ocalhost").setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(TablaUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton nuevoArchivo;
    private javax.swing.JTable tablaDatos;
    // End of variables declaration//GEN-END:variables
    private DefaultTableModel datos;
    private Server server;
}
