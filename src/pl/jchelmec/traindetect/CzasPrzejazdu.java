/**
 * 
 */
package pl.jchelmec.traindetect;

/**
 * @author Jarek
 *
 */
public class CzasPrzejazdu {

	/**
	 * 
	 */
	public CzasPrzejazdu() {
		// TODO Auto-generated constructor stub
	}
	
	public double czasStart;
	public double czasKoniec;
	public String kierunek;
	
	public void setStart(double czas) {
		this.czasStart = czas;
	}
	
	public void setKoniec(double czas) {
		this.czasKoniec = czas;
	}
	
	public void setKierunek(String kierunek) {
		this.kierunek = kierunek;
	}
	
	public double getTime() {
		return czasKoniec - czasStart;
	}
	
	public String toString() {
		
		return "Pocz¹tek: " + czasStart + "  Koniec: " + czasKoniec + "  Czas przejazdu: " + getTime() + "  Kierunek: " + kierunek;
	}
}
