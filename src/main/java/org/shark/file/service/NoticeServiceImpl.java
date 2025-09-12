package org.shark.file.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.shark.file.model.dto.AttachDTO;
import org.shark.file.model.dto.NoticeDTO;
import org.shark.file.repository.NoticeDAO;
import org.shark.file.util.FileUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Transactional  //----- 서비스 클래스 레벨에 설정한 @Transactional에 의해서 모든 메소드는 트랜잭션 처리가 됩니다.
@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

  private final NoticeDAO noticeDAO;
  private final FileUtil fileUtil;
  
  @Transactional(readOnly = true)  //----- 읽기 전용 최적화를 통해 트랜잭션 매니저의 불필요한 동작을 방지하여 성능을 향상할 수 있습니다.
  @Override
  public List<NoticeDTO> findNotices() {
    return noticeDAO.getNotices();
  }

  @Transactional(readOnly = true)  //----- 읽기 전용 최적화를 통해 트랜잭션 매니저의 불필요한 동작을 방지하여 성능을 향상할 수 있습니다.
  @Override
  public Map<String, Object> findNoticeById(Integer nid) {
    return Map.of("notice", noticeDAO.getNoticeById(nid)
                , "attaches", noticeDAO.getAttaches(nid));
  }
  
  @Override
  public boolean addNotice(NoticeDTO notice, List<MultipartFile> files) throws Exception {

    //----- 공지사항 DB에 등록하기
    System.out.println("공지사항 등록 이전 nid : " + notice.getNid());  //----- null
    int addedNoticeCount = noticeDAO.insertNotice(notice);  //------------- INSERT 수행하면서 파라미터 notice의 nid 필드에 자동 생성된 PK 값이 저장됩니다.
    System.out.println("공지사항 등록 이후 nid : " + notice.getNid());  //----- 자동 생성된 PK 값
    if (addedNoticeCount != 1) {
      throw new RuntimeException("공지사항 DB 등록 실패");
    }
    
    //----- 첨부파일 없으면 곧바로 공지사항 등록 성공
    if (files == null || files.isEmpty()) {
      return true;
    }
    
    //----- 첨부파일 서버에 저장하기
    try {
      
      for (MultipartFile file : files) {
        
        //***** 비어 있는 파일이면 다음 첨부로 넘어가기
        if (file.isEmpty())
          continue;
        
        //***** 첨부 파일을 저장할 경로(디렉터리)
        String filePath = fileUtil.getFilePath();
        
        //***** 디렉터리 없으면 생성하기
        Path uploadPath = Paths.get(filePath);
        if (Files.notExists(uploadPath)) {
          Files.createDirectories(uploadPath);
        }
        
        //***** 첨부 파일의 원래 이름
        String originalFilename = file.getOriginalFilename();
        
        //***** 첨부 파일을 서버에 저장할 때 사용하는 이릅
        String filesystemName = fileUtil.getFilesystemName(originalFilename);
        
        //***** 첨부 파일을 서버에 저장
        Path targetLocation = Paths.get(filePath + "/" + filesystemName);
        file.transferTo(targetLocation);
        
        //***** 첨부파일 정보 DB에 등록하기
        AttachDTO attach = AttachDTO.builder()
            .nid(notice.getNid())
            .filePath(filePath)
            .originalFilename(originalFilename)
            .filesystemName(filesystemName)
            .build();
        
        //***** DB 예외 발생 시 clean 처리를 위해 추가 try - catch
        try {
          int addedAttachResult = noticeDAO.insertAttach(attach);
          if (addedAttachResult == 0) {
            // DB 저장 실패(0 반환)
            Files.deleteIfExists(targetLocation);
            throw new RuntimeException("첨부파일 DB 등록 실패");
          }
        } catch (Exception e) {
          // DB 저장 실패(예외 발생)
          Files.deleteIfExists(targetLocation);
          throw e;
        }
        
      }
      
    } catch (Exception e) {
      
      //----- 파일 저장이나, DB 저장 실패 시 예외를 던져 트랜잭션 롤백
      throw new RuntimeException("첨부파일 저장 실패", e);
      
    }
    
    //----- 성공
    return true;
    
  }

  @Override
  public boolean deleteNotice(Integer nid) {
    //----- 삭제하려는 공지사항에 등록된 첨부 목록 가져오기
    List<AttachDTO> attaches = noticeDAO.getAttaches(nid);
    //----- 첨부 목록 참조해서 서버에 저장된 첨부 파일들을 삭제하기
    attaches.stream()
        .map(attach -> new File(attach.getFilePath(), attach.getFilesystemName()))
        .filter(file -> file.exists())  //----- 메소드 참조 방식의 코드 .filter(File::exists)
        .forEach(file -> {
          boolean deleted = file.delete();
          if (!deleted) {
            System.out.println("===== 파일 삭제 실패 : " + file.getPath());
          }
        });
    /*
    for (AttachDTO attach : attaches) {
      String path = attach.getFilePath() + "/" + attach.getFilesystemName();
      File file = new File(path);
      if (file.exists()) {
        boolean deleted = file.delete();
        if (!deleted) {
          System.out.println("===== 파일 삭제 실패 : " + path);
        }
      }
    }
    */
    //----- DB에서 공지사항 삭제하기 (ON DELETE CASCADE에 의해서 첨부 목록은 함께 삭제됩니다.)
    return noticeDAO.deleteNoticeById(nid) == 1;
  }

  @Transactional(readOnly = true)  //----- 읽기 전용 최적화를 통해 트랜잭션 매니저의 불필요한 동작을 방지하여 성능을 향상할 수 있습니다.
  @Override
  public AttachDTO findAttachById(Integer aid) {
    return noticeDAO.getAttachById(aid);
  }
  
  @Override
  public Resource loadAttachAsResource(AttachDTO attach) {
    return new FileSystemResource(attach.getFilePath() + "/" + attach.getFilesystemName());
  }
  
}
