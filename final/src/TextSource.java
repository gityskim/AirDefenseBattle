import java.io.BufferedReader; // 자바 8장 버퍼 입출력
import java.io.FileReader; // 자바 8장 파일 입력 스트림 생성
import java.io.IOException; // 입출력 예외 처리를 위한 IOException 클래스 import
import java.util.Vector; // 동적 배열을 사용하기위한 Vector 클래스 import

public class TextSource { // 단어 관리하는 클래스
    private Vector<String> v = new Vector<String>(); // 단어 저장할 Vector 생성

    public TextSource() {
        loadWordsFile("src/resources/words.txt"); // 상대 경로로 파일 읽기
    }

    public String get() { // 단어를 랜덤으로 반환하는 메소드
        int index = (int)(Math.random() * v.size()); // Vector size 범위 내의 랜덤 인덱스 
        return v.get(index); // 랜덤 인덱스의 단어 반환
    }

    public void add(String word) { // 새로운 단어를 추가하는 메소드
        v.add(word);
    }

    private void loadWordsFile(String filePath) { // 파일에서 단어를 읽어 Vector에 추가하는 메소드
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) { // 파일 입력 스트림 생성, 문자 버퍼 스트림 생성
            String line; // 파일의 각 줄을 저장할 변수 line
            while ((line = br.readLine()) != null) { // 파일 한 줄씩 읽기
                v.add(line.trim()); // 줄바꿈 제거 후 추가
            }
        } catch (IOException e) { // 파일 입출력 예외 처리
            System.err.println("loadWordsFile 메소드 : 파일을 읽어오는 데 실패했습니다. " + e.getMessage()); // 오류 메시지 출력
        }
    }
}
