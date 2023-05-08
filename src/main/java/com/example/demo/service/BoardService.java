package com.example.demo.service;

import java.io.*;
import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

import com.example.demo.domain.*;
import com.example.demo.mapper.*;

import software.amazon.awssdk.core.sync.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

// 큰서비스라면 인터페이스로 나눔
// Component여도 상관 없지만 Service안에 Component 포함되어있으므로 명시적으로 @Service 기입
@Service
@Transactional(rollbackFor = Exception.class) // 모든 exception에 rollback 적용
public class BoardService {

	// 삭제 s3.delete
	// 생성 s3.putObject
	@Autowired
	private S3Client s3;

	@Value("${aws.s3.bucketName}")
	private String bucketName;

	// mapper 한테 일 시키므로 mapper 주입
	@Autowired
	private BoardMapper mapper;

	public List<Board> listBoard() {
		List<Board> list = mapper.selectALL();
		return list;
	}

	public Board getBoard(Integer id) {
		Board board = mapper.selectById(id);
		return board;
	}

	public boolean modify(Board board, List<String> removeFileNames, MultipartFile[] addFiles) throws Exception { // 원래
																													// void지만
																													// boolean
																													// 은
																													// 지정자
																													// 마음
		// FileName 테이블 삭제
		if (removeFileNames != null && !removeFileNames.isEmpty()) {
			for (String fileName : removeFileNames) {
				// s3에서 파일(객체) 삭제
				String objectKey = "board/" + board.getId() + "/" + fileName;
				DeleteObjectRequest dor = DeleteObjectRequest.builder()
						.bucket(bucketName)
						.key(objectKey)
						.build();
				s3.deleteObject(dor);

				// 테이블에서 삭제
				mapper.deleteFileNameByBoardIdAndFileName(board.getId(), fileName);
			}
		}

		// 그림파일 추가
		// 새 파일 추가
		for (MultipartFile newFile : addFiles) {
			if (newFile.getSize() > 0) {
				// 테이블에 파일명 추가
				mapper.insertFileName(board.getId(), newFile.getOriginalFilename());

				// s3에 파일(객체) 업로드
				String objectKey = "board/" + board.getId() + "/" + newFile.getOriginalFilename();
				PutObjectRequest por = PutObjectRequest.builder()
						.acl(ObjectCannedACL.PUBLIC_READ)
						.bucket(bucketName)
						.key(objectKey)
						.build();
				RequestBody rb = RequestBody.fromInputStream(newFile.getInputStream(), newFile.getSize());
				s3.putObject(por, rb);
			}
		}

		// 게시물(board) 테이블 수정
		int cnt = mapper.update(board);

		return cnt == 1;
	}

	public boolean remove(Integer id) {
		// 파일명 조회
		List<String> fileNames = mapper.selectFileNameByBoardId(id);
		System.out.println(fileNames);
		// 파일명 조회 확인됨 [main.jpa, aaa.jpa], 폴더명은 들어온 id로 확인 가능

		// filename 테이블의 데이터 지우기
		mapper.deleteFileNameByBoardId(id);

		// s3 bucket의 파일(객체) 지우기
		for (String fileName : fileNames) {
			String objectKey = "board/" + id + "/" + fileName;
			DeleteObjectRequest dor = DeleteObjectRequest.builder().bucket(bucketName).key(objectKey).build();
			s3.deleteObject(dor);
		}
		// 게시물 테이블의 데이터 지우기
		int cnt = mapper.deleteById(id);
		return cnt == 1;
	}

	public boolean createProcess(Board board, MultipartFile[] files) throws Exception {

		int cnt = mapper.create(board);

		System.out.println(board.getId());
		for (MultipartFile file : files) {
			if (file.getSize() > 0) {
				String objectKey = "board/" + board.getId() + "/" + file.getOriginalFilename();
				PutObjectRequest por = PutObjectRequest.builder()
						.bucket(bucketName)
						.key(objectKey) // 폴더 + 파일명
						.acl(ObjectCannedACL.PUBLIC_READ)
						.build();
				RequestBody rb = RequestBody.fromInputStream(file.getInputStream(), file.getSize()); // Amazon의
																										// RequestBody

				s3.putObject(por, rb);
				// db에 관련 정보 저장
				mapper.insertFileName(board.getId(), file.getOriginalFilename());
			}
			// 게시물 insert
		}
		return cnt == 1;

	}

	public Map<String, Object> listBoard(Integer page, String search, String type) { // pagenation용 SELECT
		Integer rowPerPage = 15; // 페이지당 행의 수
		Integer startIndex = (page - 1) * rowPerPage; // 20개 보여줄꺼면 *20 10개라면 *10
		// 페이지네이션 필요한 정보
		Integer numOfRecords = mapper.countAll(search, type);// 전체 레코드 갯수
		// 마지막 페이지 번호
		Integer lastPageNumber = (numOfRecords - 1) / rowPerPage + 1;

		// 페이지네이션 왼쪽 번호
		Integer leftPageNum = page - 5;

		// 1보다 작을수 없음
		leftPageNum = Math.max(leftPageNum, 1);
		// 페이지네이션 오른쪽 번호
		Integer rightPageNum = page + 4;
		rightPageNum = Math.min(rightPageNum, lastPageNumber);

		// 현재 페이지 확인
//		Integer currentPageNumber = page;
//		System.out.println(page);
		// 빈 혹은 맵에 만들어서 토스

		Map<String, Object> pageInfo = new HashMap<>();
		pageInfo.put("rightPageNum", rightPageNum);
		pageInfo.put("leftPageNum", leftPageNum);
		pageInfo.put("currentPageNum", page);
		pageInfo.put("lastPageNumber", lastPageNumber);
		// 게시물 목록
		List<Board> list = mapper.selectAllPaging(startIndex, rowPerPage, search, type);
		// 게시물 목록
		return Map.of("pageInfo", pageInfo,
				"boardList", list);

	}

}
