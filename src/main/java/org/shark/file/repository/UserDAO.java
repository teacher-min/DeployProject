package org.shark.file.repository;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.shark.file.model.dto.UserDTO;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class UserDAO {

  private final SqlSessionTemplate sqlSession;
  
  public List<UserDTO> getAllUsers() {
    return sqlSession.selectList("mybatis.mapper.userMapper.getAllUsers");
  }
  
  public UserDTO getUserById(Integer uid) {
    return sqlSession.selectOne("mybatis.mapper.userMapper.getUserById", uid);
  }
  
  public int insertUser(UserDTO user) {
    return sqlSession.insert("mybatis.mapper.userMapper.insertUser", user);
  }
  
}
