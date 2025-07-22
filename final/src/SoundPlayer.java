import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class SoundPlayer {
    private Clip clip; // 사운드 재생을 관리하는 Clip 객체

    public void playSound(String soundFilePath, float volume, boolean loop) {
        try {
            File soundFile = new File(soundFilePath); // 재생할 사운드 파일 경로
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundFile); // 오디오 입력 스트림 생성
            clip = AudioSystem.getClip(); // Clip 객체 생성
            clip.open(audioInput); // 입력 스트림으로 Clip 초기화

            // 볼륨 조절
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // 볼륨 제어용 FloatControl
            float dB = 20f * (float) Math.log10(volume); // 볼륨 값을 데시벨로 변환 (0.0~1.0 범위)
            gainControl.setValue(dB); // 볼륨 설정

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY); // 무한 반복 재생
            } else {
                clip.start(); // 한 번만 재생
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage()); // 오류 메시지 출력
        }
    }

    public void stopSound() { // 사운드 재생 중지 메서드
        if (clip != null && clip.isRunning()) { // Clip이 초기화되고 재생 중인지 확인
            clip.stop(); // 재생 중지
        }
    }
}