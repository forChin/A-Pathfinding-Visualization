import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.*;

/* Класс является панелью для найтсройки работы 
 * алгоритма (левый нижний угол). Вынес его в отдельный
 * класс, чтобы код в Frame.java не смотрелся громодзким.
 */

public class Menu {
	private Frame f;
	private JLabel speedLab, noPathLab, steps;
	private JCheckBox diagonalBox, showStepsBox;
	private JSlider speedSlider;
	private JButton startBtn;
	private boolean hover;
	private ArrayList<JLabel> labels;
	private ArrayList<JCheckBox> chBoxes;
	
	public Menu(Frame f) {
		this.f = f;
		
		labels = new ArrayList<>();
		chBoxes = new ArrayList<>();
		
		setup();
	}

	private void setup() {
		Font style = new Font("Monaco", Font.BOLD, 12);
		
		speedLab = new JLabel("speed: 50");
		speedLab.setOpaque(false);
		speedLab.setFont(style);
		speedLab.setName("speedLab");;
		speedLab.setVisible(true);
		
		steps = new JLabel("steps: 0");
		steps.setOpaque(false);
		steps.setFont(style);
		steps.setName("steps");
		steps.setVisible(true);
		
		noPathLab = new JLabel("NO PATH");
		noPathLab.setOpaque(false);
		noPathLab.setName("noPathLab");
		noPathLab.setForeground(Color.white);
		noPathLab.setFont(new Font("Monaco", Font.BOLD, 100));
		noPathLab.setVisible(true);
				
		diagonalBox = new JCheckBox("diagonal");
		diagonalBox.setFont(style);
		diagonalBox.setName("diagonalBox");
		diagonalBox.setOpaque(false);
		diagonalBox.setFocusable(false);
		diagonalBox.setSelected(true);
		diagonalBox.setVisible(true);
	
		showStepsBox = new JCheckBox("show steps");
		showStepsBox.setFont(style);
		showStepsBox.setName("showStepsBox");
		showStepsBox.setFocusable(false);
		showStepsBox.setOpaque(false);				
		showStepsBox.setFocusTraversalKeysEnabled(false);
		showStepsBox.setSelected(true);
		showStepsBox.setVisible(true);
		showStepsBox.addChangeListener((n) -> {
			if(showStepsBox.isSelected()) {
				speedSlider.setEnabled(true);
				speedLab.setEnabled(true);
				speedLab.setText("speed: " + speedSlider.getValue());
			} else {
				speedSlider.setEnabled(false);
				speedLab.setEnabled(false);
				speedLab.setText("speed: ...");
			}
		});
		
		labels.add(speedLab);
		labels.add(steps);
		labels.add(noPathLab);
		chBoxes.add(diagonalBox);
		chBoxes.add(showStepsBox);
		
		startBtn = new JButton("start");
		startBtn.setMargin(new Insets(0, 0, 0, 0));
		startBtn.setVisible(true);
		startBtn.setFocusable(false);
		startBtn.addActionListener(f);
		startBtn.setEnabled(false);
		
		speedSlider = new JSlider();
		speedSlider.setOpaque(false);
		speedSlider.setPreferredSize(new Dimension(130, 7));
		speedSlider.setFocusable(false);
		speedSlider.addChangeListener((n) -> {
			speedLab.setText("speed: " + speedSlider.getValue());
		});
		speedSlider.setVisible(true);
	}
	
	// Если мышь не наведена на меню, то 
	// используются цвета ниже.
	public void nonhoverColor() {
		speedLab.setForeground(Color.black);
		steps.setForeground(Color.black);
		showStepsBox.setForeground(Color.black);
		diagonalBox.setForeground(Color.black);
		hover = false;
	}
	
	// Если мышь наведена на меню, то 
	// используются цвета ниже.
	public void hoverColor() {
		speedLab.setForeground(Color.white);
		steps.setForeground(Color.white);
		showStepsBox.setForeground(Color.white);
		diagonalBox.setForeground(Color.white);
		hover = true;
	}
	
	public void positionAll() {
		showStepsBox.setBounds(20, f.getHeight() - 100, showStepsBox.getPreferredSize().width, showStepsBox.getPreferredSize().height);
		diagonalBox.setBounds(20, f.getHeight() - 72, diagonalBox.getPreferredSize().width, diagonalBox.getPreferredSize().height);
		speedLab.setBounds(25, f.getHeight() - 40, speedLab.getPreferredSize().width, speedLab.getPreferredSize().height);
		speedSlider.setBounds(100, f.getHeight() - 35, speedSlider.getPreferredSize().width, speedSlider.getPreferredSize().height);
		startBtn.setBounds(160, f.getHeight() - 97, startBtn.getPreferredSize().width, startBtn.getPreferredSize().height);
		steps.setBounds(155, f.getHeight() - 68, steps.getPreferredSize().width, steps.getPreferredSize().height);
		
	}
	
	public void addNoPath() {		
		noPathLab.setBounds((f.getWidth() - noPathLab.getPreferredSize().width)/2, 
								(f.getHeight()-noPathLab.getPreferredSize().height)/2, 
										noPathLab.getPreferredSize().width, noPathLab.getPreferredSize().height);
		
		f.add(noPathLab);
	}
	
	public void addAll() {
		positionAll();
		f.add(speedLab);
		f.add(showStepsBox);
		f.add(diagonalBox);
		f.add(speedSlider);
		f.add(steps);
		f.add(startBtn);
	}
	
	// Получить определенный JCheckBox
	public JCheckBox getChBox(String name) {
		for (int i = 0; i < chBoxes.size(); i++) {
			if (chBoxes.get(i).getName() == name) {
				return chBoxes.get(i);
			}
		}
		
		return null;
	}

	// Получить определенный JLabel
	public JLabel getLabel(String name) {
		for (int i = 0; i < labels.size(); i++) {
			if (labels.get(i).getName() == name) {
				return labels.get(i);
			}
		}
		
		return null;
	}
	
	public JSlider getSlider() {
		return speedSlider;
	}
	
	public JButton getButton() {
		return startBtn;
	}
}
