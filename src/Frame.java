import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/* Frame.java отвечает за визуальную часть программы.
*/

public class Frame extends JPanel 
					implements MouseListener, MouseMotionListener, 
									KeyListener, MouseWheelListener, ActionListener {
	private Timer t;
	private char key;
	private Pathfinding p;
	private int size;
	private int r, g, b;
	private final int MAX = 255, MIN = 0; // Макс и мин значения для цветов
	private Menu m;
	private JFrame window;
	private double a1, a2;
	private boolean running, hover;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new Frame();
		});
	}
	
	public Frame() {
		// Значения переменных используются в экспоненциальной 
		// функции в методе setSpeed()
		a1 = (5000.0000 / (Math.pow(25.0000/5000, 1/49)));
		a2 = 625.0000;
		
		running = false;
		size = 25;
				
		r = (int) (Math.random() * ((MAX - MIN) + 1)) + MIN;
		g = (int) (Math.random() * ((MAX - MIN) + 1)) + MIN;
		b = (int) (Math.random() * ((MAX - MIN) + 1)) + MIN;

		p = new Pathfinding(this, size);
		
		m = new Menu(this);
		
		setBackground(Color.WHITE);
		setFocusable(true);
		setLayout(null);
		setFocusTraversalKeysEnabled(false);
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
				
		window = new JFrame("A* Pathfinding Algorithm Visualization");
		window.setContentPane(this);
		window.setMinimumSize(new Dimension(300, 300));
		window.getContentPane().setPreferredSize(new Dimension(700, 600));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		//добавляем меню
		m.addAll();
		
		revalidate();
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
				
		// Рисуем сетку
		g.setColor(Color.lightGray);			
		for (int i = 0; i < getWidth(); i += size) {
			for (int j = 0; j < getHeight(); j += size) {
				g.drawRect(i, j, size, size);
			}
		}

		// Стенки
		g.setColor(Color.BLACK);
		for (int i = 0; i < p.getBorderList().size(); i ++) {
			g.fillRect(p.getBorderList().get(i).getX()+1, p.getBorderList().get(i).getY()+1, size-1, size-1);
		}
		
		// Закрытые клетки
		g.setColor(Color.red);
		for (int i = 0; i < p.getClosedList().size(); i ++) {
			g.fillRect(p.getClosedList().get(i).getX()+1, p.getClosedList().get(i).getY()+1, size-1, size-1);
		}
		
		// Открытые клетки
		g.setColor(Color.green);
		for (int i = 0; i < p.getOpenList().size(); i ++) {
			g.fillRect(p.getOpenList().get(i).getX()+1, p.getOpenList().get(i).getY()+1, size-1, size-1);
		}

		// Путь
		g.setColor(Color.MAGENTA);
		for (int i = 0; i < p.getPath().size(); i ++) {
			g.fillRect(p.getPath().get(i).getX()+1, p.getPath().get(i).getY()+1, size-1, size-1);
		}
		
		// Начальная клетка (старт)
		if (p.getStartNode() != null) {
			g.setColor(Color.BLUE);
			g.fillRect(p.getStartNode().getX()+1, p.getStartNode().getY()+1, size-1, size-1);
		}

		// Конечная клетка (финиш)
		if (p.getEndNode() != null) {
			g.setColor(Color.YELLOW);
			g.fillRect(p.getEndNode().getX()+1, p.getEndNode().getY()+1, size-1, size-1);
		}

		if (hover) {
			m.hoverColor();
			g.setColor(new Color(25, 25, 25, 230));
		} else {
			m.nonhoverColor();
			g.setColor(new Color(0, 0, 0, 87));			
		}
		
		// Бэкграунд для меню
		g.fillRect(15,  getHeight() - 105, 215, 85);
		
		if(p.getStartNode() != null && p.getEndNode() != null) {
			m.getButton().setEnabled(true);
		} else {
			m.getButton().setEnabled(false);			
		}
		
		m.positionAll();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		if(running) {
			return;
		}
		
		int orderX, orderY; // Порядковый номер клетки по x, y
		int value = arg0.getWheelRotation();
		int prevSize = p.getSize();
		
		// Максимальный размер клетки = 100
		// Минимальный = 7
		if ( (value > 0 && prevSize < 7) 
				| (value < 0 && prevSize > 100) ) {
			return;
		}
		
		size = p.getSize() - value;
		p.setSize(size);
		
		// Изменяем размеры и координаты стенок
		for (int i = 0; i < p.getBorderList().size(); i++) {
			orderX = p.getBorderList().get(i).getX() / prevSize;
			orderY = p.getBorderList().get(i).getY() / prevSize;

			p.getBorderList().get(i).setXY(orderX * size, orderY * size);
		}
		
		// Изменяем размеры и координаты закрытых клеток, 
		// если только это не стартовая клетка
		for (int i = 0; i < p.getClosedList().size(); i++) {
			Node closedNode = new Node(p.getClosedList().get(i).getX(),
											p.getClosedList().get(i).getY()); 

			if(closedNode.isEqual(p.getStartNode())) {
				continue;
			} 
			
			orderX = p.getClosedList().get(i).getX() / prevSize;
			orderY = p.getClosedList().get(i).getY() / prevSize;
			
			p.getClosedList().get(i).setXY(orderX * size, orderY * size);
		}
		
		// Изменяем открытые клетки
		for (int i = 0; i < p.getOpenList().size(); i++) {
			orderX = p.getOpenList().get(i).getX() / prevSize;
			orderY = p.getOpenList().get(i).getY() / prevSize;
			
			p.getOpenList().get(i).setXY(orderX * size, orderY * size);
		}
		
		// Изменяем конечную клетку
		if(p.getEndNode() != null) {
			orderX = p.getEndNode().getX() / prevSize;
			orderY = p.getEndNode().getY() / prevSize;
			p.getEndNode().setXY(orderX * size, orderY * size);
		} 
		
		// Стартовую
		if(p.getStartNode() != null) {
			orderX = p.getStartNode().getX() / prevSize;
			orderY = p.getStartNode().getY() / prevSize;
			p.getStartNode().setXY(orderX * size, orderY * size);
		}
		
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		MapCalculation(arg0);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		int x = arg0.getX();
		int y = arg0.getY();
		
		if (x <= 230 && y<= (getHeight() - 15) 
				&& x >= 15 && y >= getHeight() - 105) {
			hover = true;
		} else {
			hover = false;
		}
		
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		key = e.getKeyChar();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		key = (char) 0;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		MapCalculation(arg0);
	}

	// Задает координаты для стартовой/конечной клетки или
	// для стенок
	private void MapCalculation(MouseEvent arg0) {
		int extraX, extraY;
		int x, y;
		
		if (SwingUtilities.isLeftMouseButton(arg0)) {
			if (key == 's' | key=='S' 
					| key == 'ы' | key == 'Ы') {
				extraX = arg0.getX() % size;
				extraY = arg0.getY() % size;
				
				x = arg0.getX() - extraX;
				y = arg0.getY() - extraY;
				
				int possibleBorder = p.searchBorder(x, y);
				
				if(possibleBorder != -1) {
					p.getBorderList().remove(possibleBorder);
				}
				
				if(p.getEndNode() != null && p.getEndNode().isEqual(new Node(x, y))) {
					p.setEndNode(null);
				}
				
				p.setStartNode(new Node(arg0.getX() - extraX, arg0.getY() - extraY));
			} else if (key == 'e' | key =='E' 
							| key == 'у' | key == 'У') {
				extraX = arg0.getX() % size;
				extraY = arg0.getY() % size;
				
				x = arg0.getX() - extraX;
				y = arg0.getY() - extraY;
				
				int possibleBorder = p.searchBorder(x, y);
				
				if(possibleBorder != -1) {
					p.getBorderList().remove(possibleBorder);
				}
				
				if(p.getStartNode() != null && p.getStartNode().isEqual(new Node(x, y))) {
					p.setStartNode(null);
				}
				
				p.setEndNode(new Node(arg0.getX() - extraX, arg0.getY() - extraY));			
			} else {
				extraX = arg0.getX() % size;
				extraY = arg0.getY() % size;
				
				x = arg0.getX() - extraX;
				y = arg0.getY() - extraY;
			
				if(p.getStartNode() != null && p.getStartNode().isEqual(new Node(x, y))) {
					p.setStartNode(null);
				} else if(p.getEndNode() != null && p.getEndNode().isEqual(new Node(x, y))) {
					p.setEndNode(null);					
				}
				
				p.addBorder(new Node(arg0.getX() - extraX, arg0.getY() - extraY));
			}
		} else {
			if (key == 's' | key=='S' 
					| key == 'ы' | key == 'Ы') {
				extraX = arg0.getX() % size;
				extraY = arg0.getY() % size;
				p.setStartNode(null);
			} else if (key == 'e') {
				extraX = arg0.getX() % size;
				extraY = arg0.getY() % size;
				p.setEndNode(null);			
			} else {
				extraX = arg0.getX() % size;
				extraY = arg0.getY() % size;
	
				x = arg0.getX() - extraX;
				y = arg0.getY() - extraY;
				
				int possibleBorder = p.searchBorder(x, y);
				
				if(possibleBorder != -1) {
					p.getBorderList().remove(possibleBorder);					
				}
			}
		}
				
		repaint();
	}

	// Вычисляет скорость таймера, основываясь на 
	// слайдере скорости
	private void setSpeed() {
		int value = m.getSlider().getValue();
		int delay = 0;
				
		if(value == 0) {
			t.stop();
		} else if(value >= 1 && value < 50) {
			if(t != null && t.isRunning()) {
				t.stop();
			}
			
			// Экспоненциальная функция. value(1) == delay(5000). value (50) == delay(25)
			delay = (int)(a1 * (Math.pow(25/5000.0000, value / 49.0000)));
		} else if(value >= 50 && value <= 100) {
			if(t != null && t.isRunning()) {
				t.stop();
			}
			
			// Экспоненциальная функция. value (50) == delay(25). value(100) == delay(1).
			delay = (int)(a2 * (Math.pow(1/25.0000, value/50.0000))); 
		}
		
		t = new Timer(delay, this);
	}
	
	private Color getRandomColor() {		
		r += 10;
		g += 10;
		b += 10;
		
		if(r > MAX | g > MAX | b > MAX) {
			r = (int) (Math.random() * ((MAX - MIN) + 1)) + MIN;
			g = (int) (Math.random() * ((MAX - MIN) + 1)) + MIN;
			b = (int) (Math.random() * ((MAX - MIN) + 1)) + MIN;
		}
		
		return new Color(r, g, b);
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String value = arg0.getActionCommand();

		// Проверяем чем был вызван метод: таймером (null) 
		// или кнопкой
		if (value == null) {
			
			// Завершился ли алгоритм
			if (p.isComplete() || p.noPath()) {
				finish();
				flicker();
			} else {
				p.setShowSteps(m.getChBox("showStepsBox").isSelected());
				p.findpath(p.getPar());	
				setSpeed();
				t.start();
			}
			
		} else if (value.equals("start")) {
			start();
		} else if (value.equals("stop")) {
			m.getButton().setText("start");
			t.stop();
		} else if (value.equals("clear")) {
			clear();
		}
		
		repaint();
	}
	
	// Анимация мерцающего фона
	private void flicker() {
		t.setDelay(80);
		setBackground(getRandomColor());
	}

	private void clear() {
		m.getButton().setText("start");		
		m.getLabel("steps").setText("steps: 0");			
		m.getChBox("diagonalBox").setEnabled(true);
		p.reset();
		t.stop();
		remove(m.getLabel("noPathLab"));
		setBackground(Color.WHITE);
	}

	private void start() {
		running = true;
		p.setDiagonal(m.getChBox("diagonalBox").isSelected());
		m.getChBox("diagonalBox").setEnabled(false);
		
		if(m.getChBox("showStepsBox").isSelected()) {
			m.getButton().setText("stop");		
			p.setShowSteps(true);

		} else {
			m.getButton().setText("clear");
			p.setShowSteps(false);
		}
		
		p.setup();
		p.findpath(p.getStartNode());
		setSpeed();
		t.start();
	}
	
	private void finish() {
		if (running) {
			running = false;
			m.getButton().setText("clear");
			m.getLabel("steps").setText("steps: " + p.getPath().size());
			
			if (p.noPath()) {
				m.addNoPath();
				revalidate();
			}
		}
	}
}
