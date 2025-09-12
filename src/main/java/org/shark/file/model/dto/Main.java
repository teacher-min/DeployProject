package org.shark.file.model.dto;

public class Main {

  private static final String getMapperId() {
    return "mybatis.mapper.noticeMapper." + Thread.currentThread().getStackTrace()[2].getMethodName();
  }
  
  public static void main(String[] args) {
    
    
    System.out.println(getMapperId());

  }

}
