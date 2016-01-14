package bGLOOP;

import java.util.Arrays;

import bGLOOP.windowimpl.listener.KeyboardListenerFacade;

/** Die Entwicklerkamera hat zusätzliche Fähigkeiten gegenüber der
 * {@link GLSchwenkkamera}. Die dargestellte Szene kann durch folgende Tasten
 * verändert werden:
 * <ul>
 * <li><code>g</code>: Wechselt zwischen individueller und
 * Drahtgitterdarstellung.</li>
 * <li><code>a</code>: Blendet die Koordinatenachsen ein und aus</li>
 * <li><code>b</code>: Blendet den Blickpunkt ein</li>
 * <li><code>f</code>: Wechselt zwischen Vollbild und Fenstermodus</li>
 * <li><code>c</code>: Gibt Kamera-Koordinaten und Blickpunkt auf der Konsole aus.</li> 
 * <li><code>t</code>: Macht ein Bildschirmfoto</li>
 * <li><code>Strg-q</code>: Schließt das aktuelle Kamerafenster und beendet so (zumeist)
 * das laufende Programm</li>  
 * </ul>
 * 
 * @author R. Spillner
 */
public class GLEntwicklerkamera extends GLSchwenkkamera {
	/**
	 * Erstellt eine bGLOOP-Entwicklerkamera mit zusätzlichen
	 * Fenster-Parametern. Das Fenster kann dabei im Vollbildmodus und/oder
	 * mit/ohne Fenstermanager- Dekorationen angezeigt werden.
	 * 
	 * @param pVollbild
	 *            Wenn <code>true</code>, dann wird die Anzeige direkt in den
	 *            Vollbildmodus geschaltet. Wenn <code>false</code>, dann nicht
	 * @param pKeineDekoration
	 *            Wenn <code>true</code>, dann wird das Anzeigefenster ohne
	 *            Dekoration des Fenstermanagers gezeichnet, wenn
	 *            <code>false</code>, dann nicht.
	 */
	public GLEntwicklerkamera(boolean pVollbild, boolean pKeineDekoration) {
		super(pVollbild, pKeineDekoration);
		addKeyboardListener();
	}

	public GLEntwicklerkamera(boolean pVollbild) {
		this(pVollbild, false);
	}

	/**
	 * Erstellt eine bGLOOP-Entwicklerkamera. Die Kamera öffnet ein Fenster mit
	 * den Abmessungen aus der in der bGLOOP-Konfigurationsdatei eingetragenen
	 * Standardgrößen.
	 */
	public GLEntwicklerkamera() {
		this(WindowConfig.defaultWindowConfig.globalDefaultWidth,
				WindowConfig.defaultWindowConfig.globalDefaultHeight);
	}

	/** Erstellt eine bGLOOP-Entwicklerkamera mit vorgegebener Breite und Höhe.
	 * Das Fenster wird nicht im Vollbild-Modus geöffnet.
	 * @param width Die Breite des Anzeigefensters
	 * @param height Die Höhe des Anzeigefensters
	 */
	public GLEntwicklerkamera(int width, int height) {
		super(width, height);
		addKeyboardListener();
	}

	private void addKeyboardListener() {
		associatedRenderer.getWindow().addKeyboardListener(new KeyboardListenerFacade() {
			@Override
			public void handleKeyPressed(char key, int keycode, int modifiers) {
				switch (key) {
				case 'a':  // show axis
					zeigeAchsen(!getWconf().aDisplayAxes);
					break; // "zeigeAchsen" does a scheduleRender()
				case 'b':  // show axis
					zeigeBlickpunkt(!getWconf().aDrawLookAt);
					break; // "zeigeAchsen" does a scheduleRender()
				case 'g':  // wireframe
					synchronized (GLEntwicklerkamera.this) {
						getWconf().aWireframe = !getWconf().aWireframe;
						associatedRenderer.scheduleRender();
					}
					break;
				case 'f':  // fullscreen
					associatedRenderer.getWindow().toggleFullscreen();
					break;
				case 't':
					associatedRenderer.scheduleScreenshot(null);
					break;
				case 'c':
					System.out.println("Kamera-Position: " + Arrays.toString(aPos));
					System.out.println("Kamera-Blickpunkt: " + Arrays.toString(aLookAt));
					break;
				}

				if((int)key == 17)  // ctrl-q --> quit
					associatedRenderer.getWindow().closeDisplay();
			}

			@Override
			public void handleKeyReleased(char key, int keycode) {
			}
		});
	}
}
