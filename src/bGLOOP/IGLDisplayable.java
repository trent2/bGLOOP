package bGLOOP;

import bGLOOP.GLObjekt.Darstellungsmodus;

/** Methoden, die alle normalen bGLOOP-Objekte implementieren. Dies betrifft alle
 * von {@link GLObjekt} abgeleiteten Klassen außer {@link GLHimmel} und
 * {@link GLBoden}.
 * 
 * @author R. Spillner
 */
interface IGLDisplayable {
	/**
	 * Setzt den {@link GLObjekt.Darstellungsmodus} des Objekts.
	 * 
	 * @param dm Darstellungsmodus (<code>PUNKT</code>, <code>LINIE</code> oder
	 *            <code>GEFUELLT</code>)
	 */
	public void setzeDarstellungsModus(Darstellungsmodus dm);

	/** Stellt ein, ob das Objekt in der Szene sichtbar ist. Ein unsichtbares
	 * Objekt wird nicht gerendert, ist aber noch Teil der Szene.
	 * 
	 * @param pSichtbar Wenn <code>true</code>, so wird das Objekt
	 * gerendert, wenn <code>false</code>, dann nicht.
	 */
	public void setzeSichtbarkeit(boolean pSichtbar);
}
