import bGLOOP.*;
class Airboat {
    //Kamera und Licht    
    GLKamera dieKamera;
    GLLicht dasLicht;
    GLHimmel derHimmel;

    //Objekte des Airboat
    GLMesh airboat;

    Airboat(){
        //Kamera und Licht erstellen
        dieKamera  = new GLSchwenkkamera();
        dasLicht = new GLLicht();
        derHimmel = new GLHimmel();

        airboat = new GLMesh("airboat.obj");
    }
}
