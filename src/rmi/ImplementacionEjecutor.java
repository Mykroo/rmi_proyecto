/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
import java.awt.image.BufferedImage;
import java.rmi.*;
import java.rmi.server.*;
import javax.swing.ImageIcon;
/**
 *
 * @author Miguel
 */
public class ImplementacionEjecutor extends UnicastRemoteObject implements Ejecutor{
    protected ImplementacionEjecutor() throws RemoteException{
        super();
    }
    public Imagen ejecutar(AEjecutar a) throws RemoteException{
        return a.renderizar();
    }


}
