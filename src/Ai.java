import java.util.*;
import java.lang.*;

class Point {
	int id;
	int x;
	int y;

	Point(int id, int x, int y) {
		this.id = id;
		this.x  = x;
		this.y  = y;
	}

	Point(int x, int y) {
		this.x  = x;
		this.y  = y;
	}
	int getId() { return id;}
	int getX()  { return x;}
	int getY()  { return y;}

	Point subPoint(Point p) {
		int w = p.getX();
		int t = p.getY();
		Point q = new Point(x-w, y-t);
		return q;
	}

	int mulPoint(Point p) {
		int w = p.getX();
		int t = p.getY();
		return x*t-y*w;
	}
}

class Map {
	int numberPoints;
	Point[] points;
	
	Map(int n, int maxX, int minX, int maxY, int minY, int flag) {
		numberPoints = n;
		points = new Point[numberPoints];
		if (flag==0) { //not random, mapa é irrelevante
			Scanner sc = new Scanner(System.in);
			System.out.println("coordenadas (x,y)");
			for (int i=0; i<numberPoints; i++) {
				int x = sc.nextInt();
				int y = sc.nextInt();
				if (searchPoint(x,y,i)==-1)
					points[i] = new Point(i,x,y);
				else {
					i--;
					System.out.println("Ponto já existe");
				}
			}
		}
		else { //random
			Random rand = new Random();
			for (int i=0; i<numberPoints; i++) {
				int x = rand.nextInt(maxX - minX) + minX;
				int y = rand.nextInt(maxY - minY) + minY;
				if (searchPoint(x,y,i)==-1)
					points[i] = new Point(i,x,y);
				else i--;
			}
		}
	}

	void coordMap(int numberPoints) { //para debug
		for (int i=0; i<numberPoints;i++) {
			System.out.println("Ponto nº" + points[i].id + " (" + points[i].x + " ," + points[i].y + ")");
		}
	}

	int searchPoint(int x1, int y1, int size) {
		int x2, y2;
		for (int i=0;i<size;i++) {
			x2 = points[i].getX();
			y2 = points[i].getY();
			if (x1==x2 && y1==y2) return i;
		}
		return -1;
	}
}

class Intersection {
	Point p1;
	Point p2;
	Point p3;
	Point p4;

	Intersection(Point p1, Point p2, Point p3, Point p4) {
		this.p1=p1;
		this.p2=p2;
		this.p3=p3;
		this.p4=p4;
	}

	Point getp1() { return p1;}
	Point getp2() { return p2;}
	Point getp3() { return p3;}
	Point getp4() { return p4;}
}

class Candidate {
	LinkedList<Point> l;
	LinkedList<Intersection> in;

	Candidate(LinkedList<Point> l,LinkedList<Intersection> in) {
		this.l=l;
		this.in=in;
	}
	LinkedList<Point> getL() { return l;}
	LinkedList<Intersection> getIn() { return in;}
	int getNrIntesections() { return in.size();}
}

public class Ai {

	Scanner sc;
	int nPoints, m, ra, kmax;
	//leitura personalizada do
	//nº de pontos (N) e o tamanho do mapa(M)
	Ai(){
		readInput();
	}

	void readInput(){
		sc = new Scanner(System.in);
		System.out.println("random (1-sim 0-não)?");
		ra = sc.nextInt();
		System.out.println("numero de pontos (n)?");
		nPoints = sc.nextInt();
		if (ra!=0) {
			System.out.println("tamanho do mapa (m)?");
			m = sc.nextInt();
		}
		System.out.println("valor de 'k' no simulated annealing (-1=default)?");
		kmax = sc.nextInt();
	}

	int getN() { return nPoints;}
	int getM()  { return m;}

	LinkedList<Point> permutation(Map map) { // a permutaçao random ja é random natural 2a)
		LinkedList<Point> l = new LinkedList<Point>();

		for (int i=0; i<map.numberPoints; i++) {
			l.add(map.points[i]);
		}
		return l;
	}

	LinkedList<Point> neighbour(LinkedList<Point> l) {	 //deletes l
		Point p1 = l.removeFirst();
		Point p2;
		int size = l.size();
		double max;
		int jMax = -1;
		double eucl;
		LinkedList<Point> newList = new LinkedList<Point>();
		newList.add(p1);

		for (int i=0; size!=0; i++) {
			max = Integer.MAX_VALUE;
			for (int j=0; j<size; j++) {
				p2 = l.get(j);
				eucl = Math.sqrt(Math.pow((p2.getX()-p1.getX()),2)+Math.pow((p2.getY()-p1.getY()),2));
				if (eucl < max) {
					max = eucl;
					jMax = j;
				}
			}
			p1=l.remove(jMax);
			newList.addLast(p1);
			size--;
		}

		return newList;
	}
	
	boolean segmentsIntersect(Point p1, Point p2, Point p3, Point p4) { 
		//pseudo codigo pag 16 --- http://www.inf.ed.ac.uk/teaching/courses/ads/Lects/lecture1516.pdf
		int d1=(p3.subPoint(p1)).mulPoint(p2.subPoint(p1));
		int d2=(p4.subPoint(p1)).mulPoint(p2.subPoint(p1));
		int d3=(p1.subPoint(p3)).mulPoint(p4.subPoint(p3));
		int d4=(p2.subPoint(p3)).mulPoint(p4.subPoint(p3));
		//System.out.println(d1 +" "+ d2+ " "+d3 + " " + d4);
		if (d1*d2<0 && d3*d4<0) return true;
		else if (d1==0 && inBox(p1,p2,p3)) return true;
		else if (d2==0 && inBox(p1,p2,p4)) return true;
		else if (d3==0 && inBox(p3,p4,p1)) return true;
		else if (d4==0 && inBox(p3,p4,p2)) return true;
		else return false;
	}

	boolean inBox(Point p1, Point p2, Point p3) {
		int x1 = p1.getX();
		int x2 = p2.getX();
		int x3 = p3.getX();
		int y1 = p1.getY();
		int y2 = p2.getY();
		int y3 = p3.getY();
		return ((Math.min(x1, x2) <= x3) && (x3 <= Math.max(x1,x2))) && ((Math.min(y1, y2) <= y3) && (y3 <= Math.max(y1,y2)));
	}

	LinkedList<Intersection> brute(LinkedList<Point> l) {
		int size = l.size();
		LinkedList<Intersection> it = new LinkedList<Intersection>();
		if (size <=3) {
			return it;
		}
		Point p1,p2,p3,p4;
		for (int i=0;i<size-3;i++) {
			p1 = l.get(i);
			p2 = l.get(i+1);
			for (int j=i+2;j<=size-2;j++) {
				p3 = l.get(j);
				p4 = l.get(j+1);
				if (segmentsIntersect(p1, p2, p3, p4)) {
					Intersection f = new Intersection(p1,p2,p3,p4);
					it.add(f);
				}
			}
		}

		//verifica ultimo segmento
		p1 = l.getLast();
		p2 = l.getFirst();
		for (int i=1;i<=size-3;i++) {
			p3 = l.get(i);
			p4 = l.get(i+1);
			if (segmentsIntersect(p1, p2, p3, p4)) {
				Intersection f = new Intersection(p1,p2,p3,p4);
				it.add(f);
			}
		}
		return it;
	}
	
	void exchange(LinkedList<Point> l, int a, int b, int intersectionNumber) {
		if (a==b) return;
		Point p1=l.get(a);
		Point p2=l.get(b);
		l.remove(a);
		l.add(a,p2);
		l.remove(b);
		l.add(b,p1);
		if (a==b+1 || b==a+1) return;
		if (a<b) exchange(l,a+1, b-1, intersectionNumber);
		if (a>b) exchange(l,a-1, b+1, intersectionNumber);
	}

	int searchPoint(Point p1, LinkedList<Point> l) {
		int x2, y2; 
		int x1 = p1.getX();
		int y1 = p1.getY();
		int size = l.size();
		for (int i =0; i<size; i++) {
			Point p2 = l.get(i);
			x2 = p2.getX();
			y2 = p2.getY();
			if (x1==x2 && y1==y2) return i;
		}
		return -1; //nunca deve returnar -1 porque o ponto é garantido estar na lista
	}

	Candidate listExchanged(Intersection in, LinkedList<Point> l1, int intersectionNumber) { 
		//{i, j} e {k, l} -> {i, k} e {j, l}
		int j = searchPoint(in.getp2(), l1);
		int k = searchPoint(in.getp3(), l1);
		LinkedList<Point> l2 = new LinkedList<Point>(l1);
		exchange(l2, j, k,intersectionNumber);
		Candidate c = new Candidate(l2, brute(l2));

		return c;
	}

	int hillClimbing(LinkedList<Candidate> candidateList,int alternative){
		if(alternative == 0){
			Candidate current = candidateList.get(0);
			Candidate neighbor;
			double minPerim = getValue(current);
        	for (int i = 1; i < candidateList.size(); i++) {
            	if (minPerim > getValue(candidateList.get(i)))
                	current = candidateList.get(i);
        	}
		return candidateList.indexOf(current);
		}
		
		else if(alternative == 1){
			return 0;
		}
		
		else if(alternative == 2){
			Candidate current = candidateList.get(0);
			Candidate neighbor;
			int minNumbIntersections = current.getIn().size();
			for (int i = 1; i < candidateList.size(); i++) {
				if(minNumbIntersections > candidateList.get(i).getIn().size())
					current = candidateList.get(i);
			}
			return candidateList.indexOf(current);
		}
		
		else if(alternative == 3){
			Random random2 = new Random();
			int cand = random2.nextInt(candidateList.size());
			//if (getN()<20) System.out.println("candidateList.size() " + candidateList.size());
			//if (getN()<20) System.out.println("random = " + cand);
			return cand;
		}
		return -1;
	}

    double getValue(Candidate c){
    	return perimeter(c.getL());
    }
	
	double perimeter(LinkedList<Point> m) {
		int x, y, w, t;
		double sum = 0;
		double eucl;
		Point p2 = m.getLast();

		for(Point p1 : m) {
			x=p1.getX();
			y=p1.getY();
			w=p2.getX();
			t=p2.getY();
			eucl = Math.sqrt(Math.pow((p2.getX()-p1.getX()),2)+Math.pow((p2.getY()-p1.getY()),2));
			sum = sum + eucl;
			p2 = p1;
		}

		return sum;
	}

	void printList(LinkedList<Point> l) {
		for (Point point : l) {
			System.out.println("x=" + point.getX() + " y=" + point.getY());
		}
		System.out.println();
	}

	void printIntersection(LinkedList<Intersection> l) {
		Point p1, p2, p3, p4;
		for (Intersection i : l) {
			p1 = i.p1;
			p2 = i.p2;
			p3 = i.p3;
			p4 = i.p4;
			System.out.println("(" + p1.getX() + "," + p1.getY()+ 
			");(" +p2.getX()+","+p2.getY()+") interseta com ("+ p3.getX() + "," + p3.getY()+ 
			");(" +p4.getX()+","+p4.getY()+ ")");
		}
		if(l.size() == 0) System.out.println("Não existe interseções");
	}

	void printPoints(LinkedList<Point> l, Map m) {
		int x;
		int y;
		int n = getN();
		System.out.print("[");
		for(Point p: l) {
			x=p.getX();
			y=p.getY();
			System.out.print(m.searchPoint(x,y,n)+",");
		}
		Point p=l.getFirst();
		x=p.getX();
		y=p.getY();
		System.out.println(m.searchPoint(x,y,n)+"]");
	}

	LinkedList<Candidate> newCandidates(LinkedList<Intersection> in, LinkedList<Point> l) {
		LinkedList<Candidate> c = new LinkedList<Candidate>();
		int intersectionNumber=1;
		for (Intersection intersection : in) {
			c.add(listExchanged(intersection, l, intersectionNumber)); //ia.listExchanged(...) 
			intersectionNumber++;
		}
		return c;
	}

	void hc(LinkedList<Candidate> c, int flag, LinkedList<Point> l,Map m) {
		int candidateSolution, tmp1, tmp2;
		Candidate dummy;
		LinkedList<Intersection> in2;
		do {
			candidateSolution = hillClimbing(c,flag);
			tmp1 = c.size();
			dummy = c.get(candidateSolution);
			tmp2 = dummy.getNrIntesections();
			if (tmp2<tmp1) { 
				//System.out.println("there are "+ tmp2 + " intersections and " +tmp1 +" candidatos");
				l = new LinkedList<Point>(dummy.getL());							
				in2 = new LinkedList<Intersection>(dummy.getIn());
				c = newCandidates(in2, l);
			}
		} while (tmp2 !=0 && tmp2<tmp1);
		int p = flag+'A';
		char u = (char)p;
		if (tmp2==0) System.out.println("\nSolução "+ u +" foi ótima");
		else System.out.println("\nSolução "+ u +" NÃO foi ótima");
		//System.out.println("Perimetro 4"+u+" = " + ia.perimeter(l));
		if (getN()<51) printPoints(l,m);

	}

	//https://en.wikipedia.org/wiki/Simulated_annealing
	LinkedList<Point> sa(LinkedList<Point> l) {
		LinkedList<Point> l1 = new LinkedList<Point>(l);
		LinkedList<Point> l2 = new LinkedList<Point>(l);
		LinkedList<Intersection> in;
		LinkedList<Candidate> c;
		double t;
		if (kmax == -1) kmax = l.size()*5; //random kmax, kmax global
		Random rand = new Random();
		int s=-1;
		Candidate candidate;

		for (int k=0;k<kmax;k++){
			in = brute(l2);
			c = newCandidates(in,l2);
			t = (k+1)/kmax;
			s = in.size();
			if (s==0) {
				System.out.println("\nSimualted Annealing foi Otimo com kmax = "+ kmax);
				break;
			}
			candidate = c.get(rand.nextInt(s));
			l2 = candidate.getL();
			if (P((brute(l1).size()),brute(l2).size(),t)>rand.nextDouble()) {
				l1 = new LinkedList<Point>(l2);
			}
		}
		if (s!=0) System.out.println("\nSimualted Annealing NÃO foi Otimo com kmax = "+ kmax);
		return l1;
	}

	//https://en.wikipedia.org/wiki/Simulated_annealing
	double P(int e1, int e2, double t) {
		if (e1>e2) return 1;
		return Math.exp(-(e2-e1)/t);
	}

	public static void main(String[] args) {
		
		Ai ia = new Ai();
		
		Map map = new Map(ia.getN(), ia.getM(), -(ia.getM()), ia.getM(), -(ia.getM()), ia.ra);
		//ex. 1
		System.out.println("\n" + "EX.1");
		map.coordMap(ia.getN());
		
		//ex. 2A
		LinkedList<Point> l = ia.permutation(map);
		System.out.println("\n" + "EX.2A");
		ia.printPoints(l, map);
		
		//ex. 2B
		LinkedList<Point> n = ia.neighbour(l);
		System.out.println("\n" + "EX.2B");
		ia.printPoints(n, map);
		//ia.printSolution(ia.solution2b);
		//System.out.println("Perimetro 2B " + ia.perimeter(n));


		//ex. 3
		System.out.println("\n" + "EX.3");
		l = n;
		Candidate original = new Candidate(l,ia.brute(n));
		LinkedList<Intersection> in = original.getIn();
		ia.printIntersection(in);

		LinkedList<Candidate> c = ia.newCandidates(in, n); //candidatos originais
		
		System.out.println("\nNumero de candidatos = " + in.size());
		for (int i=0;i<in.size();i++) {
			Candidate candidate = c.get(i);
			System.out.print("Candidato "+ i + " ");
			ia.printPoints(candidate.getL(),map);
		}
		
		//EX 4
		System.out.println("\nEX4");

		//if para ganrantir que ha candidatos
		//usa-se a lista greedy
		if(c.size() != 0){
			//ciclo para chamar os 4 possibilidades para o hill climb
			for (int i=0;i<4;i++) {
				ia.hc(c,i,l,map);
				c = ia.newCandidates(in, n); //candidatos originais
			}
		}

		//Simulated Annealing
		System.out.println("\n\n\n\nEX5");
		//GET THE ORIGINAL RANDOMS IN A LIST
		int q = ia.getN();
		l = new LinkedList<Point>();
		for (int i=0;i<q;i++) {
			l.add(map.points[i]);
		}
		if (ia.getN()<51) {
			System.out.println("\nLista original");
			ia.printPoints(l,map);
		}
		l = ia.sa(l);
		if (ia.getN()<51) {
			System.out.println("\nLista depois do simulated annealing");
			ia.printPoints(l,map);
			//System.out.println("Perimetro 5-SIMULATED ANNEALING = " + ia.perimeter(l));
		}
	}
}
