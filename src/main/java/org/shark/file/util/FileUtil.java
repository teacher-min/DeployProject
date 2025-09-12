package org.shark.file.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component  //----- Spring Container에 FileUtil 타입의 빈을 등록합니다.
public class FileUtil {

  private final LocalDate today = LocalDate.now();
  
  /**
   * 파일 업로드 경로를 반환하는 메소드
   * @return 현재 날짜를 경로로 사용할 수 있도록 슬래시(/)로 구분한 문자열을 반환합니다.
   *         예를 들어 2000-01-01 날짜에 메소드를 호출하면 "/upload/2000/01/01" 값을 반환합니다.
   */
  public String getFilePath() {
    return "/home/goodee/upload" + DateTimeFormatter.ofPattern("/yyyy/MM/dd").format(today);
  }
  
  /**
   * 서버에 저장할 파일의 이름을 반환하는 메소드
   * @param 파일의 원래 이름
   * @return 저장할 파일의 이름. 중복 방지를 위해서 난수 처리된 이름을 사용(UUID). 파일의 원래 확장자를 그대로 사용.
   */
  public String getFilesystemName(String originalFilename) {
    String extensionName = "";
    if(originalFilename.endsWith(".tar.gz"))
      extensionName = ".tar.gz";
    else
      extensionName = originalFilename.substring(originalFilename.lastIndexOf("."));
    return UUID.randomUUID().toString() + extensionName;
  }
  
}
