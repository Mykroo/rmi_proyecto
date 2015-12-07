/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Miguel
 */
public class ImplementacionAEjecutar extends UnicastRemoteObject implements  AEjecutar {
    private BufferedImage imagen;
    private Imagen img;
    private String localizacion,tipoRenderizacion;
    protected ImplementacionAEjecutar(String local,String tipo) throws RemoteException{
        super();
        this.localizacion=local;
        this.tipoRenderizacion=tipo;
    }
    public Imagen renderizar() throws RemoteException{
        try {
            img=new Imagen(localizacion);
        } catch (Exception ex) {
        }
        System.out.println("Se llamo interface");
        try {
            imagen=ImageIO.read(new File(localizacion));
        } catch (IOException ex) {
            Logger.getLogger(ImplementacionAEjecutar.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(tipoRenderizacion.contentEquals("Negativo")){
            aNegativo();
        }else if(tipoRenderizacion.contentEquals("BlancoNegro")){
            aBN();
        }else{
            aEscalaG();
        }
        img.setBufferedImage(imagen);
        return img;
    }
    private void aNegativo(){
        int r,g,b;
        Color color;
        for(int i=0;i<imagen.getWidth();i++){
            for(int j=0;j<imagen.getHeight();j++){
                color=new Color(imagen.getRGB(i, j));
                r=color.getRed();
                g=color.getGreen();
                b=color.getBlue();
                imagen.setRGB(i, j, new Color(255-r,255-g,255-b).getRGB());
            }
        }
    }
    private void aBN(){
        int r,g,b;
        Color color;
        BufferedImage blancoNegro=new BufferedImage(imagen.getWidth(),imagen.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
        Graphics g2=blancoNegro.getGraphics();
        g2.drawImage(imagen,0,0, null);
        g2.dispose();
        for(int i=0;i<blancoNegro.getWidth();i++){
            for(int j=0;j<blancoNegro.getHeight();j++){
                color=new Color(blancoNegro.getRGB(i, j));
                r=color.getRed();
                g=color.getGreen();
                b=color.getBlue();
                if(r>127)
                    r=255;
                else
                    r=0;
                if(g>127)
                    g=255;
                else
                    g=0;
                if(b>127)
                    b=255;
                else
                    b=0;
                blancoNegro.setRGB(i, j, new Color(r,g,b).getRGB());
                imagen=blancoNegro;
            }
        }
    }
    private void aEscalaG(){
        int r,g,b,gris;
        Color color;
        for(int i=0;i<imagen.getWidth();i++){
            for(int j=0;j<imagen.getHeight();j++){
                color=new Color(imagen.getRGB(i, j));
                r=color.getRed();
                g=color.getGreen();
                b=color.getBlue();
                gris=(r+g+b)/3;
                imagen.setRGB(i, j, new Color(gris,gris,gris).getRGB());
            }
        }
    }

}
