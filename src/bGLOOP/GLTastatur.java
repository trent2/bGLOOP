package bGLOOP;

import java.util.logging.Logger;

import com.jogamp.newt.event.KeyEvent;

import bGLOOP.windowimpl.listener.KeyboardListenerFacade;

/** <p>Klasse, mit der man Tastatureingaben abfragen kann.</p>
 * <p>Es wird zwischen <em>normalen</em> und <em>speziellen</em> Tastendrucks
 * unterschieden: Normale Zeichen sind i.d.R. druckbare Zeichen, wie alle Buchstaben,
 * Satzzeichen und Zahlen. Sie können als <code>char</code> durch die Methode
 * {@link #gibZeichen()} ermittelt werden. Ihre Eingabe wird gepuffert, d.h. sie
 * können auch nach Loslassen der entsprechenden Taste abgefragt werden.
 * </p><p>
 * Spezielle Zeichen hingegen können nicht gedruckt werden und eine Abfrage nach
 * ihnen ist nur so lange positiv, wie die Taste heruntergedrückt ist. Zu ihnen
 * gehören die Modifizierer-Tasten (ALT, STRG, Umschalten) sowie die Pfeiltasten,
 * Enter, Backspace (Rücklöschtaste), Tab und ESC.
 * </p><p>
 * Die Implementierung dieser Klasse ist momentan noch auf Kompabilität zu GLOOP
 * hin ausgelegt, wird sich aber wahrscheinlich in Zukunft ändern. 
 * </p>
 * @author R. Spillner
 *
 */
public class GLTastatur {

	static private class KeyLog {
		private final static long MAX_DIFF_TIME = 30; 
		long aKeyReleaseTime = -1;
		boolean isPressed = false;

		private static boolean useStaticCurrentTime = false;
		private static long lastCallCurrentTime = -1;

		void recordRelease() {
			aKeyReleaseTime = System.currentTimeMillis();
			isPressed = false;
		}

		boolean isHeld() {
			if(useStaticCurrentTime)
				return isPressed ||lastCallCurrentTime - aKeyReleaseTime < MAX_DIFF_TIME;
			else
				return isPressed || System.currentTimeMillis()- aKeyReleaseTime < MAX_DIFF_TIME;
		}
	}

	Logger log = Logger.getLogger("bGLOOP");
	private KeyLog aAlt = new KeyLog();
	private KeyLog aBack = new KeyLog();
	private KeyLog aEsc = new KeyLog();
	private KeyLog aEnter = new KeyLog();
	private KeyLog aLeft = new KeyLog();
	private KeyLog aUp = new KeyLog();
	private KeyLog aRight = new KeyLog();
	private KeyLog aDown = new KeyLog();
	private KeyLog aShift = new KeyLog();
	private KeyLog aStrg = new KeyLog();
	private KeyLog aTab = new KeyLog();

	private String cbuf = "";
	
	/** Erzeugt ein GLTastatur-Objekt zur Abfrage der Tastatur.
	 */
	public GLTastatur() {
		GLKamera.aktiveKamera().associatedRenderer.getWindow().addKeyboardListener(new KeyboardListenerFacade() {
			@Override
			public void handleKeyPressed(char key, int keycode, int modifiers) {
				// keycodes for NEWT KeyEvent and AWT KeyEvent are
				// identical (whew)
				switch(keycode) {
				case KeyEvent.VK_ALT:
					aAlt.isPressed = true;
					break;
				case KeyEvent.VK_CONTROL:
					aBack.isPressed = true;
					break;
				case KeyEvent.VK_SHIFT:
					aShift.isPressed = true;
					break;
				case KeyEvent.VK_LEFT:
					aLeft.isPressed = true;
					break;
				case KeyEvent.VK_RIGHT:
					aRight.isPressed = true;
					break;
				case KeyEvent.VK_UP:
					aUp.isPressed = true;
					break;
				case KeyEvent.VK_DOWN:
					aDown.isPressed= true;
					break;
				case KeyEvent.VK_ESCAPE:
					aEsc.isPressed = true;
					break;
				case KeyEvent.VK_TAB:
					aTab.isPressed = true;
					break;
				case KeyEvent.VK_ENTER:
					aEnter.isPressed = true;
					break;
				case KeyEvent.VK_BACK_SPACE:
					aBack.isPressed = true;
					break;
				default:
					synchronized (cbuf) {
						cbuf += key;	
					}
				}
			}

			@Override
			public void handleKeyReleased(char key, int keycode) {
				switch(keycode) {
				case KeyEvent.VK_ALT:
					aAlt.recordRelease();
					break;
				case KeyEvent.VK_CONTROL:
					aBack.recordRelease();
					break;
				case KeyEvent.VK_SHIFT:
					aShift.recordRelease();
					break;
				case KeyEvent.VK_LEFT:
					aLeft.recordRelease();
					break;
				case KeyEvent.VK_RIGHT:
					aRight.recordRelease();
					break;
				case KeyEvent.VK_UP:
					aUp.recordRelease();
					break;
				case KeyEvent.VK_DOWN:
					aDown.recordRelease();
					break;
				case KeyEvent.VK_ESCAPE:
					aEsc.recordRelease();
					break;
				case KeyEvent.VK_TAB:
					aTab.recordRelease();
					break;
				case KeyEvent.VK_ENTER:
					aEnter.recordRelease();
					break;
				case KeyEvent.VK_BACK_SPACE:
					aBack.recordRelease();
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
		char c;
		synchronized (cbuf) {
			if (!cbuf.isEmpty()) {
				c = cbuf.charAt(0);
				cbuf = cbuf.substring(1);
			} else
				c = 0;
		}
		return c;
	}

	/** Liefert true, wenn ein normales Zeichen ({@link #gibZeichen()}) im Tastaturpuffer
	 * vorhanden ist.
	 * @return Liefert <code>true</code>, wenn ein normales Zeichen im Puffer
	 * vorhanden ist, sonst <code>false</code>.
	 */
	public boolean wurdeGedrueckt() {
		synchronized (cbuf) {
			return !cbuf.isEmpty();
		}
	}

	/** Liefert true, wenn irgendeine Taste (speziell oder normal) gedrückt wurde.
	 * @return <code>true</code>, wenn irgendeine Taste gedrückt wurde, sonst
	 * <code>false</code>
	 */
	public boolean istGedrueckt() {
		KeyLog.useStaticCurrentTime = true;
		KeyLog.lastCallCurrentTime = System.currentTimeMillis();
		boolean r = wurdeGedrueckt() || aBack.isHeld() || aEsc.isHeld() || aEnter.isHeld() || aLeft.isHeld() || aUp.isHeld()
				|| aRight.isHeld() || aShift.isHeld() || aStrg.isHeld() || aTab.isHeld() || aDown.isHeld();
		KeyLog.useStaticCurrentTime = false;
		return r;
	}

	/** Leert den Puffer der normalen Zeichen.
	 */
	public void loeschePuffer() {
		synchronized (cbuf) {
			cbuf = "";
		}
	}

	/**
	 * @return <code>true</code>, wenn die Alt-Taste gerade gedrückt wird.
	 */
	public boolean alt() {
		return aAlt.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die Backspace-Taste gerade gedrückt wird.
	 */
	public boolean backspace() {
		return aBack.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die ESC-Taste gerade gedrückt wird.
	 */
	public boolean esc() {
		return aEsc.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die Enter-Taste gerade gedrückt wird.
	 */
	public boolean enter() {
		return aEnter.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die linke Pfeiltaste gerade gedrückt wird.
	 */
	public boolean links() {
		return aLeft.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die Pfeil-Nach-Oben-Taste gerade gedrückt wird.
	 */
	public boolean oben() {
		return aUp.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die rechte Pfeiltaste gerade gedrückt wird.
	 */
	public boolean rechts() {
		return aRight.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die Shift-Taste gerade gedrückt wird.
	 */
	public boolean shift() {
		return aShift.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die Strg-Taste gerade gedrückt wird.
	 */
	public boolean strg() {
		return aStrg.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die Tab-Taste gerade gedrückt wird.
	 */
	public boolean tab() {
		return aTab.isHeld();
	}

	/**
	 * @return <code>true</code>, wenn die Pfeil-Nach-Unten-Taste gerade gedrückt wird.
	 */
	public boolean unten() {
		return aDown.isHeld();
	}
}
