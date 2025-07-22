import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon; // ★ 이미지 아이콘을 사용하기 위한 import 추가
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GamePanel extends JPanel {
	private GameGroundPanel ground = new GameGroundPanel(); // 단어가 떨어지는 GameGroundPanel 겍체 생성
	private InputPanel input = new InputPanel(); // 단어 입력을 처리하는 InputPanel 객체 생성
	private ScorePanel scorePanel = null; // 점수를 관리하는 scorePanel 패널
	private TextSource textSource = null; // 단어 데이터를 관리하는 textSource 패널
	private EditPanel editPanel = null; // 단어 추가 및 랭킹을 처리할 editPanel 패널
	private ArrayList<FallingWord> fallingWords = new ArrayList<>(); // 떨어질 단어들을 담는 리스트
	private FallingThread fThread = new FallingThread(); // 단어 낙하를 관리하는 스레드
	private volatile boolean isSlowMotion = false; // 속도 감소 상태 여부, volatile으로 선언된 변수는 값을 변경했을 때 다른 스레드에서 항상 최신 값을 읽어갈 수 있도록 함
	private ImageIcon defaultBackground; // 기본 배경 이미지
	private ImageIcon slowMotionBackground; // 모래시계 효과 배경 이미지
	private volatile long slowMotionEndTime = 0; // 느려진 상태 지속시간 변수, volatile으로 선언된 변수는 값을 변경했을 때 다른 스레드에서 항상 최신 값을 읽어갈 수 있도록 함
	private int fallingSpeed = 7; // 단어 낙하 속도
	private int fallingNum = 5; // 동시에 떨어지는 단어의 수
	private int wordCount = 0; // 맞춘 단어 개수
	private volatile boolean isGameOver = false; // 게임 오버 상태


	public GamePanel(ScorePanel scorePanel, TextSource textSource, EditPanel editPanel) {
		this.scorePanel = scorePanel; // scorePanel 초기화
		this.textSource = textSource; // textSource 초기화
		this.editPanel = editPanel; // editPanel 초기화

		setLayout(new BorderLayout()); // 레이아웃을 BorderLayout으로 설정
		add(ground, BorderLayout.CENTER);  // 게임이 진행될 ground 영역 가운데에 추가
		add(input, BorderLayout.SOUTH); // 하단에 단어 입력 패널 추가
	}

	public void startGame() { // 게임 시작 메소드
	    if (isGameOver) { // 게임 오버 상태라면
	        resetGame(); // 게임 데이터 초기화
	    }
	    synchronized (fallingWords) { // 동시에 하나의 스레드만 코드에 접근하도록 제한, 여러 스레드가 공유 자원인 fallingWords에 접근할 때 데이터 충돌을 방지하기 위해서 사용
	        fallingWords.clear(); // 기존의 단어들을 모두 제거
	        for (int i = 0; i < fallingNum; i++) {
	            newWord(); // 난이도에 따라 단어 생성
	        }
	    }
	    fThread = new FallingThread(); // 새로운 스레드 생성
	    fThread.start(); // 스레드 시작
	    isGameOver = false; // 게임 상태 시작으로 변경
	}
	
	private void resetGame() { // 게임 데이터를 초기화하는 메소드
	    fallingWords.clear(); // 떨어지는 단어 리스트 초기화
	    ground.clearAllWords(); // GameGroundPanel 의 단어 제거
	    scorePanel.reset(); // 점수 및 생명 초기화
	    wordCount = 0; // 맞춘 개수 초기화
	    editPanel.updateRanking(); // 랭킹 정보 갱신
	}

	public void changeBackground(String backgroundPath) { // 배경을 변경하는 메소드
		defaultBackground = new ImageIcon(backgroundPath); // 새로운 배경 이미지 로드, 모래시계를 발동한 후 난이도에 맞는 원래 이미지로 되돌리기 위해 defaultBackground에 할당
		repaint(); // 화면 갱신
	}

	public void changeFallingSpeed(int speed) { // 단어 낙하 속도를 변경하는 메소드 
		this.fallingSpeed = speed; // 단어 낙하 속도 설정
	}

	public void changeFallingNum(int num) { // 떨어지는 단어 개수를 변경하는 메소드
		fallingWords.clear(); // 기존 단어 초기화
		// 단어 생성은 게임 시작 시 이루어짐
		this.fallingNum = num; // 난이도에 따라 단어 수만 설정
	}

	private void newWord() { // 단어를 추가하는 메소드
		String text = textSource.get(); // 텍스트 소스에서 단어 가져오기
		Random random = new Random(); // radom.nextInt 사용을 위한 랜덤 객체 생성
		int x = random.nextInt(600); // 600 범위 내의 x좌표
		int y = random.nextInt(70); // 단어가 겹치게 떨어지지 않도록 y축 70범위 내로 설정

		// ★ 단어 종류 결정: 일반(80%), 모래시계(15%), 폭탄(5%)
		int type = random.nextInt(100);
		FallingWord word;
		if (type < 10) {
			word = new FallingWord(text, x, -y, false, true); // 폭탄 단어 생성
		} else if (type < 25) {
			word = new FallingWord(text, x, -y, true, false); // 모래시계 단어 생성
		} else {
			word = new FallingWord(text, x, -y, false, false); // 일반 단어 생성
		}

		synchronized (fallingWords) { // 동시에 하나의 스레드만 fallingWords에 접근하도록 제한
			fallingWords.add(word); // 동기화하여 단어 리스트에 추가
		}
		ground.addWord(word.label); // 화면에 단어 추가
	}

	public void gameOver() { // 게임이 정지되거나 게임 오버 됐을 때 호출 되는ㄴ 게임 오버 메소드
		System.out.println("게임 오버!"); // 콘솔에 게임 오버 메시지 출력, 디버그용
	    isGameOver = true; // 게임 오버 상태로 변경
	    fThread.interrupt(); // 떨어지는 단어 처리 스레드 중지
	    ground.clearAllWords(); // 화면에 있는 단어 모두 제거
	    editPanel.recordScore(scorePanel.getScore()); // 현재 점수를 랭킹에 기록
	    JOptionPane.showMessageDialog(this, "Game Over! Press Start to Restart."); // 게임 오버 메세지 출력
	}

	class GameGroundPanel extends JPanel { // 게임이 실제로 진행될 GameGroundPanel
		public GameGroundPanel() {
			setLayout(null); // 자유 배치 설정
			defaultBackground = new ImageIcon("src/resources/default_background.png"); // 기본 배경 이미지
			slowMotionBackground = new ImageIcon("src/resources/slow_motion_background.png"); // 모래시계 효과 배경 이미지
		}

		public void clearAllWords() { // 모든 단어를 제거하는 메소드
			this.removeAll(); // 모든 단어 제거
			repaint(); // 화면 갱신
		}

		public void addWord(JLabel wordLabel) { // 단어 추가하는 메소드
			this.add(wordLabel); // GameGroundPanel에 단어 추가
			wordLabel.setVisible(true); // 단어 화면에 표시
			repaint(); // 화면 갱신
		}

		public void removeWord(JLabel wordLabel) { // 단어 삭제하는 메소드, 단어 맞췄을 때, 폭탄 효과로 제거할 때
			this.remove(wordLabel); // 단어 제거
			repaint(); // 화면 갱신
		}

		@Override
		protected void paintComponent(java.awt.Graphics g) { // 배경화면 그리기
			super.paintComponent(g); // 부모 클래스의 paintComponent호출
			ImageIcon currentBackground = isSlowMotion ? slowMotionBackground : defaultBackground; // 현재 상태에 따라 배경 이미지 설정, 모래시계 효과일 때는 sloMotionBackground로, 아닐 때는 defaultBackGround로 설정
			g.drawImage(currentBackground.getImage(), 0, 0, getWidth(), getHeight(), this); // 배경 이미지를 화면 크기에 맞게 그리기
		}
	}

	class FallingThread extends Thread { // Thread를 상속받는 단어를 떨어뜨릴 FallingThread
		@Override
		public void run() {
			while (!isGameOver) { // 게임 오버될 때 까지 반복 실행
				synchronized (fallingWords) { // 동시에 하나의 스레드만 fallingWords에 접근하도록 제한
					ArrayList<FallingWord> toRemove = new ArrayList<>(); // 제거할 단어 리스트 생성
					for (FallingWord word : fallingWords) { // 컬렉션에서 저장된 값을 하나씩 꺼내면서 반복
						word.fall(isSlowMotion); // 단어 떨어뜨리기
						if (word.position.y > ground.getHeight()) { // 단어가 바닥에 닿으면
							toRemove.add(word); // 제거할 단어 리스트에 추가
							if (word.isBomb) { // 폭탄 단어가 바닥에 닿으면 라이프 2개 감소
								scorePanel.decreaseLife(); // 라이프 감소
								scorePanel.decreaseLife(); // 라이프 감소
							} else // 일반 단어라면
								scorePanel.decreaseLife(); // 라이프 감소
							if (scorePanel.getLives() == 0) { // 생명이 0이 되면
								gameOver(); // 게임 오버 처리
								return; // 스레드 종료
							}
						}
					}
					for (FallingWord word : toRemove) { // 제거할 단어 리스트에서 값을 하나씩 꺼내면서 반복
						fallingWords.remove(word); // 리스트에서 제거
						ground.removeWord(word.label); // 화면에서 단어 제거
						newWord(); // 새로운 단어 생성
					}
				}
				try {
					Thread.sleep(isSlowMotion ? 500 : 250); // 느려진 상태에 따라 대기 시간 설정, 모래시계 효과 상태일 때는 천천히 떨어지도록
				} catch (InterruptedException e) {
					return; // 예외가 발생하면 스레드 종료
				}
			}
		}
	}

	class FallingWord { // 떨어질 단어 클래스
		JLabel label; // 단어를 표시하는 JLabel
		Point position; // 단어 위치를 담을 position
		Random random = new Random(); // radom.nextInt 사용을 위한 랜덤 객체 생성
		int monsterColor; // 몬스터의 색상을 결정할 변수 monsterColor
		boolean isHourglass; // 모래시계 단어 여부
		boolean isBomb; // 폭탄 단어 여부

		public FallingWord(String text, int x, int y, boolean isHourglass, boolean isBomb) { // 단어와 좌표, 특수 단어의 여부를 매개변수로 받음
			this.isHourglass = isHourglass; // 모래시계 여부 설정
			this.isBomb = isBomb; // 폭탄 여부 설정

			if (isBomb) { // 폭탄 단어라면
				label = new JLabel(text, new ImageIcon("src/resources/bomb.png"), JLabel.LEFT); // 폭탄 단어 이미지 설정
			} else if (isHourglass) { // 모래시계 단어라면
				label = new JLabel(text, new ImageIcon("src/resources/hourglass.png"), JLabel.LEFT); // 모래시계 이미지 설정
			} else {
				monsterColor = random.nextInt(3); // 몬스터 색상을 랜덤으로 선택
				if (monsterColor == 0) 
					label = new JLabel(text, new ImageIcon("src/resources/blackMonster.png"), JLabel.LEFT); // 검은 몬스터 이미지
				else if (monsterColor == 1)
					label = new JLabel(text, new ImageIcon("src/resources/redMonster.png"), JLabel.LEFT); // 붉은 몬스터 이미지
				else if (monsterColor == 2)
					label = new JLabel(text, new ImageIcon("src/resources/blueMonster.png"), JLabel.LEFT); // 푸른 몬스터 이미지

			}

			position = new Point(x, y); // 초기 위치 설정
			label.setSize(150, 50); // Label 사이즈 설정
			label.setForeground(Color.RED); // 단어 색 설정, 가시성을 위해 빨간색으로 설정
			label.setLocation(position); // 라벨 위치 지정
		}

		public void fall(boolean isSlowMotion) { // 단어를 떨어뜨리는 메소드
			position.y += isBomb ? fallingSpeed + 5 // 폭탄 단어는 가장 빠르게 떨어지고
					: isHourglass ? fallingSpeed + 3 // 모래시계는 살짝 빠르게
					: (isSlowMotion ? fallingSpeed / 2 : fallingSpeed); // 모래시계 상태일 경우 떨어지는 속도 반감
			label.setLocation(position); // 새로운 위치로 Label이동
		}
	}

	class InputPanel extends JPanel {
		private JTextField tf = new JTextField(10); // 단어 입력 필드
		private SoundPlayer soundPlayer = new SoundPlayer(); // 사운드를 재생할 객체 생성
		
		public InputPanel() {
			this.setBackground(Color.LIGHT_GRAY); // 배경색을 회색으로 설정
			add(tf); // 입력 필드 추가
			tf.addActionListener(new ActionListener() { // 단어 입력 이벤트 리스너 추가
				@Override
				public void actionPerformed(ActionEvent e) {
					String text = tf.getText(); // 입력된 텍스트 가져오기
					if (text.isEmpty()) // 입력이 없으면 리턴
						return;

					synchronized (fallingWords) { // 동시에 하나의 스레드만 fallingWords에 접근하도록 제한
						FallingWord matchedWord = null; // 맞춘 단어 초기화
						for (FallingWord word : fallingWords) { // 컬렉션에서 저장된 값을 하나씩 꺼내면서 반복
							if (text.equals(word.label.getText())) { // 입력된 단어가 일치하면
								matchedWord = word; // 맞춘 단어로 초기화
								break;
							}
						}
						if (matchedWord != null) { // 일치하는 단어가 발견되면
							fallingWords.remove(matchedWord); // 단어 리스트에서 제거
							final FallingWord wordToRemove = matchedWord; // final 변수 생성 익명 클래스(Timer)에서 사용하는 로컬 변수
						    wordToRemove.label.setIcon(new ImageIcon("src/resources/hit.png")); // 피격 효과 이미지
						    ground.repaint(); // 화면 갱신

						    new javax.swing.Timer(500, new ActionListener() { // 타격 효과 지속 시간 설정
						        @Override
						        public void actionPerformed(ActionEvent evt) {
						            ground.removeWord(wordToRemove.label); // 단어 제거
						            ground.repaint(); // 화면 갱신
						        }
						    }).start();
						    
	                        soundPlayer.playSound("src/resources/Gunshot-sound.wav", 0.3f, false); // 피격 사운드 재생
	                        
							if (matchedWord.isBomb) { // 폭탄 단어면
								removeNearWords(matchedWord); // 주변 단어 제거
								wordCount += 3; // 맞춘 단어 개수 3개 증가
							} else if (matchedWord.isHourglass) { // 모래시계 단어라면
								activateSlowMotion(); // 느려진 상태 연장, 연속으로 맞추면 계속 지속되도록
							} else { // 일반 단어라면
								scorePanel.increase(); // 점수 증가
								wordCount++; // 맞춘 단어 개수 증가
								if (wordCount % 20 == 0) { // 맞춘 단어 개수가 20개를 달성할 때마다 속도 증가
									if (fallingSpeed < 15) { // 최대 속도를 15로 제한
										fallingSpeed++; // 속도 증가
										System.out.println("속도 증가! 현재 속도: " + fallingSpeed); // 속도가 증가됐을 때 콘솔에 현재속도 출력, 디버깅용
									} else { // 최대 속도라면
										scorePanel.increase(); // 속도가 최대일 때 보너스 점수를 두배로 획득
										System.out.println("최대 속도 도달! 보너스 점수 추가."); // 콘솔에 출력
									}
								}
							}
							newWord(); // 새로운 단어 생성
						}
					}
					tf.setText(""); // 입력 필드 초기화
				}
			});
		}

		private void activateSlowMotion() { // 슬로우 모션이 걸린 것처럼 표현하는 메소드
			isSlowMotion = true; // 느려진 상태 활성화

			slowMotionEndTime = System.currentTimeMillis() + 3000; // 현재 시간을 기준으로 느려진 상태 종료 시점 갱신

			// 이미 실행 중인 느려진 상태를 확인하고 추가로 연장
			new Thread(new Runnable() {
			    @Override
			    public void run() { // 새로운 스레드에서 실행
			        try {
			            while (System.currentTimeMillis() < slowMotionEndTime) { // 현재 시간과 종료 시간 비교
			                Thread.sleep(100); // 100ms마다 상태 확인
			            }
			        } catch (InterruptedException e) {
			            return; // 인터럽트 시 종료
			        } finally { // 예외가 발생하더라도 복구를 위해 finally 사용
			            if (System.currentTimeMillis() >= slowMotionEndTime) { // 종료 시간 도달 여부 확인
			                isSlowMotion = false; // 느려진 상태 종료
			            }
			        }
			    }
			}).start();
		}

		private void removeNearWords(FallingWord bombWord) { // 주변 단어를 제거하는 메소드
			int removedCount = 0; // 제거된 단어 변수
			synchronized (fallingWords) { // 동시에 하나의 스레드만 fallingWords에 접근하도록 제한
				ArrayList<FallingWord> toRemove = new ArrayList<>(); // 제거할 단어 리스트 생성
				for (FallingWord word : fallingWords) { // 컬렉션에서 저장된 값을 하나씩 꺼내면서 반복
					if (removedCount >= 3) // 최대 단어 3개까지만 제거
						break;
					if (!word.isHourglass && !word.isBomb) { // 일반 단어만 제거
						toRemove.add(word); // 제거할 단어 리스트에 추가
						removedCount++; // 제거된 단어 증가
					}
				}

				// 제거된 단어를 실제로 삭제
				for (FallingWord word : toRemove) { // 컬렉션에서 저장된 값을 하나씩 꺼내면서 반복
					fallingWords.remove(word); // fallingWords 리스트에서 제거
					ground.removeWord(word.label); // 화면에서 제거
				}

				// 제거된 단어 수만큼 새로운 단어 생성
				for (int i = 0; i < removedCount; i++) { // 제거된 단어만큼 반복
					scorePanel.increase(); // 점수 증가
					newWord(); // 새로운 단어 생성
				}
			}
			System.out.println("폭탄 효과로 주변 단어 제거 완료. 새 단어 " + removedCount + "개 생성."); // 디버그용 콘솔 출력
		}
	}
}
