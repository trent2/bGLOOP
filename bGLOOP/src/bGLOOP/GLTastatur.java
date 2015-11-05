package bGLOOP;

import bGLOOP.windowimpl.KeyboardListener.KeyPressedLogic;

/** <p>Klasse, mit der man Tastatureingaben abfragen kann.</p>
 * @author R. Spillner
 *
 */
public class GLTastatur {
	private boolean aAlt = false;
	private boolean aBack = false;
	private boolean aEsc = false;
	private boolean aEnter = false;
	private boolean aLeft = false;
	private boolean aUp = false;
	private boolean aRight = false;
	private boolean aShift = false;
	private boolean aStrg = false;
	private boolean aTab = false;
	private boolean aDown = false;

	private String cbuf = "";
	
	/** Erzeugt ein GLTastatur-Objekt zur Abfrage der Tastatur.
	 */
	public GLTastatur() {
		GLKamera.aktiveKamera().getRenderer().getWindow().addKeyboardListener(new KeyPressedLogic() {
			@Override
			public void handleKeyPressed(char key, int keycode) {
				// keycodes for NEWT KeyEvent and AWT KeyEvent are
				// identical (whew)
				switch(keycode) {
				case java.awt.event.KeyEvent.VK_ALT:
					aAlt = true;
					break;
				case java.awt.event.KeyEvent.VK_CONTROL:
					aBack = true;
					break;
				case java.awt.event.KeyEvent.VK_SHIFT:
					aShift = true;
					break;
				case java.awt.event.KeyEvent.VK_LEFT:
					aLeft = true;
					break;
				case java.awt.event.KeyEvent.VK_RIGHT:
					aRight = true;
					break;
				case java.awt.event.KeyEvent.VK_UP:
					aUp = true;
					break;
				case java.awt.event.KeyEvent.VK_DOWN:
					aDown = true;
					break;
				case java.awt.event.KeyEvent.VK_ESCAPE:
					aEsc = true;
					break;
				case java.awt.event.KeyEvent.VK_TAB:
					aTab = true;
					break;
				case java.awt.event.KeyEvent.VK_ENTER:
					aEnter = true;
					break;
				case java.awt.event.KeyEvent.VK_BACK_SPACE:
					aBack = true;
					break;
				default:
					cbuf += key;
				}
			}

			@Override
			public void handleKeyReleased(char key, int keycode) {
				switch(keycode) {
				case java.awt.event.KeyEvent.VK_ALT:
					aAlt = true;
					break;
				case java.awt.event.KeyEvent.VK_CONTROL:
					aBack = true;
					break;
				case java.awt.event.KeyEvent.VK_SHIFT:
					aShift = true;
					break;
				case java.awt.event.KeyEvent.VK_LEFT:
					aLeft = true;
					break;
				case java.awt.event.KeyEvent.VK_RIGHT:
					aRight = true;
					break;
				case java.awt.event.KeyEvent.VK_UP:
					aUp = true;
					break;
				case java.awt.event.KeyEvent.VK_DOWN:
					aDown = true;
					break;
				case java.awt.event.KeyEvent.VK_ESCAPE:
					aEsc = true;
					break;
				case java.awt.event.KeyEvent.VK_TAB:
					aTab = true;
					break;
				case java.awt.event.KeyEvent.VK_ENTER:
					aEnter = true;
					break;
				case java.awt.event.KeyEvent.VK_BACK_SPACE:
					aBack = true;
					break;
				}
			}
		});
		;
	}

	/** <p>Gibt das nächste normale Zeichen aus dem Tastaturpuffer zurück und löscht es
	 * aus diesem. Dies beinhaltet alle Buchstaben und Zahlen oder Interpunktionszeichen.
	 * Spezielle Tasten wie STRG, ALT, SHIFT, Pfeiltasten usw. können auf diesem
	 * Weg nicht direkt abfragt werden.</p>
	 * <p>Das Zeichen kann dann durch diese Methode nicht erneut abgefragt
	 * werden, wenn es nicht erneut gedrückt wird.</p>
	 * @return Das nächste Zeichen im Tastaturpuffer. Wenn dieser leer ist, so wird 0
	 * zurück gegeben.
	 */
	public char gibZeichen() {
		if(!cbuf.isEmpty()) {
			char c = cbuf.charAt(0);
			cbuf = cbuf.substring(1);
			return c;
		} else
			return 0;
	}

	/** Liefert true, wenn ein normales Zeichen ({@link #gibZeichen()}) im Tastaturpuffer
	 * vorhanden ist.
	 * @return Liefert <code>true</code>, wenn ein normales Zeichen im Puffer
	 * vorhanden ist, sonst <code>false</code>.
	 */
	public boolean wurdeGedrueckt() {
		return !cbuf.isEmpty();
	}

	/** Liefert true, wenn irgendeine Taste (speziell oder normal) gedrückt wurde.
	 * @return <code>true</code>, wenn irgendeine Taste gedrückt wurde, sonst
	 * <code>false</code>
	 */
	public boolean istGedrueckt() {
		return wurdeGedrueckt() ||	aBack || aEsc || aEnter || aLeft || aUp || aRight ||
		aShift || aStrg || aTab || aDown;
	}

	/** Leert den Puffer der normalen Zeichen.
	 */
	public void loeschePuffer() {
		cbuf = "";
	}

	/**
	 * @return <code>true</code>, wenn die Alt-Taste gerade gedrückt wird.
	 */
	public boolean alt() {
		return aAlt;
	}

	/**
	 * @return <code>true</code>, wenn die Backspace-Taste gerade gedrückt wird.
	 */
	public boolean backspace() {
		return aBack;
	}

	/**
	 * @return <code>true</code>, wenn die ESC-Taste gerade gedrückt wird.
	 */
	public boolean esc() {
		return aEsc;
	}

	/**
	 * @return <code>true</code>, wenn die Enter-Taste gerade gedrückt wird.
	 */
	public boolean enter() {
		return aEnter;
	}

	/**
	 * @return <code>true</code>, wenn die linke Pfeiltaste gerade gedrückt wird.
	 */
	public boolean links() {
		return aLeft;
	}

	/**
	 * @return <code>true</code>, wenn die Pfeil-Nach-Oben-Taste gerade gedrückt wird.
	 */
	public boolean oben() {
		return aUp;
	}

	/**
	 * @return <code>true</code>, wenn die rechte Pfeiltaste gerade gedrückt wird.
	 */
	public boolean rechts() {
		return aRight;
	}

	/**
	 * @return <code>true</code>, wenn die Shift-Taste gerade gedrückt wird.
	 */
	public boolean shift() {
		return aShift;
	}

	/**
	 * @return <code>true</code>, wenn die Strg-Taste gerade gedrückt wird.
	 */
	public boolean strg() {
		return aStrg;
	}

	/**
	 * @return <code>true</code>, wenn die Tab-Taste gerade gedrückt wird.
	 */
	public boolean tab() {
		return aTab;
	}

	/**
	 * @return <code>true</code>, wenn die Pfeil-Nach-Unten-Taste gerade gedrückt wird.
	 */
	public boolean unten() {
		return aDown;
	}
}
