package Robot;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.lang.Math;

public class BezierApproximation2 extends Applet {
	Point2D.Double[] controlPoints;
	Point2D.Double curveChange;
	public int numDiv = 50;//number of points to plot per bezier curve
	ArrayList<CubBez> bezCurves;
	ArrayList<Point2D.Double> curvePoints; 
	
	public void init() {
		controlPoints = new Point2D.Double[4];
		curvePoints= new ArrayList<Point2D.Double>();
		
		
		bezCurves = new ArrayList<CubBez>();
		//ArrayList<Point> arcPoints = new ArrayList<Point>();
		bezCurves.add(new CubBez(20, 260, 50, 10, 250, 350, 300, 200));
		//bezCurves.add(new CubBez(300, 200, 350, 250, 375, 35, 400, 200));
		controlPoints[0] = new Point2D.Double(20, 260);
		controlPoints[1] = new Point2D.Double(50, 10);
		controlPoints[2] = new Point2D.Double(250, 35);
		controlPoints[3] = new Point2D.Double(300, 200);
		//controlPoints[3]=new Point(450,290);
		Compute();
	}

	public void SubDivide(Point2D.Double p1, Point2D.Double p2, double t) {
		if (p1.x > p2.x)
			p1.x -= Math.abs(p1.x - p2.x) * t;
		else
			p1.x += Math.abs(p1.x - p2.x) * t;
		if (p1.y > p2.y)
			p1.y -= Math.abs(p1.y - p2.y) * t;
		else
			p1.y += Math.abs(p1.y - p2.y) * t;
	}

	public void Compute() { // COMPUTE CURVEPOINTS FOR BEZIER CURVE
		Point2D.Double tmp[];
		bezDiv(bezCurves, 0, 11);
		//go through each bezier curve
		//for(int b=0; b<bezCurves.size();b++){
		//System.out.println(bezCurves.size());
		for(int b=0; b<3;b++){
			//make a hard copy
			CubBez current= bezCurves.get(b).clone();
			for (int i = 0; i <= numDiv; i++) { 
				// t is percentage of curve done
				double t = ((double) i) / (numDiv);
				// copy points from cubBez
				tmp = new Point2D.Double[]{current.p1, current.p2, current.p3, current.p4};
				// dimension of bezier curve???
				int Depth = 4;
				// recurse
				
				while (Depth > 1) {
					for (int j = 0; j < Depth - 1; j++)
						// reduce dimension, to get new lines
						SubDivide(tmp[j], tmp[j + 1], t);
					Depth--;
				}
				// last remaining point is on the path
				curvePoints.add(new Point2D.Double(tmp[0].x, tmp[0].y));
			}
			int c;
			if((c=curveChange(curvePoints))!=-1){
				System.out.println("curvature changes "+c);
				//bezDiv(bezCurves, b, c);
				
			}
		}
		
		// COMPUTE ARCPOINTS FOR ARCS
		
		
	}
	public void bezDiv(ArrayList<CubBez> bezCurves, int indexCubBez, int indexCurvePoint){
		double k=(double)indexCurvePoint/numDiv;
		CubBez r=new CubBez();
		CubBez s=new CubBez();
		CubBez temp= bezCurves.get(indexCubBez);
		r.p1= (Double) temp.p1.clone();
		r.p2= addPoint(temp.p1, scalePoint(subPoint(temp.p2, temp.p1), k));
		r.p3= addPoint(r.p2, scalePoint(subPoint(addPoint(temp.p2, scalePoint(  subPoint(temp.p3, temp.p2)  ,k)  ), r.p2)  , k));
		r.p4= bezExplicit(temp, k);
		s.p1= (Double) r.p4.clone();
		Point2D.Double p23=addPoint(temp.p2, scalePoint(subPoint(temp.p3, temp.p2), k));
		s.p3= addPoint(temp.p3,scalePoint(subPoint(temp.p4, temp.p3),k));
		s.p2= addPoint(p23, scalePoint(subPoint(s.p3,p23),k));
		s.p4= (Double) temp.p4.clone();
		//bezCurves.remove(indexCubBez);
		bezCurves.add(indexCubBez, s);
		bezCurves.add(indexCubBez, r);
	}
	public void printPoint(Point2D.Double p){
		System.out.println("x= " +p.getX()+" y= "+p.getY());
	}
	public Point2D.Double bezExplicit(CubBez bezier, double k){
		return new Point2D.Double(Math.pow(1-k,3)*bezier.p1.x+ 3*k*Math.pow(1-k, 2)*bezier.p2.x+3*Math.pow(k,2)*(1-k)*bezier.p3.x+Math.pow(k, 3)*bezier.p4.x, 
								  Math.pow(1-k,3)*bezier.p1.y+ 3*k*Math.pow(1-k, 2)*bezier.p2.y+3*Math.pow(k,2)*(1-k)*bezier.p3.y+Math.pow(k, 3)*bezier.p4.y);
	
	}
	public Point2D.Double addPoint(Point2D.Double a, Point2D.Double b){
		return new Point2D.Double(a.getX()+b.getX(), a.getY()+b.getY());
	}
	public Point2D.Double subPoint(Point2D.Double a, Point2D.Double b){
		return new Point2D.Double(a.getX()-b.getX(), a.getY()-b.getY());
	}
	public Point2D.Double scalePoint(Point2D.Double a, double k){
		return new Point2D.Double(a.getX()*k, a.getY()*k);
	}
	public int curveChange(ArrayList<Point2D.Double> curvePoints){
		
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
				System.out.println("curvature: ("+ curvePoints.get(i-2).x+" , "+curvePoints.get(i-2).y+")");
				curveChange=curvePoints.get(i-2); 
				return i-2;
			}
			slope1=slope2;
			slope2=slope3;
			slope3=slope(curvePoints.get(i), curvePoints.get(i+1));
		}
		System.out.println("no curvature");
		return -1;
	}
	public double slope(Point2D.Double p1, Point2D.Double p2){
		return (p2.y-p1.y)/(p2.x-p1.x);
	}
	
	public void Draw(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < bezCurves.size(); i++){
			g2d.drawLine((int) bezCurves.get(i).p1.x, (int) bezCurves.get(i).p1.y, (int) bezCurves.get(i).p2.x, (int) bezCurves.get(i).p2.y);
			g2d.drawLine((int) bezCurves.get(i).p2.x, (int) bezCurves.get(i).p2.y, (int) bezCurves.get(i).p3.x, (int) bezCurves.get(i).p3.y);
			g2d.drawLine((int) bezCurves.get(i).p3.x, (int) bezCurves.get(i).p3.y, (int) bezCurves.get(i).p4.x, (int) bezCurves.get(i).p4.y);
			g2d.fillOval((int) bezCurves.get(i).p1.x, (int)bezCurves.get(i).p1.y, 4, 4);
			g2d.fillOval((int) bezCurves.get(i).p2.x, (int)bezCurves.get(i).p2.y, 4, 4);
			g2d.fillOval((int) bezCurves.get(i).p3.x, (int)bezCurves.get(i).p3.y, 4, 4);
			g2d.fillOval((int) bezCurves.get(i).p4.x, (int)bezCurves.get(i).p4.y, 4, 4);
		}
//		System.out.println(bezCurves.get(0).p1.x +" "+ bezCurves.get(0).p1.y +" "+ bezCurves.get(0).p2.x+" "+ bezCurves.get(0).p2.y);
//		System.out.println(bezCurves.get(0).p3.x +" "+ bezCurves.get(0).p3.y +" "+ bezCurves.get(0).p4.x+" "+ bezCurves.get(0).p4.y);
		//// why is the red line drawn differently????
		// GeneralPath path = new GeneralPath(); // Bezier curve
		// g2d.setColor(Color.RED);
		// path.moveTo(curvePoints[0].x, curvePoints[0].y);
		// for (int i=1; i<curvePoints.length; i++)
		// path.lineTo(curvePoints[i].x, curvePoints[i].y);
		// g2d.draw(path);
		g2d.setColor(Color.RED);
		for (int i = 0; i < curvePoints.size() - 1; i++){
			g2d.fillOval((int)curvePoints.get(i).x, (int)curvePoints.get(i).y, 4, 4);
			g2d.drawLine((int) curvePoints.get(i).x, (int) curvePoints.get(i).y, (int) curvePoints.get(i+1).x,
					(int) curvePoints.get(i+1).y);
		}
		if(curveChange!=null){
			g2d.setColor(Color.YELLOW);
			g2d.fillOval((int)curveChange.x, (int)curveChange.y, 4, 4);
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