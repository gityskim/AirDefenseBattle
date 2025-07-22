
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class GameFrame extends JFrame {  // JFrame을 상속받아서 GUI를 구성하는 GameFrame 클래스
	private TextSource textSource = new TextSource(); // 단어를 관리할 TextSource 생성
	private ScorePanel scorePanel = new ScorePanel(3); // 점수를 관리할 ScorePanel 생성
	private EditPanel editPanel = new EditPanel(textSource); // 단어를 추가 기능을 할 EditPanel 생성
	private GamePanel gamePanel = new GamePanel(scorePanel, textSource, editPanel); // 게임이 굴러갈 gamePanel 생성
	private SoundPlayer backgroundMusicPlayer = new SoundPlayer(); // 배경 음악을 재생할 SoundPlayer 생성
	
	public GameFrame() {
		setTitle("공중 방어전"); // 타이틀은 "공중 방어전"으로 설정
		setSize(1000, 720); // 윈도우 사이즈 1000x720
		setResizable(false); // 윈도우 크기 조절 불가능하게 설정
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫을때 프로그램 종료
		makeMenu(); // Menu 생성
		makeToolBar(); // ToolBar 생성
		makeSplit(); // SplitPane 생성
		setVisible(true); // 창이 보이도록 설정
		
		playBackgroundMusic(); // 배경 음악 재생
		
	}
	
	public GamePanel getGamePanel() {
        return gamePanel; // gamePanel 리턴
    }
	
	public ScorePanel getScorePanel() {
        return scorePanel; //scorePanel 리턴
    }
	
	private void playBackgroundMusic() {
	    new Thread(new Runnable() { // 배경 음악 재생을 위한 새로운 스레드 생성
	        @Override
	        public void run() {
	            backgroundMusicPlayer.playSound("src/resources/background_music.wav", 0.05f, true); // 배경 음악 재생
	        }
	    }).start();
	}

	
	private void openCustomSettings() {
        CustomSettingsPanel customSettingsPanel = new CustomSettingsPanel(this); // 사용자 설정 패널 생성
        JFrame customFrame = new JFrame("Customize Game Settings"); // 사용자 설정 창 생성
        customFrame.setSize(400, 500); // 설정 창 크기 설정
        customFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫기 설정, 하나만 닫도록 DISPOSE_ON_CLOSE 사용
        customFrame.add(customSettingsPanel); // 설정 창에 패널 추가
        customFrame.setVisible(true); // 설정 창 표시
    }
	
	private void makeSplit() { // 창을 나누는 SplitPane 생성
		JSplitPane hPane = new JSplitPane(); // 수평 SplitPane 생성
		hPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT); // hPane의 방향 설정, 수평 방향의 SplitPane
		hPane.setDividerLocation(750); // 나누는 위치
		hPane.setEnabled(false); // 나누는 위치 변경 불가
		getContentPane().add(hPane, BorderLayout.CENTER); // hPane을 BorderLayout의 CENTER영역에 배치
		
		
		JSplitPane vPane = new JSplitPane(); // 수직 SplitPane 생성
		vPane.setOrientation(JSplitPane.VERTICAL_SPLIT); // vPane의 방향 설정, 수직 방향의 SplitPane
		hPane.setRightComponent(vPane); // 수평 SplitPane의 오른쪽에 수직 SplitPane 배치
		hPane.setLeftComponent(gamePanel); // 수평 SplitPane의 왼쪽에 gamePanel 배치
		vPane.setDividerLocation(250); // 수직 SplitPane 분리되는 위치를 250으로 설정
		vPane.setTopComponent(scorePanel); // vPane의 top영역에 scorePanel 배치
		vPane.setBottomComponent(editPanel); // vPane의 Bottom영역에 editpanel 배치
	}
	
	private void makeToolBar() { // ToolBar를 만드는 메소드
		JToolBar tBar = new JToolBar(); // ToolBar 생성
		tBar.setFloatable(false); // ToolBar가 움직이지 못하도록 설정, 핸들이 사라짐
		getContentPane().add(tBar, BorderLayout.NORTH); // 컨텐트 팬에 NORTH 방향에 tBar 배치
		JButton startBtn = new JButton("Start"); // "Start" 버튼 생성
		tBar.add(startBtn); // 툴바에 "Start" 버튼 추가
		
		startBtn.addActionListener(new ActionListener() { // start 버튼의 이벤트 추가

				@Override
				public void actionPerformed(ActionEvent e) {
					gamePanel.startGame(); // 게임 시작		
				}
				
			});
	}
	
	private void makeMenu() {  // Menu를 만드는 메소드
		JMenuBar mb = new JMenuBar();  // MenuBar 생성
		this.setJMenuBar(mb); // 현재 프레임에 MenuBar 배치
		
		JMenu fileMenu = new JMenu("File"); // File 메뉴 생성
		mb.add(fileMenu); // 메뉴바 mb에 fileMenu 배치
		JMenu levelMenu = new JMenu("난이도"); // "Edit" 메뉴 생성
		mb.add(levelMenu); // 메뉴바 mb에 editMenu 배치
		JMenu editMenu = new JMenu("Edit"); // "Edit" 메뉴 생성
		mb.add(editMenu); // 메뉴바 mb에 editMenu 배치
		
		
		JMenuItem startItem = new JMenuItem("Start");
		fileMenu.add(startItem); // MenuItem fileMenu에 startItem 배치
		startItem.addActionListener(new ActionListener() { // startItem의 이벤트 추가

				@Override
				public void actionPerformed(ActionEvent e) {
					gamePanel.startGame(); // 게임 시작
				}
				
			});
		
		JMenuItem stopItem = new JMenuItem("Stop"); // "Stop" 메뉴 아이템 생성
		fileMenu.add(stopItem); // MenuItem fileMenu에 stopItem 배치
		stopItem.addActionListener(new ActionListener() { // stopItem의 이벤트 추가

			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.gameOver(); // 게임 종료
			}
			
		});
			
		JMenuItem exitItem = new JMenuItem("Exit"); // "Exit" 메뉴 아이템 생성
		fileMenu.add(exitItem); // MenuItem fileMenu에 exitItem 배치
		exitItem.addActionListener(new ActionListener() { // exitItem의 이벤트 추가
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        System.exit(0); // 프로그램 종료
		    }
		});
		
		JMenuItem customSettingsItem = new JMenuItem("Customize Game Settings"); // 사용자 설정 메뉴 아이템 생성
		customSettingsItem.addActionListener(new ActionListener() { // customSettingsItem의 이벤트 추가
            @Override
            public void actionPerformed(ActionEvent e) {
                openCustomSettings(); // 사용자 설정 창 열기
            }
        });
        editMenu.add(customSettingsItem); // editMenu에 customSettingsItem 추가
        
		addDifficultyMenu(levelMenu); // 난이도 메뉴 항목 추가
	}
	
	private void addDifficultyMenu(JMenu levelMenu) { // 난이도 메뉴 항목을 추가하는 메소드
        JMenuItem easyItem = new JMenuItem("Easy"); // "Easy" 메뉴 아이템 생성
        levelMenu.add(easyItem); // levelMenu에 easyItem 배치
        easyItem.addActionListener(new ActionListener() { // easyItem의 이벤트 추가

            @Override
            public void actionPerformed(ActionEvent e) {
            	gamePanel.changeFallingNum(5); // 떨어지는 객체 개수 설정
                gamePanel.changeFallingSpeed(5); // 떨어지는 속도 설정
                scorePanel.changeAddScore(10); // 점수 증가량 설정
                scorePanel.setLives(5); // 생명 설정
                gamePanel.changeBackground("src/resources/easy_background.png"); // 배경 이미지 변경
            }
            
        });

        JMenuItem normalItem = new JMenuItem("Normal"); // "Normal" 메뉴 아이템 생성
        levelMenu.add(normalItem); // levelMenu에 normalItem 배치
        normalItem.addActionListener(new ActionListener() { // normalItem의 이벤트 추가

            @Override
            public void actionPerformed(ActionEvent e) {
            	gamePanel.changeFallingNum(7); // 떨어지는 객체 개수 설정
                gamePanel.changeFallingSpeed(7); // 떨어지는 속도 설정
                scorePanel.changeAddScore(20); // 점수 증가량 설정
                scorePanel.setLives(3); // 생명 설정
                gamePanel.changeBackground("src/resources/normal_background.png"); // 배경 이미지 변경
            }
            
        });

        JMenuItem hardItem = new JMenuItem("Hard"); // "Hard" 메뉴 아이템 생성
        levelMenu.add(hardItem); // levelMenu에 hardItem 배치
        hardItem.addActionListener(new ActionListener() { // hardItem의 이벤트 추가

            @Override
            public void actionPerformed(ActionEvent e) {
            	gamePanel.changeFallingNum(10); // 떨어지는 객체 개수 설정
                gamePanel.changeFallingSpeed(9); // 떨어지는 속도 설정
                scorePanel.changeAddScore(30); // 점수 증가량 설정
                scorePanel.setLives(2); // 생명 설정
                gamePanel.changeBackground("src/resources/hard_background.png"); // 배경 이미지 변경

            }
            
        });
    }

	
	public static void main(String[] args) {
		new GameFrame(); // GameFrame 객체 생성
	}

}
