import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ImageIcon;

public class EditPanel extends JPanel {
    private JTextField tf = new JTextField(10); // 단어 입력 텍스트 필드
    private JTextArea rankingTextArea; // 랭킹을 표시할 텍스트 영역
    private TextSource textSource = null; // 단어를 관리하는 텍스트 소스 객체
    private Image backgroundImage; // 배경 이미지 객체

    public EditPanel(TextSource textSource) { // EditPanel 생성자
        this.textSource = textSource; // 전달받은 텍스트 소스를 필드에 저장
        this.backgroundImage = new ImageIcon("src/resources/edit_img.png").getImage(); // 배경 이미지 로드

        setLayout(null); // 자유 배치 설정
        initializeComponents(); // 구성 요소 초기화 메서드 호출
        
        // 프로그램 시작 시 점수 파일에서 점수를 로드
        ScoreManager.loadScores();
        updateRanking(); // 랭킹 업데이트
    }

    private void initializeComponents() { // 구성 요소를 초기화하는 메소드
        // 텍스트 필드 설정
        tf.setBounds(20, 20, 120, 30); // 위치 및 크기 설정
        add(tf); // 패널에 추가

        // "추가" 버튼 생성 및 설정
        JButton btn = new JButton("추가");
        btn.setBounds(150, 20, 60, 30); // 위치 및 크기 설정
        add(btn); // 패널에 버튼 추가

        btn.addActionListener(new ActionListener() { // 버튼 클릭 이벤트 리스너 추가
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = tf.getText(); // 텍스트 필드에 입력된 단어 가져오기
                if (word.length() == 0) return; // 입력이 없으면 무시

                textSource.add(word); // 텍스트 소스에 단어 추가
                tf.setText(""); // 입력 필드 초기화
            }
        });

        // 랭킹 텍스트 영역 설정
        rankingTextArea = new JTextArea(); 
        rankingTextArea.setEditable(false); // 텍스트 영역을 읽기 전용으로 설정
        rankingTextArea.setOpaque(false); // 배경을 투명하게 설정
        rankingTextArea.setBackground(null); // 배경색 제거
        rankingTextArea.setBorder(null); // 테두리 제거
        rankingTextArea.setBounds(20, 60, 180, 300); // 위치 및 크기 설정
        rankingTextArea.setFont(rankingTextArea.getFont().deriveFont(java.awt.Font.BOLD, 14f)); // 글자 크기 및 스타일 설정
        add(rankingTextArea); // 패널에 추가

        updateRanking(); // 초기 랭킹 텍스트를 업데이트
    }

    public void recordScore(int score) { // 점수를 기록하는 메소드
        ScoreManager.addScore(score); // 점수를 ScoreManager에 저장
        updateRanking(); // 랭킹 업데이트
    }

    public void updateRanking() { // 랭킹 정보를 업데이트하는 메소드
        List<Integer> scores = ScoreManager.getTopScores(5); // 랭킹 정보를 표시할 텍스트 빌더 생성
        StringBuilder rankingText = new StringBuilder("랭킹:\n"); // 텍스트 빌더로 랭킹 구성
        for (int i = 0; i < scores.size(); i++) {
            rankingText.append((i + 1)).append("위: ").append(scores.get(i)).append("점\n"); // 순위와 점수 추가
        }
        rankingTextArea.setText(rankingText.toString()); // 랭킹 텍스트 영역에 설정
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // 부모 클래스의 paintComponent 호출
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // 배경 이미지를 패널 크기에 맞게 그림
    }
}
