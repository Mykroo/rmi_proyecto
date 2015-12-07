/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *
 * @author Miguel
 */
public interface AEjecutar extends Remote{
    public Imagen renderizar() throws RemoteException;
}
