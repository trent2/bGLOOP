package bGLOOP;

/** Methoden, die von Objekten unterstützt werden, die in kleinere Polygone
 * unterteilbar sind.
 * @author R. Spillner
 */
interface IGLSubdivisable {
	/** Anzahl der polygonalen Unterteilungen des Modells. Jedem Objekt liegt ein
	 * Gittermodell zugrundeliegenden. Je größer die Anzahl der Unteilungen,
	 * desto feiner sind die Schattierungen auf der Oberfläche. Allerdings führt
	 * eine hohe Zahl von Polygonen in einer Szene zu langsamerer Darstellung
	 * und fallender Framerate.
	 * 
	 * @param pBreitengrade Anzahl der Unteilungen horizontalen Scheiben
	 * @param pLaengengrade Anzahl der Unteilungen vertikalen Scheiben
	 */
	public void setzeQualitaet(int pBreitengrade, int pLaengengrade);

	/** Anzahl der polygonalen Unterteilungen des Modells.
	 * 
	 * @see #setzeQualitaet(int,int)
	 * @param pUnterteilungen Anzahl der horizontalen und vertikalen
	 * Scheiben in der Unterteilung
	 */
	public void setzeQualitaet(int pUnterteilungen);
}
