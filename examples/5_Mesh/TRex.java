import bGLOOP.*;
class TRex {
    //Kamera und Licht    
    GLKamera dieKamera;
    GLLicht dasLicht;
    GLHimmel derHimmel;

    //Objekte des TRex
    GLMesh trex;

    TRex(){
        //Kamera und Licht erstellen
        dieKamera  = new GLSchwenkkamera();
        dasLicht = new GLLicht();
        derHimmel = new GLHimmel();

        trex = new GLMesh("dino_milimeter.obj");
        //trex.setzeDarstellungsModus(GLObjekt.Darstellungsmodus.LINIE);
        //trex.setzeDarstellungsModus(GLObjekt.Darstellungsmodus.PUNKT);
        trex.setzeDarstellungsModus(GLObjekt.Darstellungsmodus.FUELLEN);
    }
}
