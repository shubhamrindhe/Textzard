// Author : Shubham Rindhe.
// GitHub : https://github.com/shubhamrindhe/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Editor {
	public static ImageIcon logo;
	public static JFrame editorUI;
	public static JLabel status; 
	
	public static JMenuBar menubar;
	public static JMenu file;
	public static JMenuItem newDoc,open,save,saveAs,close,next,exit;

	public static JMenu text;
	public static JMenu textStyle;
	public static JMenuItem italic,bold;
	public static JMenu fontMenu;
	
	
	public static JToolBar tools;
	public static JButton newTool,saveTool,prev;
	
	public static JTabbedPane tabs;
	public static Vector<File> files;
	
	
	
	public static JComboBox<?> fontBox;
	public static JComboBox<?> fontSizeBox;
	
	public static String fontName;
	public static int fontStyle;
	public static int fontSize;
	public static String fonts[];
	public static String fontSizes[]={"10","11","12","14","16","18","20","22","24","26","28","30","36","48","72"};
	public static Font font;
	
	
	public static GraphicsEnvironment graphicsEnvironment;
	
	
	
	Editor(){
		
		logo = new ImageIcon("files/icons/logo.png");
		status = new JLabel("");
		editorUI = new JFrame("Bit-Beast");
		editorUI.setLayout(new BorderLayout());
		editorUI.setSize(600,600);
		editorUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		editorUI.setIconImage(logo.getImage());
		//editorUI.add(status,BorderLayout.AFTER_LAST_LINE);
		
		init();
		integrate();
		initListeners();
		integrateShortcuts();
		
		editorUI.setJMenuBar(menubar);
		editorUI.add(tools,BorderLayout.NORTH);
		editorUI.add(tabs);
		editorUI.add(status,BorderLayout.AFTER_LAST_LINE);
		
		editorUI.setVisible(true);
	}
	public static void init() {
		files = new Vector<File>();
		
		graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//fonts = graphicsEnvironment.getAvailableFontFamilyNames();
		
		menubar = new JMenuBar();
		tools = new JToolBar();
		tabs = new JTabbedPane();
		
		fontBox = new JComboBox(graphicsEnvironment.getAvailableFontFamilyNames());
		fontBox.setSelectedItem("Arial");
		fontSizeBox = new JComboBox(fontSizes);
		fontSizeBox.setSelectedItem("20");
		
		fontName = fontBox.getSelectedItem().toString();
		fontStyle = 0;
		fontSize = Integer.parseInt(fontSizeBox.getSelectedItem().toString(),10);
		font = new Font(fontName,fontStyle,fontSize);
	} 
	public static void integrate(){
		//Menus
		file = new JMenu("File");
		menubar.add(file);
			newDoc = new JMenuItem("new");
			open = new JMenuItem("open");
			save = new JMenuItem("save");
			saveAs = new JMenuItem("save as");	
			close = new JMenuItem("close");
			next = new JMenuItem("next");
			exit = new JMenuItem("Quit");
				file.add(newDoc);
				file.add(open);
				file.add(save);
				file.add(saveAs);
				file.add(close);
				file.add(next);
				file.add(exit);
		
					
		text = new JMenu("Text");	
		menubar.add(text);
			textStyle = new JMenu("Style");
				bold = new JCheckBoxMenuItem("Bold");
				italic = new JCheckBoxMenuItem("Italic");
				textStyle.add(bold);
				textStyle.add(italic);
				text.add(textStyle);
				
		fontMenu = new JMenu("Font");
		
		text.add(fontMenu);
		
		//toolBar
		newTool = new JButton(new ImageIcon("files/icons/new.gif"));
		tools.add(newTool);
		tools.add(fontSizeBox);
		tools.add(fontBox);
	}
	
	public static void initListeners() {
		newDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JTextArea textArea = new JTextArea();
				textArea.setFont(font);
				JScrollPane scrollPane = new JScrollPane(textArea);
				tabs.addTab("Untitled", scrollPane);
				//tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
				System.out.println(tabs.getTabCount());
				files.add(tabs.getTabCount()-1,null);
				tabs.setSelectedComponent(scrollPane);
				
			}
		});
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0){
				try {
						File Doc = fileGrabber('o');
						if(Doc!=null) {
							if(files.contains(Doc))
								tabs.setSelectedIndex(files.indexOf(Doc));
							else {
								files.add(Doc);
							
								JTextArea textarea = new JTextArea();
								textarea.setFont(font);
								JScrollPane scrollPane = new JScrollPane(textarea);
								
								textarea.setText(read(Doc));
								tabs.addTab(Doc.getName(),scrollPane);
								tabs.setSelectedComponent(scrollPane);
								
								status.setText(Doc.getName()+" Opened Successfully !");
							}
						}	
				}catch(Exception e) {
					e.printStackTrace();
				}
			}	
		});
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(tabs.getTabCount()!=0) {
					if(((File)files.get(tabs.getSelectedIndex()))==null) {
						try {
								File Doc = fileGrabber('s');
								int n = tabs.getSelectedIndex();
								files.add(n, Doc);
								if(Doc!=null) {
									savior(Doc,getTextArea(n));
									tabs.setTitleAt(n, ((File) files.get(n)).getName());
									status.setText(Doc.getName()+" Saved Successfully !");
								}	
						}catch(Exception e) {
							e.printStackTrace();
						}		
					}else {
						System.out.println("else");
						savior(((File)files.get(tabs.getSelectedIndex())),getTextArea(tabs.getSelectedIndex()));
					}
				}
			}
		});
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(tabs.getTabCount()!=0) {
					File Doc = fileGrabber('s');
					if(Doc!=null) {
						savior(Doc,getTextArea(tabs.getSelectedIndex()));
						int n = tabs.getSelectedIndex();
						System.out.println(((File) files.get(n)));
						files.set(n,Doc);
						tabs.setTitleAt(n, ((File) files.get(n)).getName());
					}
				}
			}
		});
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int i = tabs.getSelectedIndex();
				if(i==tabs.getTabCount()-1)
					tabs.setSelectedIndex(0);
				else
					tabs.setSelectedIndex(i+1); 
			}
		});
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(tabs.getTabCount()!=0) {
					files.remove(tabs.getSelectedIndex());
					tabs.removeTabAt(tabs.getSelectedIndex());
				}
			}
		});
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		bold.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				styler();
			}
		});
		italic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 styler();
			}
			
		});
		fontBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				styler();
			}
		});
		fontSizeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				styler();
			}
		});
	} 
	
	public static void styler(){
		fontName = fontBox.getSelectedItem().toString();
		fontSize = Integer.parseInt(fontSizeBox.getSelectedItem().toString(),10);
		fontStyle = ( bold.isSelected() ? Font.BOLD : 0 ) + ( italic.isSelected() ? Font.ITALIC : 0 );
		font = new Font(fontName,fontStyle, fontSize); 
		refresh();
	}
	
	private static void refresh() {
		//Font
		int n = tabs.getTabCount();
		System.out.println(n);
		for(int i=0;i<n;++i) {
			JTextArea textarea = getTextArea(i);
			textarea.setFont(font);
		}
	}
	public static String read(File file){
		String content = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			content = "";
			String line;
			while((line = br.readLine()) != null) {
				content += line+"\n";
			}
			if(content.length() > 0) {
				content = content.substring(0, content.length() - 1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public static void savior(File file,JTextArea Textarea) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			String content = Textarea.getText()+"\n";
			int idx, count = 0;
			do {
				idx = content.indexOf('\n');
				if(idx > 0 || count == 0) {
					bw.write(content.substring(0, idx));
					content = content.substring(idx+1);
					count++;
					bw.newLine();
				}
			}while(idx > 0);
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static JTextArea getTextArea(int index) {
		JScrollPane scrollPane = (JScrollPane) tabs.getComponent(index);
		JViewport view = scrollPane.getViewport();
		Component[] components = view.getComponents();
		return (JTextArea) components[0];
	}
	
	public static File fileGrabber(char choice) {
		try {
			JFileChooser grabber = new JFileChooser();
			if((choice=='s'?grabber.showSaveDialog(null):grabber.showOpenDialog(null))==JFileChooser.APPROVE_OPTION) {
				return grabber.getSelectedFile();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void integrateShortcuts() {
		KeyStroke newKS = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
		newDoc.setAccelerator(newKS);
		
		KeyStroke saveKS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
		save.setAccelerator(saveKS);
		
		KeyStroke openKS = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
		open.setAccelerator(openKS);
		
		KeyStroke closeKS = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.ALT_DOWN_MASK);
		close.setAccelerator(closeKS);
		
		KeyStroke exitKS = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK);
		exit.setAccelerator(exitKS);
		
		KeyStroke nextKS = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK);
		next.setAccelerator(nextKS);
	}
	
	public static void main(String args[]) {
		Editor theBitBeast = new Editor();
	}
	
}
