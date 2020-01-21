import java.time.LocalTime;
import java.util.ArrayList;

/* Это класс реализующий алгоритм нахождения 
 * кратчайшего пути между двумя точками (Pathfinding Algorithm).
 * Подробнее о работе алгоритма можно узнать здесь: 
 * https://www.youtube.com/watch?v=-L-WgKMFuhE
 */

public class Pathfinding {
	private Node startNode, endNode, par;
	private ArrayList<Node> borderList, openList, closedList, pathList;
	private boolean noPath, complete, diagonal, showSteps;
	private Frame f;
	LocalTime t;
	private int size, width, height;
	private int diagonalCost; 
	
	Pathfinding(Frame f, int size) {
		this.f = f;
		this.size = size;		
		
		complete = false;
		showSteps = true;
		diagonal = true;
		
		openList = new ArrayList<>();
		borderList = new ArrayList<>();
		closedList = new ArrayList<>();
		pathList = new ArrayList<>();
	}
	
	public void setup() {
		width = f.getWidth();
		height = f.getHeight();
		diagonalCost = (int) (size * Math.sqrt(2));
		startNode.setG(0);
		addClosed(startNode);		
	}
	
	public void findpath(Node node) {

		// Вычисляем соседние клекти
		if(diagonal) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (i == 1 && j == 1) continue;
					
					int possibleX = (node.getX() - size) + size*i;
					int possibleY = (node.getY() - size) + size*j;
					
					if ((searchBorder(node.getX(), possibleY) != -1 
							| searchBorder(possibleX, node.getY()) != -1)
									&& ((i !=1) && (j != 1))) {
						continue;
					}

					calculateNode(possibleX, possibleY, node);
				}
			}
		} else {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						if ((i == 1 && j == 1)
								| (i != 1 && j != 1)) continue;
						
						int possibleX = (node.getX() - size) + size*i;
						int possibleY = (node.getY() - size) + size*j;
						
						calculateNode(possibleX, possibleY, node);
					}
				}
		}
		
		if(openList.isEmpty()) {
			noPath = true;
			return;
		}
		
		node = lowestFCost();
		
		removeOpen(node);

		if (node.isEqual(endNode)) {
			endNode = node;
			complete = true;
			connectPath();
			reversePath();
			return;
		}
		
		addClosed(node);			
		
		if (!showSteps) {
			findpath(node);
		} else {
			par = node;
		}
	}
	
	private void connectPath() {
		Node parent = endNode.getParent();

		while(parent != startNode)
			for (int i = 0; i < closedList.size(); i++) {
				if (parent.isEqual(closedList.get(i))) {
					addPath(parent);
					parent = closedList.get(i).getParent();
				}
			}
	}
	
	private void addPath(Node node) {
		pathList.add(node);
	}

	private void reversePath() {
		int i = 0;
		int j = pathList.size()-1;
		
		while(i<=j) {
			Node temp = pathList.get(i);
			pathList.remove(i);
			pathList.add(i, pathList.get(j-1));
			pathList.remove(j);
			pathList.add(j, temp);
			i++;
			j--;
		}
	}

	private void removeOpen(Node node) {
		if(openList.size() > 0) {
			for (int i = 0; i < openList.size(); i++) {
				if (openList.get(i).getX() == node.getX() 
						&& openList.get(i).getY() == node.getY())
					openList.remove(i);
					return;
			}
		}
	}
	
	public ArrayList<Node> getPath() {
		return pathList;
	}
	
	//	если соседние клетки не являются 
	// закрытми или стенкой, добавляем их в лист 
	// открытых клеток (openList).
	private void calculateNode(int possibleX, int possibleY, Node parent) {
		// Выходят ли координаты за рамки окна приложения
		if (possibleX >= width | possibleX < 0 
				| possibleY >= height | possibleY < 0)
			return; 
		
		if (searchBorder(possibleX, possibleY) != -1
							| searchClosed(possibleX, possibleY) != -1) {
			return;
		}
		
		int newH = (int) (Math.abs(possibleX-endNode.getX()) + Math.abs(possibleY - endNode.getY()));
		int newG = parent.getG();
		if ((possibleX - parent.getX()) != 0 && (possibleY - parent.getY()) != 0)
			newG += diagonalCost;
		else
			newG += size;
		
		// Проверяем, не находится ли уже клетка в списке открытых клеток 
		int i = searchOpen(possibleX, possibleY);
		if (i != -1) {
			Node openCheck = openList.get(i);
			
			if(diagonal && openCheck != null && openCheck.getF() > (newG + newH)) {
				openCheck.setXY(possibleX, possibleY);
				openCheck.setG(newG);
				openCheck.setH(newH);
				openCheck.setF(newG + newH);
				openCheck.setParent(parent);
			}
			
			return;
		}

		addOpen(new Node(possibleX, possibleY, newG, newH, parent));
	}
	
	public boolean noPath() {
		return noPath;
	}
	
	public Node lowestFCost() {
		if (openList.size() > 0) {
			Sort.quickSort(openList, 0, openList.size() -1);
			return openList.get(0);
		}
		return null;
	}
	
	public void setStartNode(Node n) {
		startNode = n;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public Node getStartNode() {
		return startNode;
	}

	public Node getEndNode() {
		return endNode;
	}
	
	public void setEndNode(Node n) {
		endNode = n;
	}

	private int searchOpen(int possibleX, int possibleY) {
		for (int i = 0; i < openList.size(); i++) {
			if(openList.get(i).getX() == possibleX && openList.get(i).getY() == possibleY)
				return i;
		}
		return -1;
	}

	public int searchBorder(int x, int y) {
		if (borderList.size() > 0) {
			for (int i = 0; i < borderList.size(); i++) {
				if (borderList.get(i).getX() == x 
						&& borderList.get(i).getY() == y)
					return i;
			}
		}
		return -1;
	}
	
	public void setShowSteps(boolean b) {
		showSteps = b;
	}
	
	public int searchClosed(int x, int y) {
		if (closedList.size() > 0) {
			for (int i = 0; i < closedList.size(); i++) {
				if (closedList.get(i).getX() == x 
						&& closedList.get(i).getY() == y)
					return i;
			}
		}
		return -1;
	}
	
	public void setDiagonal(boolean b) {
		diagonal = b;
	}
	
	public void reset() {
		noPath = false;
		complete = false;
		openList.clear();
		closedList.clear();
		pathList.clear();
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public Node getPar() {
		return par;
	}

	public void addOpen(Node node) {
		if(searchOpen(node.getX(), node.getY()) != -1 ) {
			return;
		}
		openList.add(node);
	}

	public void addClosed(Node node) {
		if(searchClosed(node.getX(), node.getY()) != -1 ) {
			return;
		}
		closedList.add(node);
	}
	
	public void addBorder(Node node) {
		if (searchBorder(node.getX(), node.getY()) == -1)
			borderList.add(node);
	}
	
	public ArrayList<Node> getBorderList() {
		return borderList;
	}

	public ArrayList<Node> getOpenList() {
		return openList;
	}

	public ArrayList<Node> getClosedList() {
		return closedList;
	}
}
