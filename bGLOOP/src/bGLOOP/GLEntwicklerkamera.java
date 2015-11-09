package bGLOOP;

import com.jogamp.newt.event.KeyEvent;

import bGLOOP.windowimpl.listener.KeyboardListenerFacade;

/** Die Entwicklerkamera hat zusätzliche Fähigkeiten gegenüber der
 * {@link GLSchwenkkamera}. Die dargestellte Szene kann durch folgende Tasten
 * verändert werden:
 * <ul>
 * <li><code>g</code>: Wechselt zwischen individueller und
 * Drahtgitterdarstellung.</li>
 * <li><code>a</code>: Blendet die Koordinatenachsen ein und aus</li>
 * <li><code>s</code>: Setzt die Kamera auf <code>(0,0,500)</code> mit Blickpunkt
 * auf <code>(0,0,0)</code> </li>
 * <li><code>&#8593;</code>: Rückt die Kamera ein Stück nach oben</li>
 * <li><code>&#8595;</code>: Rückt die Kamera ein Stück nach unten</li>
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

	/**
	 * Erstellt eine bGLOOP-Entwicklerkamera. Die Kamera öffnet ein Fenster mit
	 * den Abmessungen aus der in der bGLOOP-Konfigurationsdatei eingetragenen
	 * Standardgrößen.
	 */
	public GLEntwicklerkamera() {
		this(GLWindowConfig.defaultWindowConfig.globalDefaultWidth,
				GLWindowConfig.defaultWindowConfig.golbalDefaultHeight);
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
		renderer.getWindow().addKeyboardListener(new KeyboardListenerFacade() {
			@Override
			public void handleKeyPressed(char key, int keycode) {
				switch (key) {
				case 'a':
					zeigeAchsen(!getWconf().aDisplayAxes);
					break; // "zeigeAchsen" does a scheduleRender()
				case 'g':
					getWconf().aWireframe = !getWconf().aWireframe;
					getRenderer().scheduleRender();
					break;
				case 's':
					aPos[0] = 0; aPos[1] = 0; aPos[2] = 500;
					aLookAt[0] = 0; aLookAt[1] = 0; aLookAt[2] = 0;
					aUp[0] = 0; aUp[1] = 1; aUp[2] = 0;
					getRenderer().scheduleRender();
					break;
				}
				switch (keycode) {
				case KeyEvent.VK_UP:
					aPos[0] += aUp[0]*10; aPos[1] += aUp[1]*10; aPos[2] += aUp[2]*10;
					aLookAt[0] += aUp[0]*10; aLookAt[1] += aUp[1]*10; aLookAt[2] += aUp[2]*10;
					getRenderer().scheduleRender();
					break;
				case KeyEvent.VK_DOWN:
					aPos[0] -= aUp[0]*10; aPos[1] -= aUp[1]*10; aPos[2] -= aUp[2]*10;
					aLookAt[0] -= aUp[0]*10; aLookAt[1] -= aUp[1]*10; aLookAt[2] -= aUp[2]*10;
					getRenderer().scheduleRender();
					break;
				}
			}

			@Override
			public void handleKeyReleased(char key, int keycode) {
			}
		});

	}
}
