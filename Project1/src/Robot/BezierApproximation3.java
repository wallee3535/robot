package Robot;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;

public class BezierApproximation3 extends Applet {
	Dot[] controlPoints;
	Dot curveChange;
	public int numDiv = 8;//number of points to plot per bezier curve
	ArrayList<Curve> rawCurves;
	ArrayList<Dot> rawPoints; 
	ArrayList<Curve> noCurvature;
	ArrayList<Dot> noCurvaturePoints; 
	ArrayList<Curve> smallAngle;
	
	ArrayList<Curve> noError;
	ArrayList<Dot> curvePoints; 
	
	public void init() {
		controlPoints = new Dot[4];
		curvePoints= new ArrayList<Dot>();
		noCurvature = new ArrayList<Curve>();
		noCurvaturePoints= new ArrayList<Dot>();
		smallAngle = new ArrayList<Curve>();
		noError = new ArrayList<Curve>();
		rawCurves = new ArrayList<Curve>();
		rawPoints= new ArrayList<Dot>();
		
		//ArrayList<Point> arcPoints = new ArrayList<Point>();
		//rawCurves.add(new Curve(20, 260, 50, 10, 250, 30, 300, 200));
		rawCurves.add(new Curve(20, 260, 50, 10, 250, 300, 300, 200));
		//rawCurves.add(new Curve(300, 200, 350, 250, 375, 35, 400, 200));
		controlPoints[0] = new Dot(20, 260);
		controlPoints[1] = new Dot(50, 10);
		controlPoints[2] = new Dot(250, 35);
		controlPoints[3] = new Dot(300, 200);
		//controlPoints[3]=new Point(450,290);
		Compute();
	}

	public void Compute() {
		
		Curve r= new Curve();
		Curve s= new Curve();
		
		Dot[] temp= new Dot[numDiv+1]; //initialize temp array for storing points for one curve
		for(Curve c : rawCurves){//for each curve\
		//for(int i=0; i<2; i++){
			//Curve c= rawCurves.get(i).clone();
			
			temp=c.dotData(numDiv);//generate points on the path and store in temp
			int n=curveChange(temp);//check for a curvature change
			
			if(n!=-1){//if curvature change found
				bezDiv(noCurvature, c,n);
			}else{
				noCurvature.add(c);
			}
		}
		for(Curve c : noCurvature){
			reduce(c, smallAngle);
		}
		

		for(Curve c : rawCurves) rawPoints.addAll(c.dotData2(numDiv));
		for(Curve c : noCurvature)noCurvaturePoints.addAll(c.dotData2(numDiv));
		//System.out.println(getAngle(noCurvaturePoints)*180/Math.PI);
	}
	public void bezDiv(Curve rawCurve, int index, Curve r, Curve s){
		double k=(double)index/numDiv;

		
		r.p1= rawCurve.p1.dup();
		//r.p2= addPoint(rawCurve.p1, scalePoint(subPoint(rawCurve.p2, rawCurve.p1), k));
		r.p2= rawCurve.p1.plus(rawCurve.p2.minus(rawCurve.p1).times(k));
		//r.p3= addPoint(r.p2, scalePoint(subPoint(addPoint(rawCurve.p2, scalePoint(  subPoint(rawCurve.p3, rawCurve.p2)  ,k)  ), r.p2)  , k));
		r.p3= rawCurve.p3.minus(rawCurve.p2).times(k).plus(rawCurve.p2).minus(r.p2).times(k).plus(r.p2);
		r.p4= bezExplicit(rawCurve, k);
		
		s.p1= r.p4.dup();
		//Dot p23=addPoint(rawCurve.p2, scalePoint(subPoint(rawCurve.p3, rawCurve.p2), k));
		Dot p23= rawCurve.p3.minus(rawCurve.p2).times(k).plus(rawCurve.p2);
		//s.p3= addPoint(rawCurve.p3,scalePoint(subPoint(rawCurve.p4, rawCurve.p3),k));
		s.p3= rawCurve.p4.minus(rawCurve.p3).times(k).plus(rawCurve.p3);
		//s.p2= addPoint(p23, scalePoint(subPoint(s.p3,p23),k));
		s.p2= s.p3.minus(p23).times(k).plus(p23);
		s.p4= rawCurve.p4.dup();
		
	}
	public void bezDiv(ArrayList<Curve> dest, Curve c, int index){
		double k=(double)index/numDiv;
		Curve r=new Curve();
		Curve s=new Curve();
		r.p1= c.p1.dup();
		r.p2= c.p1.plus(c.p2.minus(c.p1).times(k));
		r.p3= c.p3.minus(c.p2).times(k).plus(c.p2).minus(r.p2).times(k).plus(r.p2);
		r.p4= bezExplicit(c, k);
		s.p1= r.p4.dup();
		Dot p23= c.p3.minus(c.p2).times(k).plus(c.p2);
		s.p3= c.p4.minus(c.p3).times(k).plus(c.p3);
		s.p2= s.p3.minus(p23).times(k).plus(p23);
		s.p4= c.p4.dup();
		dest.add(r);
		dest.add(s);
	}
	public void printPoint(Dot p){
		System.out.println("x= " +p.x+" y= "+p.y);
	}
	public Dot bezExplicit(Curve bezier, double k){
		return new Dot(Math.pow(1-k,3)*bezier.p1.x+ 3*k*Math.pow(1-k, 2)*bezier.p2.x+3*Math.pow(k,2)*(1-k)*bezier.p3.x+Math.pow(k, 3)*bezier.p4.x, 
								  Math.pow(1-k,3)*bezier.p1.y+ 3*k*Math.pow(1-k, 2)*bezier.p2.y+3*Math.pow(k,2)*(1-k)*bezier.p3.y+Math.pow(k, 3)*bezier.p4.y);
	
	}

	public int curveChange(ArrayList<Dot> curvePoints){
		
		int size= curvePoints.size();
		if(size<4){
			return -1;
		}
		double slope1= slope(curvePoints.get(0), curvePoints.get(1));
		double slope2= slope(curvePoints.get(1), curvePoints.get(2));
		double slope3= slope(curvePoints.get(2), curvePoints.get(3));
		for(int i=3; i<size-1; i++){
			System.out.println("slope: "+slope1);
			if((slope2-slope1>0&&slope3-slope2<0)||(slope2-slope1<0&&slope3-slope2>0)){
				//curvature changes between i-1 and i-2
				System.out.println("curvature: ("+ curvePoints.get(i-1).x+" , "+curvePoints.get(i-1).y+")");
				curveChange=curvePoints.get(i-1); 
				return i-1;
			}
			slope1=slope2;
			slope2=slope3;
			slope3=slope(curvePoints.get(i), curvePoints.get(i+1));
		}
		System.out.println("no curvature");
		return -1;
	}
	public int curveChange(Dot[] curvePoints){
		
		int size= curvePoints.length;
		if(size<4){
			return -1;
		}
		Dot vector0= curvePoints[1].minus(curvePoints[0]);
		Dot vector1= curvePoints[2].minus(curvePoints[1]);
		boolean curvature= vector0.crossSign(vector1);
		for(int i=2; i<size-2; i++){
			Dot vectorA= curvePoints[i].minus(curvePoints[i-1]);
			Dot vectorB= curvePoints[i+1].minus(curvePoints[i]);
			boolean currentCurvature= vectorA.crossSign(vectorB);
			if(curvature!=currentCurvature){
				System.out.println("curvature: ("+ curvePoints[i].x+" , "+curvePoints[i].y+")");
				curveChange=curvePoints[i]; 
				return i;
			}
			vectorA= vectorB.dup();
			vectorB= curvePoints[i+2].minus(curvePoints[i+1]);
			
		}
		System.out.println("no curvature");
		return -1;
	}
	public void reduce(Curve c,ArrayList<Curve> arr){
		double angle=getAngle(c.dotData(numDiv));
		System.out.println("angle:"+ angle*180/Math.PI);
		if(angle<90){
			arr.add(c.clone());
			return;
		}else if(angle <180){
			bezDiv(arr, c, numDiv/2);
		}
		
	}
	public double getAngle(Dot[] arr){
		Dot vector0= arr[1].minus(arr[0]);
		Dot vector1= arr[2].minus(arr[1]);
		Dot last= arr[arr.length-1].minus(arr[arr.length-2]);
		boolean curvature= vector0.crossSign(vector1);
		System.out.println("*" + curvature);
		//rotation
		double angle=vector0.shortAngle(last);
		System.out.println("smallAngle:" + angle*180/Math.PI);
		if(curvature==true){
			return angle;
		}else{
			
			return 2*Math.PI- angle;
			
		}
	}

	public double getAngle(ArrayList<Dot> arr){
		Dot[] array=Arrays.copyOf(arr.toArray(), arr.size(), Dot[].class);
		return getAngle(array);

	}
	public double slope(Dot p1, Dot p2){
		return (p2.y-p1.y)/(p2.x-p1.x);
	}
	
	public void Draw(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < rawCurves.size(); i++){
			g2d.drawLine((int) rawCurves.get(i).p1.x, (int) rawCurves.get(i).p1.y, (int) rawCurves.get(i).p2.x, (int) rawCurves.get(i).p2.y);
			g2d.drawLine((int) rawCurves.get(i).p2.x, (int) rawCurves.get(i).p2.y, (int) rawCurves.get(i).p3.x, (int) rawCurves.get(i).p3.y);
			g2d.drawLine((int) rawCurves.get(i).p3.x, (int) rawCurves.get(i).p3.y, (int) rawCurves.get(i).p4.x, (int) rawCurves.get(i).p4.y);
			g2d.fillOval((int) rawCurves.get(i).p1.x, (int)rawCurves.get(i).p1.y, 4, 4);
			g2d.fillOval((int) rawCurves.get(i).p2.x, (int)rawCurves.get(i).p2.y, 4, 4);
			g2d.fillOval((int) rawCurves.get(i).p3.x, (int)rawCurves.get(i).p3.y, 4, 4);
			g2d.fillOval((int) rawCurves.get(i).p4.x, (int)rawCurves.get(i).p4.y, 4, 4);
		}
		g2d.setColor(Color.BLUE);
		
		for (int i = 0; i < noCurvature.size(); i++){
			g2d.drawLine((int) noCurvature.get(i).p1.x, (int) noCurvature.get(i).p1.y, (int) noCurvature.get(i).p2.x, (int) noCurvature.get(i).p2.y);
			g2d.drawLine((int) noCurvature.get(i).p2.x, (int) noCurvature.get(i).p2.y, (int) noCurvature.get(i).p3.x, (int) noCurvature.get(i).p3.y);
			g2d.drawLine((int) noCurvature.get(i).p3.x, (int) noCurvature.get(i).p3.y, (int) noCurvature.get(i).p4.x, (int) noCurvature.get(i).p4.y);
			g2d.fillOval((int) noCurvature.get(i).p1.x, (int)noCurvature.get(i).p1.y, 4, 4);
			g2d.fillOval((int) noCurvature.get(i).p2.x, (int)noCurvature.get(i).p2.y, 4, 4);
			g2d.fillOval((int) noCurvature.get(i).p3.x, (int)noCurvature.get(i).p3.y, 4, 4);
			g2d.fillOval((int) noCurvature.get(i).p4.x, (int)noCurvature.get(i).p4.y, 4, 4);
		}
//		System.out.println(rawCurves.get(0).p1.x +" "+ rawCurves.get(0).p1.y +" "+ rawCurves.get(0).p2.x+" "+ rawCurves.get(0).p2.y);
//		System.out.println(rawCurves.get(0).p3.x +" "+ rawCurves.get(0).p3.y +" "+ rawCurves.get(0).p4.x+" "+ rawCurves.get(0).p4.y);
		//// why is the red line drawn differently????
		// GeneralPath path = new GeneralPath(); // Bezier curve
		// g2d.setColor(Color.RED);
		// path.moveTo(curvePoints[0].x, curvePoints[0].y);
		// for (int i=1; i<curvePoints.length; i++)
		// path.lineTo(curvePoints[i].x, curvePoints[i].y);
		// g2d.draw(path);
		g2d.setColor(Color.RED);
		//drawPoints(g2d, rawPoints);
		g2d.setColor(Color.GREEN);
		drawPoints(g2d, noCurvaturePoints);
		
		if(curveChange!=null){
			g2d.setColor(Color.BLUE);
			g2d.fillRect((int)curveChange.x, (int)curveChange.y, 6, 6);
		}
	}
	public void drawPoints(Graphics2D g2d, Dot[] points){
		for (int i=0; i< points.length; i++){

			g2d.fillOval((int)points[i].x, (int)points[i].y, 4, 4);
			g2d.drawLine((int) points[i].x, (int) points[i].y, (int) points[i+1].x,
					(int) points[i+1].y);
		}
	}
	public void drawPoints(Graphics2D g2d, ArrayList<Dot> points){
		for (int i=0; i< points.size()-1; i++){
			/*if(i/(numDiv+1)%2==0){
				g2d.setColor(Color.GREEN);
			}else{
				g2d.setColor(Color.ORANGE);
			}*/
			g2d.fillOval((int)points.get(i).x, (int)points.get(i).y, 4, 4);
			g2d.drawLine((int) points.get(i).x, (int) points.get(i).y, (int) points.get(i+1).x,
					(int) points.get(i+1).y);
		}
	}
	public void paint(Graphics g) {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		Draw(g2d);
		g.drawImage(image, 0, 0, this);
		
	}
}