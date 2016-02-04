package Robot3;

public class Dot {
	double x;
	double y;
	double z;

	public Dot(double x, double y) {
		this(x, y, 0);
	}

	public Dot(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Dot dup() {
		return new Dot(x, y, z);
	}

	public static Dot circumcenter(Dot a, Dot b, Dot c) {
		
		double d = 2 * (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y));
		double ux = ((a.x * a.x + a.y * a.y) * (b.y - c.y) + (b.x * b.x + b.y * b.y) * (c.y - a.y)
				+ (c.x * c.x + c.y * c.y) * (a.y - b.y)) / d;
		double uy = ((a.x * a.x + a.y * a.y) * (c.x - b.x) + (b.x * b.x + b.y * b.y) * (a.x - c.x)
				+ (c.x * c.x + c.y * c.y) * (b.x - a.x)) / d;
		return new Dot(ux, uy);
		
		
	}
	
	public static Dot circumcenter2(Dot p1, Dot p2, Dot p3) {
		double alpha= (Math.pow((p2.minus(p3).mag()), 2)*p1.minus(p2).dotProd(p1.minus(p3)))/(
				2*Math.pow((p1.minus(p2).crossProd(p2.minus(p3))).mag(), 2)
				);
		double beta= (Math.pow((p1.minus(p3).mag()), 2)*p2.minus(p1).dotProd(p2.minus(p3)))/(
				2*Math.pow((p1.minus(p2).crossProd(p2.minus(p3))).mag(), 2)
				);
		double gamma= (Math.pow((p1.minus(p2).mag()), 2)*p3.minus(p1).dotProd(p3.minus(p2)))/(
				2*Math.pow((p1.minus(p2).crossProd(p2.minus(p3))).mag(), 2)
				);
		Dot center = p1.times(alpha).plus(p2.times(beta)).plus(p3.times(gamma));
		return center;
	}

	public Dot plus(Dot b) {
		return new Dot(x + b.x, y + b.y, z + b.z);
	}

	public Dot minus(Dot b) {
		return new Dot(x - b.x, y - b.y, z - b.z);
	}

	public Dot times(double m) {
		return new Dot(x * m, y * m, z * m);
	}

	public void print() {
		System.out.println("x= " + x + ", y= " + y + ",z= " + z);
	}

	public boolean crossSign(Dot b) {
		return (x * b.y - b.x * y > 0);
	}

	public double crossMag(Dot b) {
		return x * b.y - b.x * y;
	}

	public double dotProd(Dot b) {
		return x * b.x + y * b.y + z * b.z;
	}

	public Dot crossProd(Dot b) {
		double x3 = y * b.z - z * b.y;
		double y3 = z * b.x - x * b.z;
		double z3 = x * b.y - y * b.x;
		return new Dot(x3, y3, z3);
	}

	public double mag() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public double shortAngle(Dot b) {
		return Math.acos(this.dotProd(b) / this.mag() / b.mag());
	}

	/**
	 * returns the counterclockwise/clockwise rotation angle from this to dot b
	 */
	public double rotationTo(Dot b, boolean isCounterClockwise) {
		boolean crossSign = this.crossSign(b);
		if (crossSign == isCounterClockwise) {
			return this.shortAngle(b);
		} else {
			return 2 * Math.PI - this.shortAngle(b);
		}
		// counterclockwise:
		/*
		 * if(this.crossSign(b)==true){ return this.shortAngle(b); }else{ return
		 * 2*Math.PI-this.shortAngle(b); }
		 */
	}

	public double distTo(Dot b) {
		return this.minus(b).mag();
	}

	public static void main(String[] args) {
		Dot a = new Dot(0, 3, -3);
		Dot b = new Dot(0, 4,9);
		Dot c = new Dot(0, 1, 98);
		Dot d = Dot.circumcenter(a, b, c);
		Dot e = Dot.circumcenter2(a, b, c);
		d.print();
		e.print();
	}

}
