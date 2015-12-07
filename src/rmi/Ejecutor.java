/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
import java.awt.image.BufferedImage;
import java.rmi.*;
/**
 *
 * @author Miguel
 */
public interface Ejecutor extends Remote {
   public Imagen ejecutar(AEjecutar tarea) throws RemoteException;
}
