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
	
	public void setStart(double czas) {
		this.czasStart = czas;
	}
	
	public void setKoniec(double czas) {
		this.czasKoniec = czas;
	}
	
	public double getTime() {
		return czasKoniec - czasStart;
	}

}
