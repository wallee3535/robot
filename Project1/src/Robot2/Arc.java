package Robot2;

import java.awt.Graphics2D;

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
		calcCenter(p1, p2, p3);
		radius= p1.minus(center).mag();
	}
	
	public void calcCenter(Dot p1, Dot p2, Dot p3){
		center= Dot.circumcenter(p1, p2, p3);
		System.out.println(p1.minus(p2).crossMag((p2.minus(p3))));
		//center.print();
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
}
