package org.shark.file.service.task;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.shark.file.model.dto.AttachDTO;
import org.shark.file.repository.NoticeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 매일 1회(새벽 3시), "어제" 날짜의 폴더(/upload/yyyy/MM/dd)에 저장된 파일들 중에서
 * 데이터베이스 테이블에 정보가 없는 첨부파일들을 찾아서 자동으로 삭제하는 스케쥴러.
 */

@Service  //----- @Component 등록도 가능합니다.
public class FileCleanupScheduler {

  @Autowired
  private NoticeDAO noticeDAO;
  
  /*
   * 크론식 (Cron Expression)
   * 
   * 1. 스케쥴러에서 특정 작업이 실행되는 시점을 패턴으로 지정하는 문자열 형식입니다.
   * 2. 스프링의 경우 최대 7개의 필드로 구성됩니다.
   * 3. 형식
   *    초      분      시      일      월      요일      (년도)
   *    0~59    0~59    0~23    1~31    1~12    0~7       (생략가능)
   *                                    JAN~DEC SUN~SAT
   * 4. 주요 특수문자
   *   ----------+-------------------------------------------------------------
   *    특수문자 | 의미
   *   ----------+-------------------------------------------------------------
   *      *      | 모든 값(매번)
   *      /      | 간격 지정
   *      ,      | 여러 값
   *      -      | 범위 지정
   *      ?      | 값 없음 ("일"과 "요일"을 동시에 지정할 수 없을 때 사용)
   *      W      | 가장 가까운 평일
   *      #      | n번째 요일
   *   ----------+-------------------------------------------------------------
   * 5. 예시
   *   --------------------+---------------------------------------------------
   *    크론식             | 의미
   *   --------------------+---------------------------------------------------
   *    0 0 2 * * *        | 매일 02:00시 실행
   *    0 10 * * * ?       | 매 시각 10분에 실행
   *    0 0 3-5 * * *      | 매일 03:00, 04:00, 05:00시 실행
   *    0 0 3,5 * * *      | 매일 03:00, 05:00시 실행
   *    0 0/30 3-5 * * *   | 매일 03:00, 03:30, 04:00, 04:30, 05:00, 05:30 실행
   *    0 45 7 ? * MON-FRI | 평일 07:45 실행
   *   --------------------+---------------------------------------------------
   */
  
  @Scheduled(cron = "0 0 3 * * *")  //----- 매일 새벽 3시를 의미하는 "크론식"을 작성합니다.
                                    //      스케쥴러 동작 허용을 위한 @EnableScheduling 어노테이션(Java Config 파일에 작성) 또는 <task:annotation-driven /> 태그가 필요합니다.
  public void cleanupUnusedFiles() {
    
    //----- 1. 어제 날짜의 폴더 경로 계산하기
    LocalDate yesterday = LocalDate.now().minusDays(1);
    String dirPath = "/home/goodee/upload" + yesterday.format(DateTimeFormatter.ofPattern("/yyyy/MM/dd"));
    
    //----- 2. 해당 폴더의 파일 목록 조회하기
    File dir = new File(dirPath);
    if (!dir.exists() || !dir.isDirectory()) {
      return;
    }
    File[] files = dir.listFiles();
    
    //----- 3. DB에서 해당 경로를 가진 첨부파일 목록 조회하기
    List<AttachDTO> attaches = noticeDAO.getAttachesByFilePath(dirPath);
    List<String> dbFilenames = attaches.stream()
      .map(attach -> attach.getFilesystemName())
      .collect(Collectors.toList());
      
    //----- 4. 비교 및 삭제하기 (삭제된 파일의 로그 남기기)
    Arrays.stream(files)
      .filter(file -> !dbFilenames.contains(file.getName()))
      .forEach(file -> {
        System.out.println(file.getPath() + "파일이 스케쥴러에 의해 삭제되었습니다.");
        file.delete();
      });
    
  }
  
}
