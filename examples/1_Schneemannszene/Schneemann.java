import bGLOOP.*;
class Schneemann {
    //Kamera und Licht    
    GLKamera dieKamera;
    GLLicht dasLicht;

    //Himmel, Boden, Nebel
    GLHimmel derHimmel;
    GLBoden derBoden;
    //GLNebel derNebel;

    //Textur
    GLTextur texSchnee;

    //Objekte des Schneemanns
    GLKugel dasBein,derBauch,derKopf, k1,k2,k3, a1,a2;
    GLKegelstumpf dieNase;
    GLZylinder h1,h2;

    Schneemann(){
        //Kamera und Licht erstellen
        dieKamera  = new GLSchwenkkamera();
        dieKamera.setzePosition(-400,400,600);
        dasLicht = new GLLicht();    

        //Texture erstellen
        texSchnee = new GLTextur("Schnee.jpg");

        //Boden und Nebel erstellen
        derBoden  = new GLBoden (texSchnee);
        //derNebel = new GLNebel();
        //derNebel.setzeNebelbereich(0, 3000);

        //Schneemannkugeln
        dasBein   = new GLKugel (0,25,0,75,texSchnee);
        derBauch  = new GLKugel (0,130,0,55,texSchnee);
        derKopf   = new GLKugel (0,200,0,35,texSchnee); 

        //Knoepfe
        k1 = new GLKugel(0,130,55,5);
        k1.setzeFarbe(0,0,0);

        k2 = new GLKugel(0,130,55,5);
        k2.setzeFarbe(0,0,0); 
        k2.drehe(25,0,0, 0,130,0);

        k3 = new GLKugel(0,130,55,5);
        k3.setzeFarbe(0,0,0); 
        k3.drehe(-25,0,0, 0,130,0);

        //Augen
        a1 = new GLKugel(0,200,35,5);
        a1.setzeFarbe(1,0,0); 
        a1.drehe(-15,0,0, 0,200,0);
        a1.drehe(0,-15,0, 0,200,0);

        a2 = new GLKugel(0,200,35,5);
        a2.setzeFarbe(1,0,0); 
        a2.drehe(-15,0,0, 0,200,0);
        a2.drehe(0,15,0, 0,200,0);

        //Nase
        dieNase = new GLKegel(0,200,50,5,30);
        dieNase.drehe(0,180,0);
        dieNase.setzeFarbe(1,0.5,0);

        //Hut
        h1 = new GLZylinder(0,200,30,50,10);
        h1.drehe(-110,0,0, 0,200,0);
        h1.setzeFarbe(0.8,0.2,0);

        h2 = new GLZylinder(0,200,40,30,30);
        h2.drehe(-110,0,0, 0,200,0);        
        h2.setzeFarbe(0.8,0.2,0);
    }
}
