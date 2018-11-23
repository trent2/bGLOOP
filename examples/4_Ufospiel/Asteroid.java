import bGLOOP.*;
class Asteroid extends GLKugel{
    double zRX,zRY,zRZ,zW;
    double zGeschwindigkeit = 5;
    double zRadius;
   
    Ufo kenntUfo;
    
    public Asteroid(GLTextur pT, Ufo pUfo){
        //Oberklassenkonstruktor mit Zufallspositionierung
        super(Math.random()*2000 - 1000, Math.random()*2000 -1000, -Math.random()*4000, 1);                               
        this.setzeTextur(pT);
        
        //Größe wählen
        zRadius = 20+Math.random()*40;
        this.skaliere(zRadius);
        
        //Zufallsrotation ermitteln
        zRX = Math.random()+0.1; zRY = Math.random(); zRZ = Math.random();

        //Kennt-Beziehung herstellen
        kenntUfo = pUfo;
    }

    public void bewegeDich(){
        //Asteroid bewegen
        this.verschiebe(0,0,zGeschwindigkeit);
        this.drehe(zRX,zRY,zRZ);
        if (this.gibZ()>300) {
            this.setzePosition(  
                Math.random()*2000 - 1000,
                Math.random()*2000 - 1000,
                -4000
            );
        }

        //Kollision testen
        double lDist = Math.sqrt(
                Math.pow(this.gibX()-kenntUfo.gibX(),2)+
                Math.pow(this.gibY()-kenntUfo.gibY(),2)+
                Math.pow(this.gibZ()-kenntUfo.gibZ(),2)
            );
        if (lDist<50+zRadius){
            kenntUfo.explodiere();
        }     

    }
    
    public void schneller(){
        zGeschwindigkeit = zGeschwindigkeit*1.01;
    }
    
    public void langsamer(){
        zGeschwindigkeit = zGeschwindigkeit*0.99;
    }
}
