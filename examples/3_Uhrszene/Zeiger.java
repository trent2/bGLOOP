import bGLOOP.*;
public class Zeiger extends GLZylinder{
    double zGeschwindigkeit;
    double zX,zY;

    public Zeiger(double pX, double pY, double pLänge, double pDicke, double pGeschwindigkeit){
        super(pX,pY+pLänge/2,0,pDicke,pLänge);
        this.drehe(90,0,0);
        zGeschwindigkeit = pGeschwindigkeit;
        zX = pX; zY = pY;      
    }    

    public void weiter(){
        this.drehe(0,0,-zGeschwindigkeit, 0,0,0);
    }
}
