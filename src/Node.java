
/* Node.java это объект, который используется как блок 
 * или клетка в Pathfinding algorithm. Объект хранит
 * в себе такую информацию, как g, h, f, родителя клетки 
 * и координаты x, y. Может быть как пустой клеткой, так и
 * открытой, закрытой или стенкой.
 */

public class Node {
	private int x, y;
	private int g, h, f;
	Node parent;
	
	public Node(int x, int y, int g, int h, Node parent) {
		this.x = x;
		this.y = y;
		
		this.g = g;
		this.h = h;
		f = g + h;
	
		this.parent = parent;
	}
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public int getG() {
		return g;
	}

	public int getH() {
		return h;
	}
	
	public int getF() {
		f = g + h;
		return f;
	}
	
	public void setG(int g) {
		this.g = g;
	}
	
	public void setH(int h) {
		this.h = h;
	}
	
	
	public void setF(int f) {
		this.f = f;
	}
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
		
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public boolean isEqual(Node node) {
		if (node.getX() == x && node.getY() == y)
			return true;
		return false;
	}
}
