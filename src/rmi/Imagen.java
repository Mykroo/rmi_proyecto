/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
/**
 *
 * @author Miguel
 */
public class Imagen implements Serializable{
    private String local;
    private BufferedImage imagen;
    public Imagen(String localizacion) throws IOException{
        local=localizacion;
       
    }
    public BufferedImage getBufferedImage(){
        return this.imagen;
    }
    public void setBufferedImage(BufferedImage img){
        this.imagen=img;
    }
    private void writeObject(java.io.ObjectOutputStream out)throws IOException{
      out.writeObject(local);
      ImageIO.write(imagen,"jpeg",ImageIO.createImageOutputStream(out));
    }

    private void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException{
      local=(String)in.readObject();
      imagen=ImageIO.read(ImageIO.createImageInputStream(in));
    }
}
