package com.kh.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.kh.config.FileConfig;
import com.kh.config.PageConfig;
import com.kh.config.SponsorConfig;
import com.kh.dao.AdminDAO;
import com.kh.dao.AdoptionDAO;
import com.kh.dao.FileDAO;
import com.kh.dto.AdoptionDTO;
import com.kh.dto.AnimalDTO;
import com.kh.dto.AnimalFilesDTO;
import com.kh.dto.SponsorDTO;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;



@WebServlet("*.adm")
public class AdminController extends HttpServlet {

	// 날짜가 yyyymmdd 형식으로 입력되었을 경우 Date로 변경하는 메서드
	private Date transformDate(String date){
		SimpleDateFormat beforeFormat = new SimpleDateFormat("yyyymmdd");

		// Date로 변경하기 위해서는 날짜 형식을 yyyy-mm-dd로 변경해야 한다.
		SimpleDateFormat afterFormat = new SimpleDateFormat("yyyy-mm-dd");

		java.util.Date tempDate = null;

		try {
			// 현재 yyyymmdd로된 날짜 형식으로 java.util.Date객체를 만든다.
			tempDate = beforeFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// java.util.Date를 yyyy-mm-dd 형식으로 변경하여 String로 반환한다.
		String transDate = afterFormat.format(tempDate);

		// 반환된 String 값을 Date로 변경한다.
		Date d = Date.valueOf(transDate);

		return d;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");


		String requestURI = request.getRequestURI();
		String ctxPath = request.getContextPath();
		String url = requestURI.substring(ctxPath.length());
		
		System.out.println("requestURI : " + requestURI);
		System.out.println("ctxPath : " + ctxPath);
		System.out.println("url : " + url);

		AdminDAO admindao = AdminDAO.getInstance();
		AdoptionDAO adoptdao = AdoptionDAO.getInstance();
		FileDAO fdao = FileDAO.getInstance();

		try {
			if(url.contentEquals("/adoptRegList.adm")) {
				response.setContentType("text/html;charset=utf-8");

				Gson g = new Gson();
				List<AdoptionDTO> list = admindao.getAdoptionList();

				String result = g.toJson(list);
				response.getWriter().append(result);
			}else if(url.contentEquals("/animalInfoReg.adm")) { 

				System.out.println("동물 정보 등록");

				String filesPath = request.getServletContext().getRealPath("/upload/animalInfo");

				File filesFolder = new File(filesPath);
				if(!filesFolder.exists()) filesFolder.mkdir();

				MultipartRequest multi = new MultipartRequest(request, filesPath, FileConfig.uploadmaxSize, "utf8", new DefaultFileRenamePolicy());

				String anName = multi.getParameter("anName");
				String anCategory = multi.getParameter("anCategory");
				String anGender = multi.getParameter("anGender");
				String anKind = multi.getParameter("anKind");
				int anAge = Integer.parseInt(multi.getParameter("anAge"));
				int anWeight = Integer.parseInt(multi.getParameter("anWeight"));
				String anCharacter = multi.getParameter("anCharacter");
				Date anDate = transformDate(multi.getParameter("anDate"));
				String thumbImg = multi.getFilesystemName("thumbnail");
				thumbImg = Normalizer.normalize(thumbImg, Form.NFC);
				String anContnets = multi.getParameter("anContnets");
				String anNeutering = multi.getParameter("anNeutering");
				String code_seq = admindao.getAnimalCode();

				Set<String> fileNames = multi.getFileNameSet(); 
				for(String fileName : fileNames) {
					String oriName = multi.getOriginalFileName(fileName);
					if(oriName == null) continue;
					oriName = Normalizer.normalize(oriName, Form.NFC);
					String sysName = multi.getFilesystemName(fileName);
					sysName = Normalizer.normalize(sysName, Form.NFC);

					if(!fileName.contentEquals("files") && !fileName.contentEquals("thumbnail")) {
						fdao.animalImgUpload(new AnimalFilesDTO(0, oriName, sysName, null, code_seq));
					}
				}
				AnimalDTO adto = new AnimalDTO(code_seq, anName, anCategory, anGender, anKind, anAge, anWeight, anCharacter, anDate, "N", thumbImg, anContnets, null, anNeutering);
				int result = admindao.animalInfoReg(adto);
				
				response.sendRedirect(ctxPath + "/animalInfoList.adm?cpage=1");		

			}else if(url.contentEquals("/uploadImg.adm")) {

				response.setCharacterEncoding("utf8");
				response.setContentType("text/html;charset=utf8");

				String realPath = request.getServletContext().getRealPath("upload/editor");
				File filesPath = new File(realPath);
				System.out.println(realPath);

				if(!filesPath.exists()) {filesPath.mkdir();}
				MultipartRequest multi = new MultipartRequest(request, realPath, FileConfig.uploadmaxSize, "utf-8", new DefaultFileRenamePolicy());

				String sysName = multi.getFilesystemName("file");
				sysName = Normalizer.normalize(sysName, Form.NFC);

				String returnPath = "/upload/editor/" + sysName;

				System.out.println("returnPath : " + returnPath);
				response.getWriter().append(returnPath);

			}else if(url.contentEquals("/animalInfoList.adm")) { 
				System.out.println("동물 정보 리스트");

				int cpage = Integer.parseInt(request.getParameter("cpage"));
				int endNum = cpage * PageConfig.ANIMAL_RECORD_COUNT_PER_PAGE;
				int startNum = endNum - (PageConfig.ANIMAL_RECORD_COUNT_PER_PAGE - 1);

				List<AnimalDTO> list;
				List<String> pageNavi = admindao.getPageNavi(cpage);
				int rowNum = admindao.getRecordCount();

				list = admindao.getPageList(startNum, endNum);

				request.setAttribute("list", list);
				request.setAttribute("navi", pageNavi);
				request.setAttribute("cpage", cpage);
				request.setAttribute("rowNum", rowNum);
				request.getRequestDispatcher("admin/animalInfoList.jsp").forward(request, response);

			}else if(url.contentEquals("/animalInfoDetail.adm")) { 

				String code_seq = request.getParameter("code_seq");

				AnimalDTO dto = admindao.getAnimalInfo(code_seq);
				List<AnimalFilesDTO> files = fdao.getAnimalImgs(code_seq);

				request.setAttribute("info", dto);
				request.setAttribute("files", files);

				request.getRequestDispatcher("admin/animalInfoModify.jsp").forward(request, response);

			}else if(url.contentEquals("/animalInfoModify.adm")) {
				System.out.println("동물정보수정");

				String filesPath = request.getServletContext().getRealPath("/upload/animalInfo");
				MultipartRequest multi = new MultipartRequest(request, filesPath, FileConfig.uploadmaxSize, "utf8", new DefaultFileRenamePolicy());

				String code_seq = multi.getParameter("code_seq");
				String anName = multi.getParameter("anName");
				String anCategory = multi.getParameter("anCategory");
				String anGender = multi.getParameter("anGender");
				String anKind = multi.getParameter("anKind");
				int anAge = Integer.parseInt(multi.getParameter("anAge"));
				int anWeight = Integer.parseInt(multi.getParameter("anWeight"));
				String anCharacter = multi.getParameter("anCharacter");
				Date anDate = transformDate(multi.getParameter("anDate"));
				String anStatus = multi.getParameter("anStatus");
				String thumbImg = multi.getFilesystemName("thumbnail");
				if(thumbImg != null) {thumbImg = Normalizer.normalize(thumbImg, Form.NFC);}
				String anContnets = multi.getParameter("anContnets");
				String anNeutering = multi.getParameter("anNeutering");
				
				// 파일삭제 
				String[] delTargets = multi.getParameterValues("delete");
				System.out.println("delTargets : " + delTargets);
				if(delTargets != null) {
					System.out.println("파일 삭제 ㅅㅣ작");

					System.out.println("delTargets.length : " + delTargets.length);
					for(String target :delTargets) {
						String sysName = fdao.getSysName(Integer.parseInt(target));
						File targetFile = new File(filesPath + "/"+ sysName); // 지우고자 하는 파일을 찾음
						boolean result = targetFile.delete();
						System.out.println("파일 삭제 여부 : " + result);
						if(result) { fdao.animalImgDelete(Integer.parseInt(target));}
					}
				}
				
				//파일 업로드
				Set<String> fileNames = multi.getFileNameSet(); 
				for(String fileName : fileNames) {
					String oriName = multi.getOriginalFileName(fileName);

					if(oriName == null) continue;
					oriName = Normalizer.normalize(oriName, Form.NFC);
					String sysName = multi.getFilesystemName(fileName);
					sysName = Normalizer.normalize(sysName, Form.NFC);
					
					if(!fileName.contentEquals("files") && !fileName.contentEquals("thumbnail")) {
						fdao.animalImgUpload(new AnimalFilesDTO(0, oriName, sysName, null, code_seq));
					}
				}

				AnimalDTO dto = new AnimalDTO(code_seq, anName, anCategory, anGender, anKind, anAge, anWeight, anCharacter, anDate, anStatus, thumbImg, anContnets, null, anNeutering);
				int result = admindao.animalInfoModify(dto);
				
				response.sendRedirect(ctxPath + "/animalInfoList.adm?cpage=1");
				
			}else if(url.contentEquals("/animalInfoDelete.adm")) {

				String code_seq = request.getParameter("code_seq");
				ArrayList<String> delTargets = fdao.getFileSysName(code_seq);

				String filesPath = request.getServletContext().getRealPath("/upload/animalInfo");
				if(delTargets != null) {
					for(int i=0;i<delTargets.size(); i++) {
						String sysName = delTargets.get(i);
						File targetFile = new File(filesPath + "/"+ sysName);
						boolean result = targetFile.delete();
					}
				}
				admindao.animalInfoDelete(code_seq);
				fdao.animalImgDelete(code_seq);
				
				response.sendRedirect(ctxPath + "/animalInfoList.adm?cpage=1");				

			}else if(url.contentEquals("/adSponsorList.adm")) {
				System.out.println("스폰서 후원");
				AdminDAO adao =AdminDAO.getInstance(); 
				
				int cpage = Integer.parseInt(request.getParameter("cpage"));
				int endNum = cpage * (SponsorConfig.RECORD_COUNT_PER_PAGE);
	            int startNum = endNum - (SponsorConfig.RECORD_COUNT_PER_PAGE - 1);
				List<SponsorDTO> slist = adao.adSponsorGetPageList(startNum, endNum);
				String sp_slct_cho = request.getParameter("sp_slct_cho");
				//System.out.println(sp_slct_cho);
				List<String> navi = adao.adSponsorGetPageNavi(cpage, sp_slct_cho);//
				request.setAttribute("slist", slist);//이건 모든값
				request.setAttribute("navi", navi);//아래1~10 버튼 중 필요만큼
				request.setAttribute("sp_cho", sp_slct_cho);
				
				request.getRequestDispatcher("admin/adSponsorList.jsp?cpage=1").forward(request, response);
				
			}else if(url.contentEquals("/spAdminSearch.adm")) {
				System.out.println("카테고리");
				AdminDAO adao =AdminDAO.getInstance(); 
				
				int cpage = Integer.parseInt(request.getParameter("cpage"));
				int endNum = cpage * (SponsorConfig.RECORD_COUNT_PER_PAGE);
	            int startNum = endNum - (SponsorConfig.RECORD_COUNT_PER_PAGE - 1);
				String sp_search = request.getParameter("sp_slct_cho");
				System.out.println(sp_search);
//				if(sp_search.contentEquals("all")) {
//					response.sendRedirect("adSponsorList.adm?cpage=1&sp_slct_cho=all");
//				}else {
					List<SponsorDTO> slist = adao.adSponsorGetPageList(startNum, endNum, sp_search);
					//List<SponsorDTO> slist = adao.adminSponsorSearch(sp_search);
					List<String> navi = adao.adSponsorGetPageNavi(cpage, sp_search);
					
					request.setAttribute("slist", slist);
					request.setAttribute("navi", navi);//아래1~10 버튼 중 필요만큼
					request.setAttribute("sp_cho", sp_search);
					System.out.println(sp_search);
					request.getRequestDispatcher("admin/adSponsorList.jsp?cpage=1").forward(request, response);
				//}
				
			}

		}catch(Exception e) {
			e.printStackTrace();
			response.sendRedirect("error.jsp");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
