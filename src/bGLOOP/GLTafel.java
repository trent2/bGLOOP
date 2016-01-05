package bGLOOP;

/** Die Klasse <code>GLTafel</code> stellt die gleiche Funktionalität wie die {@link GLText}
 * zur Verfügung. Im Gegensatz zu <code>GLText</code> ist wird der Text standardmäßig mit einem
 * opaken Hintergrund dargestellt, die automatische Ausrichtung zur Kamera ist aktiviert
 * und der Text wird an der Position horizontal und vertikal zentriert dargestellt.  
 * @author R. Spillner
 * @deprecated Diese Klasse ist veraltet und verbleibt aus Gründen der Kompatitbilität mit
 * <code>GLOOP</code> zunächst in der Bibliothek.
 */
@Deprecated
public class GLTafel extends GLText {

	/** Erzeugt einen {@link GLTafel}-Objekt. Die Schriftgröße ist dabei ein Näherungswert an
	 * die Schriftgröße eines Textverarbeitungsprogramm (in Punkten), wenn der
	 * Text in der Standard-Ansicht (also <code>new GLKamera()</code> ohne weitere
	 * Transformation) im Ursprung gezeichnet wird. Der Text wird mit einem Hintergrund
	 * gezeichnet und die automatische Ausrichtung ist aktiviert.
	 * @param pX x-Koordinate der linken unteren Ecke des Textes
	 * @param pY y-Koordinate der linken unteren Ecke des Textes
	 * @param pZ z-Koordinate der linken unteren Ecke des Textes
	 * @param pSchriftgroesse Näherungswert an Schriftgröße in Punkten (s. Beschreibung oben)
	 * @param pText der darzustellende Text 
	 */
	public GLTafel(double pX, double pY, double pZ, double pSchriftgroesse, String pText) {
		super(pX, pY, pZ, pSchriftgroesse, pText);
		setzeTafel(true);
		setzeAutodrehung(true);
		setzeZentrierung(true);
	}

	/** Erzeugt einen {@link GLTafel}-Objekt. Die Schriftgröße wird dabei auf
	 * 12pt gesetzt (vgl. {@link #GLTafel(double, double, double, double, String) Beschreibung des
	 * obigen Konstruktors}).
	 * @param pX x-Koordinate der linken unteren Ecke des Textes
	 * @param pY y-Koordinate der linken unteren Ecke des Textes
	 * @param pZ z-Koordinate der linken unteren Ecke des Textes
	 * @param pText der darzustellende Text 
	 */
	public GLTafel(double pX, double pY, double pZ, String pText) {
		this(pX, pY, pZ, 12, pText);
	}
}
