USE db_file;

# 사용자 (단일 첨부 테이블)
CREATE TABLE IF NOT EXISTS tbl_user (
  uid          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email        VARCHAR(100) NOT NULL,
  file_path         VARCHAR(300) NULL,
  original_filename VARCHAR(300) NULL,
  filesystem_name   VARCHAR(300) NULL
) ENGINE=InnoDB;

# 공지사항 테이블 (다중 첨부 테이블)
CREATE TABLE IF NOT EXISTS tbl_notice (
  nid          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title        VARCHAR(100) NOT NULL,
  content      TEXT NULL
) ENGINE=InnoDB;

# 첨부 파일 테이블
CREATE TABLE IF NOT EXISTS tbl_attach (
  aid          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nid          INT NULL,
  file_path         VARCHAR(300) NULL,
  original_filename VARCHAR(300) NULL,
  filesystem_name   VARCHAR(300) NULL,
  FOREIGN KEY (nid) REFERENCES tbl_notice(nid)
    ON DELETE CASCADE
) ENGINE=InnoDB;