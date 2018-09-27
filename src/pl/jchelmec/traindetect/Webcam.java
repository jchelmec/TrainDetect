package pl.jchelmec.traindetect;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;


public class Webcam {
	BufferedImage nowyobraz;
	BufferedImage nowyobrazBS;
	int rows;
	int cols;
	VideoCapture capture;
	Mat matrix;
	BackgroundSubtractorMOG2 mog2 = Video.createBackgroundSubtractorMOG2();
	Mat mask, bg;
	ConvertImage conv1 = new ConvertImage();
	ConvertImage conv2 = new ConvertImage();
	MatOfRect trackresult;
	MatOfInt numdetect;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		}
	
	Mat getMask(Mat matrix){
		mog2.apply(matrix, mask);
		return mask;
	}
	
	Webcam(){
	 
	      
//	      Imgcodecs imgcodecs = new Imgcodecs();
		 capture = new VideoCapture(0);
		 matrix = new Mat();
		 mask = new Mat();
		 
		
		 // If camera is opened
	      if(!capture.isOpened()) {
	         System.out.println("camera not detected");
	      } else
	         System.out.println("Camera detected ");
		 
//	     Mat matrix = Imgcodecs.imread("DSC_0722.jpg",Imgcodecs.IMREAD_COLOR);
//	     Imgcodecs.imwrite("obraz.jpg", matrix);
	     
	      
	}
	
	public BufferedImage getOneFrame(){
		BufferedImage nowyobraz = null;
		
		capture.read(matrix);
		mask = getMask(matrix);
//		System.out.println("Image loaded");
//		cols = matrix.cols();
//		rows = matrix.rows();
//		System.out.println("X: " + cols + " Y: " + rows);
		nowyobraz = conv1.convert(matrix);
		return nowyobraz;
	}
	
	public BufferedImage getOneBS(){
		mask = getMask(matrix);
		
//		CascadeClassifier cascade = new CascadeClassifier();
//		cascade.detectMultiScale2(mask, trackresult, numdetect);
//		System.out.println(trackresult.dump() );
		
		nowyobrazBS = conv2.convert(mask);
		return nowyobrazBS;
	}
}

class ConvertImage{

	public BufferedImage convert(Mat matrix){
		BufferedImage obraz=null;
			
		MatOfByte imagemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, imagemat);
		byte[] byteArray = imagemat.toArray();
		
		InputStream in = new ByteArrayInputStream(byteArray);
		BufferedImage image;
		try {
			image = ImageIO.read(in);
			obraz = image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obraz;
		
	}
}