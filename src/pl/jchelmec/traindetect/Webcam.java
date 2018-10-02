package pl.jchelmec.traindetect;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.KalmanFilter;
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
	ArrayList<Rect> rectArray = new ArrayList<>();
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		}
	
	Mat getMask(Mat matrix){
		mog2.apply(matrix, mask);
//		mog2.setVarMax(100);
		return mask;
	}
	
	Webcam(){
	 
	      
//	      Imgcodecs imgcodecs = new Imgcodecs();
		 capture = new VideoCapture(0);
		 matrix = new Mat();
		 mask = new Mat();
		 mog2.setHistory(500);
		 mog2.setVarThreshold(10);
	
		
		
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
		
//		mask = getMask(matrix);

		nowyobraz = conv1.convert(matrix);
		return nowyobraz;
	}
	
	public BufferedImage getOneBS(){
		
		capture.read(matrix);
		mask = getMask(matrix);
		List <MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Rect rect = null;
		double maxArea=100;
		
		
		for (int i=0; i<contours.size();i++) {
			Mat contour = contours.get(i);
			double contourArea = Imgproc.contourArea(contour);
			if (contourArea > maxArea) {
				rect = Imgproc.boundingRect(contours.get(i));
				if (rect.height > 100 && rect.width >100) {
					rectArray.add(rect);
					Imgproc.rectangle(matrix, new Point(rect.x,rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0,0,255),4);
				}
			}
		}
		
//			Imgproc.drawContours(mask, contours, 0, new Scalar(0, 60, 250));
//		for (int i = 0;i<rectArray.size();i++) {
//			System.out.println(rectArray.get(0));
//		}
		
		nowyobrazBS = conv2.convert(mask);
		return nowyobrazBS;
	}
	
	public ArrayList<Rect> getRectBS(){
		
		return rectArray;
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