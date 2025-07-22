import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class CustomSettingsPanel extends JPanel { // 사용자 정의 게임 설정 패널
    private GameFrame gameFrame; // 게임 프레임 참조
    private JSlider speedSlider, NumSlider, heartSlider, scoreSlider; // 슬라이더 컴포넌트들
    private JLabel speedLabel, NumLabel, heartLabel, scoreLabel; // 슬라이더에 대한 레이블
    private JButton applyButton; // 설정 적용 버튼

    public CustomSettingsPanel(GameFrame gameFrame) { // 생성자: 초기화 및 구성 요소 설정
        this.gameFrame = gameFrame; // 전달된 GameFrame을 참조로 저장

        setLayout(new GridLayout(6, 2, 10, 10)); // 6행 2열의 GridLayout, 셀 간 여백 10px

        // 낙하 속도 슬라이더 생성
        speedLabel = new JLabel("  Falling Speed: "); // 레이블 설정
        speedSlider = new JSlider(1, 10, 7);  // 최소값 1, 최대값 10, 기본값 7
        speedSlider.setMajorTickSpacing(1); // 주요 눈금 간격 1
        speedSlider.setMinorTickSpacing(1); // 세부 눈금 간격 1
        speedSlider.setPaintTicks(true); // 눈금선 표시
        speedSlider.setPaintLabels(true); // 눈금 값 표시

        // 단어 개수 슬라이더
        NumLabel = new JLabel("  Falling Word Num: "); // 레이블 설정
        NumSlider = new JSlider(1, 10, 5); // 최소값 1, 최대값 10, 기본값 5
        NumSlider.setMajorTickSpacing(1); // 주요 눈금 간격 1
        NumSlider.setMinorTickSpacing(1); // 세부 눈금 간격 1
        NumSlider.setPaintTicks(true); // 눈금선 표시
        NumSlider.setPaintLabels(true); // 눈금 값 표시

     // 하트(생명) 개수 슬라이더 생성
        heartLabel = new JLabel("  Number of Hearts: "); // 레이블 설정
        heartSlider = new JSlider(1, 10, 3);  // 최소값 1, 최대값 10, 기본값 3
        heartSlider.setMajorTickSpacing(1); // 주요 눈금 간격 1
        heartSlider.setMinorTickSpacing(1); // 세부 눈금 간격 1
        heartSlider.setPaintTicks(true); // 눈금선 표시
        heartSlider.setPaintLabels(true); // 눈금 값 표시

        // 점수 증가량 슬라이더 생성
        scoreLabel = new JLabel("  Score Increment: "); // 레이블 설정
        scoreSlider = new JSlider(0, 50, 10);  // 최소값 0, 최대값 50, 기본값 10
        scoreSlider.setMajorTickSpacing(5); // 주요 눈금 간격 5
        scoreSlider.setMinorTickSpacing(1); // 세부 눈금 간격 1
        scoreSlider.setPaintTicks(true); // 눈금선 표시
        scoreSlider.setPaintLabels(true); // 눈금 값 표시

        // 설정 적용 버튼 생성
        applyButton = new JButton("  Apply Settings"); // 버튼 텍스트 설정
        applyButton.addActionListener(new ActionListener() { // 버튼 클릭 이벤트 리스너 추가
            @Override
            public void actionPerformed(ActionEvent e) {
                // 슬라이더 값 가져오기
                int speed = speedSlider.getValue(); // 낙하 속도
                int count = NumSlider.getValue(); // 낙하 단어 개수
                int hearts = heartSlider.getValue(); // 생명(하트) 개수
                int scoreIncrement = scoreSlider.getValue(); // 점수 증가량

                // 게임 설정 변경
                gameFrame.getGamePanel().changeFallingSpeed(speed);  // GamePanel에서 낙하 속도 설정
                gameFrame.getGamePanel().changeFallingNum(count);    // GamePanel에서 단어 개수 설정
                gameFrame.getScorePanel().setLives(hearts);          // ScorePanel에서 생명 개수 설정
                gameFrame.getScorePanel().changeAddScore(scoreIncrement); // ScorePanel에서 점수 증가량 설정

                // 사용자에게 설정 적용 알림
                JOptionPane.showMessageDialog(CustomSettingsPanel.this, "Settings Applied");
            }
        });

        // 슬라이더와 레이블을 패널에 추가
        add(speedLabel);
        add(speedSlider);
        add(NumLabel);
        add(NumSlider);
        add(heartLabel);
        add(heartSlider);
        add(scoreLabel);
        add(scoreSlider);
        add(new JLabel());  // 빈 공간 채우기
        add(applyButton);   // 적용 버튼 추가
    }
}
