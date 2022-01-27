package com.rsDev32.fiparcel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class Window extends JFrame {
	private static final long serialVersionUID=1L;
	private static final int[] DEFAULT_WINDOW_SIZE= {500, 500};
	private static final String TITLE="FiParcel";
	private JPanel firstScreen=new FirstScreen(), sendScreen=new WaitScreen("Sharing", ": 4321"), recieveScreen=new WaitScreen("Recieveing", ":4321"), lastScreen=firstScreen;
	public Window() {		
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch(Exception ex) {
	        ex.printStackTrace();
	    }
		
		setSize(DEFAULT_WINDOW_SIZE[0], DEFAULT_WINDOW_SIZE[1]);
		setTitle(TITLE);
		getContentPane().setBackground(Color.decode("#8cefff"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel header=new JLabel(TITLE, SwingConstants.CENTER);
		header.setFont(new Font("Arial", Font.BOLD, 45));
		header.setForeground(Color.BLACK);
		
		add(header, BorderLayout.NORTH);
		add(firstScreen);
		setVisible(true);
	}
	
	private void switchScreen(JPanel screen) {
		getContentPane().remove(lastScreen);
		Window.this.add(screen);
		lastScreen=screen;
		Window.this.revalidate();
	}
	class FirstScreen extends JPanel {
		private static final long serialVersionUID=1L;
		private FirstScreen() {
			setBackground(Color.decode("#8cefff"));
			setLayout(new GridBagLayout());
			GridBagConstraints gbc=new GridBagConstraints();
			
			JButton send=new AppButton("SEND");
			send.addActionListener((e) -> {
				
				//Choose Files
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setMultiSelectionEnabled(true);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				if(fileChooser.showOpenDialog(Window.this)==JFileChooser.APPROVE_OPTION) {
					File[] files=fileChooser.getSelectedFiles();
					
					if(files.length>0) {
						try {
							Server.start(files);
							switchScreen(sendScreen);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});
			JButton recieve=new AppButton("  RECIEVE  ");
			recieve.addActionListener((e) -> {
				switchScreen(recieveScreen);
				try {
					Server.start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			JPanel container=new JPanel(new GridLayout(2, 1));
			container.add(send);
			container.add(recieve);			
			
			add(container, gbc);
		}
		
		class AppButton extends JButton {
			private static final long serialVersionUID=1L;
			AppButton(String text) {
				super(text);
				setFont(new Font("Arial", Font.PLAIN, 30));
				setBorder(new RoundedBorder(10));
			}
			
			private class RoundedBorder implements Border {
			    private int radius;

			    RoundedBorder(int radius) {
			        this.radius = radius;
			    }
			    public Insets getBorderInsets(Component c) {
			        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
			    }
			    public boolean isBorderOpaque() {
			        return true;
			    }
			    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			        g.drawRoundRect(x, y, width-1, height-1, radius, radius);
			    }
			}
		}
	}
	
	class WaitScreen extends JPanel {
		private static final long serialVersionUID=1L;
		private WaitScreen(String action, String url) {
			setBackground(Color.decode("#8cefff"));
			JLabel lbl=new JLabel("<html>"+action+" on "+url+"<br>Open "+url+" in your browser</html>");
			lbl.setFont(new Font("Calibri", Font.PLAIN, 25));
			
			setLayout(new GridBagLayout());
			add(lbl, new GridBagConstraints());
		}
	}
}
