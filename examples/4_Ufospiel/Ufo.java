import bGLOOP.*;
public class Ufo {
    GLTorus hRumpf;
    GLKugel hCockpit;
    GLZylinder hBoden;  
    GLKegel hFluegel1, hFluegel2;

    public Ufo (){
        hRumpf = new GLTorus(0,0,0,50,20);
        hRumpf.skaliere (0.5,1,0.5);

        hCockpit = new GLKugel(0,0,0,25);
        hCockpit.setzeMaterial(GLMaterial.RUBIN);

        hBoden = new GLZylinder (0,0,0,50,10);
        hBoden.skaliere (0.5,1,1);

        hFluegel1 = new GLKegel(-60,-10,0,15,70);
        hFluegel1.skaliere(0.2,1,1);        
        hFluegel1.rotiere(90,0,1,0,-60,10,0);
        hFluegel1.rotiere(45,0,0,1,0,0,0);
        hFluegel1.setzeFarbe(1,0,0);

        hFluegel2 = new GLKegel(60,-10,0,15,70);
        hFluegel2.skaliere(0.2,1,1);        
        hFluegel2.rotiere(-90,0,1,0,60,10,0);
        hFluegel2.rotiere(-45,0,0,1,0,0,0);
        hFluegel2.setzeFarbe(1,0,0);

        hRumpf.drehe(-90,0,0,0,0,0);
        hBoden.drehe(-90,0,0,0,0,0);
        hCockpit.drehe(-90,0,0,0,0,0);
        hFluegel1.drehe(-90,0,0,0,0,0);
        hFluegel2.drehe(-90,0,0,0,0,0);
    }

    public double gibX(){
        return hRumpf.gibX();
    }

    public double gibY(){
        return hRumpf.gibY();
    }

    public double gibZ(){
        return hRumpf.gibZ();
    }

    public void bewegeLinks(){
        hRumpf.verschiebe(-10,0,0);
        hBoden.verschiebe(-10,0,0);
        hCockpit.verschiebe(-10,0,0);
        hFluegel1.verschiebe(-10,0,0);
        hFluegel2.verschiebe(-10,0,0);
    }

    public void bewegeRechts(){       
        hRumpf.verschiebe(10,0,0);
        hBoden.verschiebe(10,0,0);
        hCockpit.verschiebe(10,0,0);
        hFluegel1.verschiebe(10,0,0);
        hFluegel2.verschiebe(10,0,0);
    }

    public void bewegeOben(){       
        hRumpf.verschiebe(0,10,0);
        hBoden.verschiebe(0,10,0);
        hCockpit.verschiebe(0,10,0);
        hFluegel1.verschiebe(0,10,0);
        hFluegel2.verschiebe(0,10,0);
    }

    public void bewegeUnten(){       
        hRumpf.verschiebe(0,-10,0);
        hBoden.verschiebe(0,-10,0);
        hCockpit.verschiebe(0,-10,0);
        hFluegel1.verschiebe(0,-10,0);
        hFluegel2.verschiebe(0,-10,0);
    }

    public void explodiere(){
        while (true){
            hRumpf.verschiebe(0.5,-0.25,1.1);
            hRumpf.drehe(1,0,0.3);
            hBoden.verschiebe(-0.5,-0.25,1.1);
            hBoden.drehe(2,4,-5);
            hCockpit.verschiebe(-0.25,1.1,0.5);
            hCockpit.drehe(-3,2,1);
            hFluegel1.verschiebe(0.5,1.1,-0.75);
            hFluegel1.drehe(0.23,2,0);
            hFluegel2.verschiebe(0.15,-0.9,-0.45);
            hFluegel2.drehe(0.23,0.11,0.1);
            Sys.warte(5);
        }
    }

}