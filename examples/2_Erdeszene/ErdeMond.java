import bGLOOP.*;
class ErdeMond {
    GLKamera   dieKamera;    
    GLLicht    dasLicht;    
    GLKugel    dieErde, derMond;
    GLTastatur dieTastatur;

    ErdeMond(){
        dieKamera  = new GLSchwenkkamera();
        dasLicht = new GLLicht();        
        dieTastatur = new GLTastatur();
        
        dieErde = new GLKugel (0,0,0,150);
        dieErde.setzeTextur ("Erde.jpg"); 
        dieErde.drehe(-90,0,0);

        derMond = new GLKugel (250,0,0,50);
        derMond.setzeTextur ("Mond.jpg");
        derMond.drehe(-90,0,0);
      
        while (!dieTastatur.esc()){
            dieErde.drehe(0,0.2,0);          
            derMond.drehe(0,-0.25,0,  0,0,0);            
            Sys.warte(5);
        }
        Sys.beenden();
    }
}


