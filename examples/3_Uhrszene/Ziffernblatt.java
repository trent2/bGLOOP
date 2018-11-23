import bGLOOP.*;
public class Ziffernblatt{
    GLZylinder hZylinder,hNabe;
    GLTorus hTorus;

    public Ziffernblatt(){
        hTorus = new GLTorus (0,0,0,215,15);
        hTorus.setzeTextur("Holz.jpg");
        hZylinder = new GLZylinder(0,0,-5, 210,5);
        hZylinder.setzeTextur("Leder.jpg");
        hNabe = new GLZylinder (0,0,0,10,5);
    }  

}
