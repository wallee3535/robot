package Robot;


import java.awt.geom.Point2D;

public class CubBez {
	Point2D.Double p1;
	Point2D.Double p2;
	Point2D.Double p3; 
	Point2D.Double p4;
	
	public CubBez(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3, Point2D.Double p4){
		this.p1=p1;
		this.p2=p2;
		this.p3=p3;
		this.p4=p4;
	}
	
	public CubBez(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
		p1= new Point2D.Double(x1, y1);
		p2= new Point2D.Double(x2, y2);
		p3= new Point2D.Double(x3, y3);
		p4= new Point2D.Double(x4, y4);
	}
	
	public CubBez(){
		p1= new Point2D.Double(0, 0);
		p2= new Point2D.Double(0, 0);
		p3= new Point2D.Double(0, 0);
		p4= new Point2D.Double(0, 0);
	}
	
	public CubBez clone(){
		Point2D.Double p1 = new Point2D.Double(this.p1.x, this.p1.y);
		Point2D.Double p2 = new Point2D.Double(this.p2.x, this.p2.y);
		Point2D.Double p3 = new Point2D.Double(this.p3.x, this.p3.y);
		Point2D.Double p4 = new Point2D.Double(this.p4.x, this.p4.y);
		
		return new CubBez(p1, p2, p3, p4);
	}
}
