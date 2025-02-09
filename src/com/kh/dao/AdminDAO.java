package com.kh.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.kh.config.PageConfig;
import com.kh.dto.AdoptionDTO;
import com.kh.dto.AnimalDTO;
import com.kh.dto.SponsorDTO;


public class AdminDAO {

	private static AdminDAO instance;

	public synchronized static AdminDAO getInstance() {
		if(instance == null) {
			instance = new AdminDAO();
		}
		return instance;
	}

	private AdminDAO() {}

	private Connection getConnection() throws Exception{
		Context ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/oracle");
		return ds.getConnection();
	}

	// adoption
	public List<AdoptionDTO> getAdoptionList() throws Exception{
		String sql = "select * from adoption";

		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();
				){
			List<AdoptionDTO> list = new ArrayList<>();
			while(rs.next()) {
				AdoptionDTO dto = new AdoptionDTO();

				dto.setP_name(rs.getNString("p_name"));
				dto.setReg_date(rs.getDate("reg_date"));
				dto.setCode_seq(rs.getNString("code_seq"));
				dto.setAdopt_seq(rs.getInt("adopt_seq"));
				dto.setQ01_aname(rs.getNString("q01_aname"));
				dto.setUser_id(rs.getNString("user_id"));

				list.add(dto);
			}
			return list;
		}
	}

	// adoption
	public String getAnimalCode() throws Exception{
		String sql = "select 'AM'|| LPAD(code_seq.nextval,5,0) from dual";

		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();
				){
			rs.next();
			return rs.getString(1);

		}
	}

	// adoption
	public int animalInfoReg(AnimalDTO dto) throws Exception{
		String sql = "insert into animal values(?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, sysdate, ?)";

		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			pstat.setString(1, dto.getCode_seq());
			pstat.setString(2, dto.getAn_name());
			pstat.setString(3, dto.getAn_category());
			pstat.setString(4, dto.getAn_gender());
			pstat.setString(5, dto.getAn_kind());
			pstat.setInt(6, dto.getAn_age());
			pstat.setInt(7, dto.getAn_weight());
			pstat.setString(8, dto.getAn_character());
			pstat.setDate(9, dto.getAn_date());
			pstat.setString(10, dto.getAn_status());
			pstat.setString(11, dto.getAn_photo());
			pstat.setString(12, dto.getAn_contents());
			pstat.setString(13, dto.getAn_neutering());

			int result = pstat.executeUpdate();
			return result;
		}
	}

	// adoption
	public int getRecordCount() throws Exception{

		String sql = "select count(*) from animal";
		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();
				){
			rs.next();
			return rs.getInt(1);
		}

	}

	// adoption
	public List<String> getPageNavi(int currentPage) throws Exception{ 
		int recordTotalCount = this.getRecordCount(); 
		int recordCountPerPage = PageConfig.ANIMAL_RECORD_COUNT_PER_PAGE; 
		int naviCountPerPage = PageConfig.ADOPT_NAVI_COUNT_PER_PAGE; 

		int pageTotalCount = 0; 

		if((recordTotalCount % recordCountPerPage) > 0) { 
			pageTotalCount =recordTotalCount / recordCountPerPage + 1; 
		}else {
			pageTotalCount = recordTotalCount / recordCountPerPage; 
		}

		if(currentPage > pageTotalCount) {
			currentPage = pageTotalCount;
		}else if(currentPage < 1) {
			currentPage = 1;
		}

		int startNavi = (currentPage-1) / naviCountPerPage * naviCountPerPage + 1;
		int endNavi = startNavi + naviCountPerPage - 1; 

		if(endNavi > pageTotalCount) {endNavi = pageTotalCount;} 

		boolean needPrev = true; 
		boolean needNext = true; 

		if(startNavi == 1) {needPrev = false;} 
		if(endNavi == pageTotalCount) {needNext = false;}


		List<String> pageNavi = new ArrayList<String>(); 
		if(needPrev) {pageNavi.add("<");}
		for(int i = startNavi; i<= endNavi; i++) {			
			pageNavi.add(String.valueOf(i));
		}
		if(needNext) {pageNavi.add(">");}

		return pageNavi;
	}

	public List<AnimalDTO> getPageList(int startNum, int endNum) throws Exception{
		String sql = "select * from (select row_number() over(order by code_seq desc) row_number, code_seq, an_name, an_category, an_kind, an_status, an_date from animal) where row_number between ? and ?";

		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setInt(1, startNum);
			pstat.setInt(2, endNum);

			try(
					ResultSet rs = pstat.executeQuery();
					){
				List<AnimalDTO> list = new ArrayList<>();
				while(rs.next()) {
					AnimalDTO dto = new AnimalDTO();

					dto.setCode_seq(rs.getNString("code_seq"));
					dto.setAn_name(rs.getNString("an_name"));
					dto.setAn_category(rs.getNString("an_category"));
					dto.setAn_kind(rs.getNString("an_kind"));
					dto.setAn_status(rs.getNString("an_status"));
					dto.setAn_date(rs.getDate("an_date"));

					list.add(dto);
				}
				return list;
			}
		}
	}



	// adoption
	public AnimalDTO getAnimalInfo(String code_seq) throws Exception{

		String sql = "select * from animal where code_seq = ?";
		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			pstat.setString(1, code_seq);
			try(
					ResultSet rs = pstat.executeQuery();
					){
				if(rs.next()) {
					AnimalDTO dto = new AnimalDTO();
					dto.setCode_seq(code_seq);
					dto.setAn_name(rs.getNString("an_name"));
					dto.setAn_category(rs.getNString("an_category"));
					dto.setAn_gender(rs.getNString("an_gender"));
					dto.setAn_kind(rs.getNString("an_kind"));
					dto.setAn_age(rs.getInt("an_age"));
					dto.setAn_weight(rs.getInt("an_weight"));
					dto.setAn_character(rs.getNString("an_character"));
					dto.setAn_date(rs.getDate("an_date"));
					dto.setAn_status(rs.getNString("an_status"));
					dto.setAn_photo(rs.getNString("an_photo"));
					dto.setAn_contents(rs.getNString("an_contents"));
					dto.setAn_reg_date(rs.getDate("an_reg_date"));
					dto.setAn_neutering(rs.getNString("an_neutering"));
					return dto;
				}

				return null;

			}

		}

	}
	//sponsor
	public List<SponsorDTO> adSponsorSelectAll() throws Exception {
		String sql = "select * from sponsor order by 1 desc";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();){

			List<SponsorDTO> list = new ArrayList<>();
			while(rs.next()) {
				int sponsor_seq = rs.getInt("sponsor_seq");
				int sponsor_amount = rs.getInt("sponsor_amount");
				String sponsor_choice = rs.getNString("sponsor_choice");
				String sponsor_agecheck = rs.getNString("sponsor_agecheck");
				String sponsor_name = rs.getNString("sponsor_name");
				String sponsor_contact = rs.getNString("sponsor_contact");
				String sponsor_birth = rs.getNString("sponsor_birth");
				String sponsor_yname = rs.getNString("sponsor_yname");
				String sponsor_ybirth = rs.getNString("sponsor_ybirth");
				String sponsor_email = rs.getNString("sponsor_email");
				String sponsor_postcode = rs.getNString("sponsor_postcode");
				String sponsor_address1 = rs.getNString("sponsor_address1");
				String sponsor_address2 = rs.getNString("sponsor_address2");
				String sponsor_apply_num = rs.getNString("sponsor_apply_num");
				String sponsor_terms01 = rs.getNString("sponsor_terms01");
				String sponsor_terms02 = rs.getNString("sponsor_terms02");
				String sponsor_mb_id = rs.getNString("sponsor_mb_id");
				Date sponsor_date = rs.getDate("sponsor_date");

				SponsorDTO dto = new SponsorDTO(sponsor_seq, sponsor_amount, sponsor_choice,  sponsor_agecheck,
						sponsor_name,  sponsor_contact,  sponsor_birth,  sponsor_yname,
						sponsor_ybirth,  sponsor_email,  sponsor_postcode,  sponsor_address1,
						sponsor_address2,  sponsor_apply_num,  sponsor_terms01,  sponsor_terms02,
						sponsor_mb_id,  sponsor_date);
				list.add(dto);
			}
			return list;
		}
	}

	//sponsor
	public List<SponsorDTO> adminSponsorSearch(String sp_search) throws Exception {
		String sql = "select * from sponsor where sponsor_choice like ? order by 1 desc";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setNString(1, sp_search+"%");

			try(ResultSet rs = pstat.executeQuery();){

				List<SponsorDTO> list = new ArrayList<>();
				while(rs.next()) {
					int sponsor_seq = rs.getInt("sponsor_seq");
					int sponsor_amount = rs.getInt("sponsor_amount");
					String sponsor_choice = rs.getNString("sponsor_choice");
					String sponsor_agecheck = rs.getNString("sponsor_agecheck");
					String sponsor_name = rs.getNString("sponsor_name");
					String sponsor_contact = rs.getNString("sponsor_contact");
					String sponsor_birth = rs.getNString("sponsor_birth");
					String sponsor_yname = rs.getNString("sponsor_yname");
					String sponsor_ybirth = rs.getNString("sponsor_ybirth");
					String sponsor_email = rs.getNString("sponsor_email");
					String sponsor_postcode = rs.getNString("sponsor_postcode");
					String sponsor_address1 = rs.getNString("sponsor_address1");
					String sponsor_address2 = rs.getNString("sponsor_address2");
					String sponsor_apply_num = rs.getNString("sponsor_apply_num");
					String sponsor_terms01 = rs.getNString("sponsor_terms01");
					String sponsor_terms02 = rs.getNString("sponsor_terms02");
					String sponsor_mb_id = rs.getNString("sponsor_mb_id");
					Date sponsor_date = rs.getDate("sponsor_date");

					SponsorDTO dto = new SponsorDTO(sponsor_seq, sponsor_amount, sponsor_choice,  sponsor_agecheck,
							sponsor_name,  sponsor_contact,  sponsor_birth,  sponsor_yname,
							sponsor_ybirth,  sponsor_email,  sponsor_postcode,  sponsor_address1,
							sponsor_address2,  sponsor_apply_num,  sponsor_terms01,  sponsor_terms02,
							sponsor_mb_id,  sponsor_date);
					list.add(dto);
				}
				return list;
			}
		}
	}
	//sponsor  전체글 갯수
	private int adSponsorGetRecordCount() throws Exception{
		String sql = "select count(*) from sponsor";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				ResultSet rs = pstat.executeQuery();){
			rs.next();
			return rs.getInt(1);
		}
	}
	//sponsor 검색했을때 검색글의갯수
	private int adSponsorGetRecordCount(String keyword) throws Exception{
		String sql = "select count(*) from sponsor where sponsor_choice like ?";
		try(Connection con = this.getConnection();

				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setNString(1, keyword+"%");
			try(ResultSet rs = pstat.executeQuery();){
				rs.next();
				return rs.getInt(1);
			}
		}
	}
	//sponsor 1~10 버튼만드는거
	public List<String> adSponsorGetPageNavi(int cpage, String keyword) throws Exception{
		int recordTotalCount = 0;

		//search==null||search2==null||keyword == null
		if(keyword.contentEquals("all")) {
			recordTotalCount=this.adSponsorGetRecordCount();//널 전체 
		}else if(keyword!=null) {
			recordTotalCount=this.adSponsorGetRecordCount(keyword);
		}

		int recordCountPerPage = 10;
		int naviCountPerPage = 10;

		int pageTotalCount = 0;

		if(recordTotalCount % recordCountPerPage > 0) {  
			pageTotalCount = recordTotalCount / recordCountPerPage + 1; 
		}else {
			pageTotalCount = recordTotalCount / recordCountPerPage;
		}

		if(cpage > pageTotalCount) { 
			cpage = pageTotalCount; 

		}else if(cpage < 1) {
			cpage = 1;
		}

		int startNavi = (cpage-1) / naviCountPerPage * naviCountPerPage + 1;

		int endNavi = startNavi + (naviCountPerPage - 1);
		if(endNavi > pageTotalCount ) { 
			endNavi = pageTotalCount;
		}

		boolean needPrev = true;
		boolean needNext = true;

		if(startNavi == 1) {needPrev = false;}
		if(endNavi == pageTotalCount) {needNext = false;}

		List<String> pageNavi = new ArrayList<>();

		if(needPrev) {pageNavi.add("<");}

		for(int i = startNavi; i <= endNavi; i ++) { 
			pageNavi.add(String.valueOf(i));
		}

		if(needNext) {pageNavi.add(">");}

		return pageNavi;
	}
	//sponsor 검색했을때 한화면에 보일 데이터 10개의글 
	public List<SponsorDTO> adSponsorGetPageList(int startNum, int endNum, String keyword) throws Exception {
		String sql =  "select * from (select row_number() over (order by sponsor_seq desc) "
				+ "rnum,sponsor_seq,sponsor_amount,sponsor_choice, sponsor_agecheck, sponsor_name, sponsor_contact, "
				+ "sponsor_birth, sponsor_yname, sponsor.SPONSOR_YBIRTH, sponsor_email, sponsor_postcode, sponsor_address1, sponsor_address2, "
				+ "sponsor_apply_num, sponsor_terms01, sponsor_terms02 , sponsor_mb_id, sponsor_date "
				+ "from sponsor where sponsor_choice like ?) where rnum between ? and ?";
		try(Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);){
			pstat.setNString(1, "%"+keyword+"%");
			pstat.setInt(2, startNum);
			pstat.setInt(3, endNum);
			try(ResultSet rs = pstat.executeQuery();){

				List<SponsorDTO> list = new ArrayList<>();
				while(rs.next()) {
					int sponsor_seq = rs.getInt("sponsor_seq");
					int sponsor_amount = rs.getInt("sponsor_amount");
					String sponsor_choice = rs.getNString("sponsor_choice");
					String sponsor_agecheck = rs.getNString("sponsor_agecheck");
					String sponsor_name = rs.getNString("sponsor_name");
					String sponsor_contact = rs.getNString("sponsor_contact");
					String sponsor_birth = rs.getNString("sponsor_birth");
					String sponsor_yname = rs.getNString("sponsor_yname");
					String sponsor_ybirth = rs.getNString("sponsor_ybirth");
					String sponsor_email = rs.getNString("sponsor_email");
					String sponsor_postcode = rs.getNString("sponsor_postcode");
					String sponsor_address1 = rs.getNString("sponsor_address1");
					String sponsor_address2 = rs.getNString("sponsor_address2");
					String sponsor_apply_num = rs.getNString("sponsor_apply_num");
					String sponsor_terms01 = rs.getNString("sponsor_terms01");
					String sponsor_terms02 = rs.getNString("sponsor_terms02");
					String sponsor_mb_id = rs.getNString("sponsor_mb_id");
					Date sponsor_date = rs.getDate("sponsor_date");

					SponsorDTO dto = new SponsorDTO(sponsor_seq, sponsor_amount, sponsor_choice,  sponsor_agecheck,
							sponsor_name,  sponsor_contact,  sponsor_birth,  sponsor_yname,
							sponsor_ybirth,  sponsor_email,  sponsor_postcode,  sponsor_address1,
							sponsor_address2,  sponsor_apply_num,  sponsor_terms01,  sponsor_terms02,
							sponsor_mb_id,  sponsor_date);
					list.add(dto);
				}
				return list;
			}
		}
	}

	//sponsor 한화면에 보일 데이터 10개의글 
		public List<SponsorDTO> adSponsorGetPageList(int startNum, int endNum) throws Exception {
			String sql =  "select * from (select row_number() over (order by sponsor_seq desc) "
					+ "rnum,sponsor_seq,sponsor_amount,sponsor_choice, sponsor_agecheck, sponsor_name, sponsor_contact, "
					+ "sponsor_birth, sponsor_yname, sponsor.SPONSOR_YBIRTH, sponsor_email, sponsor_postcode, sponsor_address1, sponsor_address2, "
					+ "sponsor_apply_num, sponsor_terms01, sponsor_terms02 , sponsor_mb_id, sponsor_date "
					+ "from sponsor) where rnum between ? and ?";
			try(Connection con = this.getConnection();
					PreparedStatement pstat = con.prepareStatement(sql);){
				pstat.setInt(1, startNum);
				pstat.setInt(2, endNum);
				try(ResultSet rs = pstat.executeQuery();){

					List<SponsorDTO> list = new ArrayList<>();
					while(rs.next()) {
						int sponsor_seq = rs.getInt("sponsor_seq");
						int sponsor_amount = rs.getInt("sponsor_amount");
						String sponsor_choice = rs.getNString("sponsor_choice");
						String sponsor_agecheck = rs.getNString("sponsor_agecheck");
						String sponsor_name = rs.getNString("sponsor_name");
						String sponsor_contact = rs.getNString("sponsor_contact");
						String sponsor_birth = rs.getNString("sponsor_birth");
						String sponsor_yname = rs.getNString("sponsor_yname");
						String sponsor_ybirth = rs.getNString("sponsor_ybirth");
						String sponsor_email = rs.getNString("sponsor_email");
						String sponsor_postcode = rs.getNString("sponsor_postcode");
						String sponsor_address1 = rs.getNString("sponsor_address1");
						String sponsor_address2 = rs.getNString("sponsor_address2");
						String sponsor_apply_num = rs.getNString("sponsor_apply_num");
						String sponsor_terms01 = rs.getNString("sponsor_terms01");
						String sponsor_terms02 = rs.getNString("sponsor_terms02");
						String sponsor_mb_id = rs.getNString("sponsor_mb_id");
						Date sponsor_date = rs.getDate("sponsor_date");

						SponsorDTO dto = new SponsorDTO(sponsor_seq, sponsor_amount, sponsor_choice,  sponsor_agecheck,
								sponsor_name,  sponsor_contact,  sponsor_birth,  sponsor_yname,
								sponsor_ybirth,  sponsor_email,  sponsor_postcode,  sponsor_address1,
								sponsor_address2,  sponsor_apply_num,  sponsor_terms01,  sponsor_terms02,
								sponsor_mb_id,  sponsor_date);
						list.add(dto);
					}
					return list;
				}
			}
		}

	// adoption
	public int animalInfoModify(AnimalDTO dto) throws Exception{
		String sql = null;
		boolean existPhoto = false;
		if(dto.getAn_photo() == null) {
			sql = "update animal set an_name=?, an_category=?, an_gender=?, an_kind=?, an_age=?, an_weight=?, an_character=?, an_date=?, an_status=?, an_neutering=?, an_contents=? where code_seq=?";			
		}else {
			existPhoto = true;
			sql = "update animal set an_name=?, an_category=?, an_gender=?, an_kind=?, an_age=?, an_weight=?, an_character=?, an_date=?, an_status=?, an_photo=?, an_neutering=?, an_contents=? where code_seq=?";
		}

		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			System.out.println(dto.getAn_name());
			if(!existPhoto) {
				pstat.setNString(1, dto.getAn_name());
				pstat.setNString(2, dto.getAn_category());
				pstat.setNString(3, dto.getAn_gender());
				pstat.setNString(4, dto.getAn_kind());
				pstat.setInt(5, dto.getAn_age());
				pstat.setInt(6, dto.getAn_weight());
				pstat.setString(7, dto.getAn_character());
				pstat.setDate(8, dto.getAn_date());
				pstat.setNString(9, dto.getAn_status());
				pstat.setNString(10, dto.getAn_neutering());				
				pstat.setNString(11, dto.getAn_contents());
				pstat.setNString(12, dto.getCode_seq());
			}else {
				pstat.setNString(1, dto.getAn_name());
				pstat.setNString(2, dto.getAn_category());
				pstat.setNString(3, dto.getAn_gender());
				pstat.setNString(4, dto.getAn_kind());
				pstat.setInt(5, dto.getAn_age());
				pstat.setInt(6, dto.getAn_weight());
				pstat.setNString(7, dto.getAn_character());
				pstat.setDate(8, dto.getAn_date());
				pstat.setNString(9, dto.getAn_status());
				pstat.setNString(10, dto.getAn_photo());
				pstat.setNString(11, dto.getAn_neutering());				
				pstat.setNString(12, dto.getAn_contents());
				pstat.setNString(13, dto.getCode_seq());
			}

			int result = pstat.executeUpdate();
			return result;
		}
	}

	// adoption
	public int animalInfoDelete(String code_seq) throws Exception{

		String sql = "delete from animal where code_seq = ?";
		try(
				Connection con = this.getConnection();
				PreparedStatement pstat = con.prepareStatement(sql);
				){
			pstat.setString(1, code_seq);

			int result = pstat.executeUpdate();
			return result;
		}

	}

}
