import bGLOOP.*;
public class Ufospiel{ 
    GLKamera dieKamera;
    GLLicht  dasLicht;
    GLTastatur dieTastatur;
    GLHimmel derHimmel;

    Ufo dasUfo;
    Asteroid[] derAsteroid; 

    public Ufospiel(){   
        //Kamera, Licht, Himmel und Tastatur
        dieKamera = new GLKamera(); 
        dieKamera.setzePosition(0,350,600);
        dieKamera.setzeBlickpunkt (0,200,0);
        dasLicht = new GLLicht();        
        derHimmel= new GLHimmel(new GLTextur("Sterne.jpg"));
        dieTastatur = new GLTastatur();

        //Ufo erstellen
        dasUfo = new Ufo();

        //Asteroiden erstellen und mit Ufo bekannt machen 
        GLTextur lMT = new GLTextur("Krater.jpg");
        derAsteroid = new Asteroid[60];
        for (int i = 0; i<60 ; i++)
            derAsteroid[i] = new Asteroid(lMT,dasUfo);            

        //Animationsschleife
        while (!dieTastatur.esc()){
            //Ufosteuerung
            if (dieTastatur.links())  dasUfo.bewegeLinks();
            if (dieTastatur.rechts()) dasUfo.bewegeRechts();
            if (dieTastatur.oben())   dasUfo.bewegeOben();
            if (dieTastatur.unten())  dasUfo.bewegeUnten();

            if (dieTastatur.shift()){
                for (int i = 0; i<60 ; i++){
                    derAsteroid[i].schneller(); 
                }
            }

            if (dieTastatur.strg()){
                for (int i = 0; i<60 ; i++){
                    derAsteroid[i].langsamer();       
                }
            }

            for (int i = 0; i<60 ; i++){
                derAsteroid[i].bewegeDich();       
            }

            Sys.warte(5);
        }
        Sys.beenden();
    }
}

