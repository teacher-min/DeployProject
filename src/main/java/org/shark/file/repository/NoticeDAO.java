package org.shark.file.repository;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.shark.file.model.dto.AttachDTO;
import org.shark.file.model.dto.NoticeDTO;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class NoticeDAO {

  private final SqlSessionTemplate sqlSession;
  
  /*
  private final String getMapperId() {
    return "mybatis.mapper.noticeMapper." + Thread.currentThread().getStackTrace()[2].getMethodName();
  }
  */
  
  public List<NoticeDTO> getNotices() {
    return sqlSession.selectList("mybatis.mapper.noticeMapper.getNotices");
  }
  
  public NoticeDTO getNoticeById(Integer nid) {
    return sqlSession.selectOne("mybatis.mapper.noticeMapper.getNoticeById", nid);
  }
  
  public List<AttachDTO> getAttaches(Integer nid) {
    return sqlSession.selectList("mybatis.mapper.noticeMapper.getAttaches", nid);
  }
  
  public int insertNotice(NoticeDTO notice) {
    return sqlSession.insert("mybatis.mapper.noticeMapper.insertNotice", notice);
  }
  
  public int insertAttach(AttachDTO attach) {
    return sqlSession.insert("mybatis.mapper.noticeMapper.insertAttach", attach);
  }
  
  public int deleteNoticeById(Integer nid) {
    return sqlSession.delete("mybatis.mapper.noticeMapper.deleteNoticeById", nid);
  }
  
  public AttachDTO getAttachById(Integer aid) {
    return sqlSession.selectOne("mybatis.mapper.noticeMapper.getAttachById", aid);
  }
  
  public List<AttachDTO> getAttachesByFilePath(String filePath) {
    return sqlSession.selectList("mybatis.mapper.noticeMapper.getAttachesByFilePath", filePath);
  }
  
}
