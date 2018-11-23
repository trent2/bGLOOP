import bGLOOP.*;
public class Uhr{
    //Kamera und Licht
    GLSchwenkkamera dieKamera;
    GLLicht dasLicht;
    GLTastatur dieTastatur;

    //Ziffernblatt
    Ziffernblatt dasZiffernblatt;

    //Zeiger
    Zeiger derSekundenzeiger, derMinutenzeiger, derStundenzeiger;

    public Uhr(){
        //Kamera und Licht
        dieKamera = new GLSchwenkkamera();
        dasLicht = new GLLicht();
        dieTastatur = new GLTastatur();

        //Das Ziffernblatt
        dasZiffernblatt = new Ziffernblatt();

        //Die Zeiger
        derSekundenzeiger = new Zeiger(0,0,150,2,360.0/60);
        derMinutenzeiger  = new Zeiger(0,0,150,5,360.0/(60*60));
        derStundenzeiger = new Zeiger(0,0,80,5,  360.0/(60*60*24));
        derSekundenzeiger.setzeFarbe(1,0,0);

        //Animationsschleife
        while (!dieTastatur.esc()){
            derSekundenzeiger.weiter();    
            derMinutenzeiger.weiter();
            derStundenzeiger.weiter();

            Sys.warte(100);
        }
        Sys.beenden();
    }
}
