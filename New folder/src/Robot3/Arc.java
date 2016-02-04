package Robot3;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class Arc {
	Dot p1;
	Dot p2;
	Dot p3;
	Dot center;
	double radius;
	
	public Arc(Dot p1, Dot p2, Dot p3){
		this.p1=p1;
		this.p2=p2;
		this.p3=p3;
		center= Dot.circumcenter2(p1, p2, p3);
		radius= p1.minus(center).mag();
	}
	

	public void draw(Graphics2D g2d){
		g2d.drawOval((int)(center.x-radius), (int)(center.y-radius), (int)(2*radius), (int)(2*radius));
	}
	
	public void print(){
		System.out.println("Arc:");
		p1.print();
		p2.print();
		p3.print();
		System.out.print("center: ");
		center.print();
		System.out.println("radius"+radius);
	}
	ArrayList<Dot> circlePoints(){
		ArrayList<Dot> result = new ArrayList<Dot>();
		for(int i= 0; i< Globals.cDiv; i++){
			double theta = (i/Globals.cDiv)*2*Math.PI;
			//
		}
		return result;
	}
}
