package org.shark.file.controller;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.shark.file.model.dto.AttachDTO;
import org.shark.file.model.dto.NoticeDTO;
import org.shark.file.service.NoticeService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@RequestMapping("/notice")
@RequiredArgsConstructor
@Controller
public class NoticeController {

  private final NoticeService noticeService;
  
  @GetMapping("/list")
  public String list(Model model) {
    model.addAttribute("notices", noticeService.findNotices());
    return "notice/list";
  }
  
  @GetMapping("/write")
  public String writeForm() {
    return "notice/write";
  }
  
  @PostMapping("/write")
  public String write(NoticeDTO notice  //------------------------------------ title, content 정보를 받습니다.
                    , @RequestParam("files") List<MultipartFile> files  //---- <input type="file" multiple> 양식을 받을 때는 List나 배열을 사용합니다.
                    , RedirectAttributes redirectAttr
                    , Model model) {
    try {
      
      boolean result = noticeService.addNotice(notice, files);
      redirectAttr.addFlashAttribute("msg", result ? "공지사항 등록 성공" : "공지사항 등록 실패");
      return "redirect:/notice/list";
      
    } catch (Exception e) {
      
      e.printStackTrace();
      model.addAttribute("error", "공지사항 등록 중 오류가 발생했습니다.");
      return "notice/write";
      
    }
  }
  
  @GetMapping("/detail")
  public String detail(Integer nid
                     , Model model) {
    Map<String, Object> map = noticeService.findNoticeById(nid);
    model.addAttribute("notice", map.get("notice"));
    model.addAttribute("attaches", map.get("attaches"));
    return "notice/detail";
  }
  
  @GetMapping("/remove")
  public String remove(Integer nid
                     , RedirectAttributes redirectAttr) {
    boolean result = noticeService.deleteNotice(nid);
    redirectAttr.addFlashAttribute("msg", result ? "공지사항 삭제 성공" : "공시사항 삭제 실패");
    return "redirect:/notice/list";
  }
  
  //----- @ResponseBody가 없어도 응답 본문을 사용하는 ResponseEntity 클래스 사용
  @GetMapping(value = "/download", produces = "application/octet-stream")
  public ResponseEntity<Resource> download(Integer aid) throws Exception {
    //----- 다운로드 할 파일의 정보 확인
    AttachDTO foundAttach = noticeService.findAttachById(aid);
    if (foundAttach == null) {
      return ResponseEntity.notFound().build();
    }
    //----- 다운로드 할 파일을 Resource 타입으로 만듬
    Resource resource = noticeService.loadAttachAsResource(foundAttach);
    if (!resource.exists() || !resource.isFile()) {
      return ResponseEntity.notFound().build();
    }
    //----- 사용자들이 다운로드 받는 파일의 이름은 원본 이름입니다.
    //      원본 파일명은 UTF-8 인코딩이 반드시 필요합니다.
    String encodedFilename = URLEncoder.encode(foundAttach.getOriginalFilename(), "UTF-8")
                                       .replaceAll("\\+", "%20");
    //----- 다운로드 시 응답 헤더(Content-Disposition) 설정
    String contentDisposition = "attachment; filename=\"" + encodedFilename + "\"";
    //----- 응답 (다운로드)
    return ResponseEntity.ok()
              .header("Content-Disposition", contentDisposition)
              .body(resource);
  }
  
}
