package org.shark.file.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.shark.file.model.dto.UserDTO;
import org.shark.file.repository.UserDAO;
import org.shark.file.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Transactional  //----- 서비스 클래스 레벨에 설정한 @Transactional에 의해서 모든 메소드는 트랜잭션 처리가 됩니다.
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserDAO userDAO;
  private final FileUtil fileUtil;
  
  @Transactional(readOnly = true)  //----- 읽기 전용 최적화를 통해 트랜잭션 매니저의 불필요한 동작을 방지하여 성능을 향상할 수 있습니다.
  @Override
  public List<UserDTO> findAllUsers() {
    return userDAO.getAllUsers();
  }

  @Transactional(readOnly = true)  //----- 읽기 전용 최적화를 통해 트랜잭션 매니저의 불필요한 동작을 방지하여 성능을 향상할 수 있습니다.
  @Override
  public UserDTO findUserById(Integer uid) {
    return userDAO.getUserById(uid);
  }

  @Override
  public boolean signUp(UserDTO user, MultipartFile profile) {
    
    try {
      
      //----- 첨부 파일을 저장할 경로
      String filePath = fileUtil.getFilePath();
      
      //----- 디렉터리 없으면 생성하기
      Path uploadPath = Paths.get(filePath);
      if (Files.notExists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }
      
      //----- 첨부 파일의 원래 이름
      String originalFilename = profile.getOriginalFilename();
      
      //----- 첨부 파일을 서버에 저장할 때 사용하는 이름
      String filesystemName = fileUtil.getFilesystemName(originalFilename);
      
      //----- 첨부 파일을 서버에 저장
      Path target = Paths.get(filePath + "/" + filesystemName);
      Files.copy(profile.getInputStream(), target);
      
      //----- DB에 정보 저장하기
      user.setFilePath(filePath);
      user.setOriginalFilename(originalFilename);
      user.setFilesystemName(filesystemName);
      
      return userDAO.insertUser(user) == 1;  //----- 회원 가입 성공 여부
      
    } catch (Exception e) {
      e.printStackTrace();
      return false;  //----- 회원 가입 실패
    }
    
  }

}
