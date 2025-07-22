import java.io.*; // 파일 입출력을 위해 필요한 패키지
import java.util.ArrayList; // 동적 배열 구현을 위한 클래스
import java.util.Collections; // 정렬을 위한 유틸리티 클래스
import java.util.List; // 리스트 자료구조 인터페이스

public class ScoreManager { 
    private static final List<Integer> scores = new ArrayList<>(); // 점수를 저장하는 리스트
    private static final String SCORE_FILE = "src/resources/scores.txt"; // 점수를 저장할 파일 경로
    public static void addScore(int score) { // 점수를 리스트에 추가하고 파일에 저장
        scores.add(score); // 리스트에 점수 추가
        Collections.sort(scores, Collections.reverseOrder()); // 리스트를 내림차순으로 정렬
        saveScores(); // 정렬된 상태로 파일에 저장
    }
    
    public static List<Integer> getTopScores(int limit) { // 상위 N개의 점수를 반환
        // 리스트의 크기와 요청한 limit 중 작은 값을 기준으로 반환
        return scores.subList(0, Math.min(limit, scores.size())); 
    }

    private static void saveScores() { // 점수를 파일에 저장
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) { // 파일 쓰기를 위해 BufferedWriter를 사용
            for (int score : scores) { // 리스트의 모든 점수를 파일에 기록
                writer.write(score + "\n"); // 점수를 한 줄씩 기록
            }
        } catch (IOException e) { // 파일 쓰기 중 예외 처리
            System.err.println("점수 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public static void loadScores() { // 점수를 파일에서 읽어 리스트에 로드
        scores.clear(); // 기존 점수를 모두 제거
        File file = new File(SCORE_FILE); // 점수 파일 객체 생성

        if (!file.exists()) return; // 파일이 존재하지 않으면 종료

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) { // 파일 읽기를 위해 BufferedReader 사용
            String line; // 파일의 각 줄을 저장할 문자열
            while ((line = reader.readLine()) != null) { 
                scores.add(Integer.parseInt(line.trim())); // 읽은 문자열을 정수로 변환하여 리스트에 추가
            }
            Collections.sort(scores, Collections.reverseOrder()); // 점수를 내림차순으로 정렬
        } catch (IOException | NumberFormatException e) { // 파일 읽기 및 파싱 중 예외 처리
            System.err.println("점수 불러오기 중 오류 발생: " + e.getMessage());
        }
    }
}
