package pl.jchelmec.traindetect;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Train extends JFrame {

	BufferedImage nowyobraz;
	Webcam obraz;
	JPanel panel;

/**
	 * Klasa rysujaca wunik rozpoznania
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	     //Rysowanie
	     
	     EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						new Train(100,100,645,485);
//						new TrainBackGround(100, 100, 645, 485);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}
	
	public Train(int x, int y, int w, int h){
		
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     panel = new JPanel();
	     getContentPane().add(panel);
	     setBounds(x,y,w,h);
	     
	       //nowyobraz = obraz.getImage();
	     setSize(640, 500);
	     
	  // TODO Auto-generated method stub
	     obraz = new Webcam();
	     			
	  	new DetectThread().start();
	  	setVisible(true);
	     
	}

	public void paint (Graphics g){
		super.paint(g);
		g = panel.getGraphics();
//		if (nowyobraz!=null){
//            setSize(nowyobraz.getWidth()+5, nowyobraz.getHeight()+5);
//            g.drawImage(nowyobraz, 0, 0, rootPane);
//        }
	}

	public class DetectThread extends Thread {

	
		@Override
		public void run() {
			while (panel == null || panel.getGraphics() == null) {
				try {Thread.sleep(1000);}
				catch (InterruptedException ex) {}
			}
			Graphics g = panel.getGraphics();
			nowyobraz = obraz.getOneFrame();
			if (nowyobraz!=null) {
				setSize(nowyobraz.getWidth(), nowyobraz.getHeight()+20);
			}
			while (true) {
				nowyobraz = obraz.getOneFrame();
				g.drawImage(nowyobraz, 0, 0, rootPane);
			}
				
		}
 
	}
}	


