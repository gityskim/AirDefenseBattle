
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScorePanel extends JPanel { // 게임 점수 표시 및 관리 하는 Panel
	private int score = 0; // 점수를 저장하는 변수, 0부터 시작
	private int heart; // 초기 생명 수를 저장하는 변수
	private int lives; // 현재 남은 생명 수를 저장하는 변수
	private JLabel scoreLabel = new JLabel(Integer.toString(score)); // 점수를 표시할 Label
	private ArrayList<JLabel> heartIcons = new ArrayList<>(); // 하트 이미지를 저장하는 리스트
	private int addScore = 10; // 기본 점수 증가량
	private JPanel heartPanel; // 하트 아이콘을 별도로 표시하는 Panel
	private Image backgroundImage; // 배경 이미지

	public ScorePanel(int initialLives) {
		this.lives = this.heart = initialLives; // 초기 생명수와 현재 생명수를 초기화
		setLayout(new BorderLayout()); // BorderLayout으로 레이아웃 설정
		this.backgroundImage = new ImageIcon("src/resources/score_img.png").getImage(); // 배경 이미지 로드	

		// 상단 점수 표시 패널 생성
		JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 좌측 정렬된 FlowLayout 사용
		JLabel scoreTextLabel = new JLabel(" 점수: "); // "점수:" 텍스트 레이블 생성
		scoreTextLabel.setFont(new Font("Arial 굵게", Font.BOLD, 16)); // 볼드체로 설정
		scorePanel.add(scoreTextLabel); // 점수 텍스트 레이블 추가

		scoreLabel.setFont(new Font("Arial", Font.BOLD, 16)); // 점수 표시 레이블의 폰트 설정
		scorePanel.add(scoreLabel); // 점수 레이블을 점수 패널에 추가
		add(scorePanel, BorderLayout.NORTH); // 점수 패널을 상단에 배치
		scorePanel.setOpaque(false); // 점수 패널의 배경 투명화

		// 하단 하트 표시 패널 생성
		JPanel livesPanel = new JPanel(new BorderLayout()); // BorderLayout으로 레이아웃 설정
		livesPanel.setOpaque(false); // 생명 패널의 배경 투명화
		JLabel livesLabel = new JLabel("  생명: "); // "생명:" 텍스트 레이블 생성
		livesLabel.setFont(new Font("Arial 굵게", Font.BOLD, 16)); // 볼드체로 설정
		livesPanel.add(livesLabel, BorderLayout.NORTH); // 생명 텍스트 레이블을 상단에 배치

		heartPanel = new JPanel(); // 하트 아이콘을 표시할 객체 생성
		heartPanel.setLayout(new GridLayout(0, 5, 5, 2)); // 5개씩 표시
		heartPanel.setOpaque(false); // 하트 패널의 배경 투명화

		// 초기 하트 추가
		for (int i = 0; i < lives; i++) {
			addHeartIcon(); // 하트 아이콘 추가 메소드 호출
		}

		livesPanel.add(heartPanel, BorderLayout.CENTER); // 하트 패널을 생명 패널의 중앙에 배치
		add(livesPanel, BorderLayout.SOUTH); // 생명 패널을 ScorePanel 하단에 배치
	}

	public void reset() { // 점수 및 생명 초기화 메서드
		this.score = 0; // 점수를 0으로 초기화
		scoreLabel.setText(Integer.toString(score)); // 점수 레이블 업데이트
		setLives(heart); // 생명을 초기값으로 재설정
	}

	public void changeAddScore(int score) { // 득점 점수 변경
		this.addScore = score; // 새로운 점수 증가량을 설정
	}

	public void increase() { // 점수 증가 메소드
		score += addScore; // 난이도에 따라 증가량 반영
		scoreLabel.setText(Integer.toString(score)); // 점수 레이블 업데이트
	}

	public void decreaseLife() { // 라이프 감소
		if (lives > 0) { // 생명이 0보다 크면
			lives--; // 생명 감소
			removeHeartIcon(); // 하트 아이콘 제거 메서드 호출
		}
	}

	public void setLives(int newLives) { // 라이프 세팅, 난이도 설정에서 사용됨
		heartPanel.removeAll(); // 기존 하트 제거
		heartIcons.clear(); // 하트 리스트 초기화
 
		lives = heart = newLives; // 새로운 생명 수 설정
		for (int i = 0; i < lives; i++) {
			addHeartIcon(); // 새 하트 추가
		}
		revalidate(); // 레이아웃 갱신
		repaint(); // 화면 갱신
	}

	private void addHeartIcon() { // 하트 아이콘 추가 메소드
		JLabel heart = new JLabel(new ImageIcon("src/resources/heart.png")); // 하트 이미지 레이블 생성
		heartIcons.add(heart); // 하트 리스트에 추가
		heartPanel.add(heart); // 하트 패널에 추가
	}

	private void removeHeartIcon() { // 라이프 제거
		if (!heartIcons.isEmpty()) { // 하트 리스트가 비어있지 않은 경우
			JLabel heart = heartIcons.remove(heartIcons.size() - 1); // 마지막 하트 제거
			heartPanel.remove(heart); // 하트 패널에서 제거
			revalidate(); // 레이아웃 갱신
			repaint(); // 화면 갱신
		}
	}

	public int getLives() { // 라이프 반환
		return lives; // 남은 생명 수 반환
	}

	public int getScore() { // 현재 점수 반환 메소드
		return score; // 현재 점수 반환
	}
	
	@Override
    protected void paintComponent(Graphics g) { // 패널의 배경을 그리는 메서드
        super.paintComponent(g); // 부모 클래스의 paintComponent 호출
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // 배경 이미지를 패널 크기에 맞게 그림
    }

}
