package com.example.demo.mapper;

import java.util.*;

import org.apache.ibatis.annotations.*;

import com.example.demo.domain.*;

@Mapper
public interface BoardMapper {
	
	@Select("""
			SELECT
			id, title, writer, inserted
			FROM Board
			ORDER BY id DESC
			""")
	public List<Board> selectALL();

	
	@Select("""
			SELECT 
				b.id,
				b.title,
				b.body,
				b.inserted,
				b.writer,
				f.fileName
			FROM Board b LEFT JOIN FileName f ON b.id = f.boardId
			WHERE b.id = #{id}			
			""")
	@ResultMap("boardResultMap")
	Board selectById(Integer id);


	@Update("""
			UPDATE Board
			SET
			title = #{title},
			body = #{body}
			WHERE 
				id = #{id}
			""")
	public int update(Board board);


	@Delete("""
			DELETE FROM Board
			WHERE id = #{id}
			""")
	public int deleteById(Integer id);

	
	@Insert("""
			INSERT INTO Board (title, writer, body)
			VALUES (#{title}, #{writer}, #{body})			
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")//자동증가하는 키 확인	
	public int create(Board board);


	@Select("""
			<script>
			<bind name="pattern" value="'%' + search + '%'" />
			SELECT
				b.id,
				b.title,
				b.writer,
				b.inserted,
				COUNT(f.id) fileCount
			FROM Board b LEFT JOIN FileName f
			ON b.id = f.boardId
			
			<where>
				<if test="(type eq 'all') or (type eq 'title')">
				   title  LIKE #{pattern}
				</if>
				<if test="(type eq 'all') or (type eq 'body')">
				OR body   LIKE #{pattern}
				</if>
				<if test="(type eq 'all') or (type eq 'writer')">
				OR writer LIKE #{pattern}
				</if>
			</where>
			GROUP BY b.id
			ORDER BY b.id DESC
			LIMIT #{startIndex}, #{rowPerPage}
			</script>
			""")	
	List<Board> selectAllPaging(Integer startIndex, Integer rowPerPage, String search, String type);

	

	@Select("""
			<script>
			<bind name="pattern" value="'%' + search + '%'" />
			SELECT COUNT(*) 
			FROM Board
			<where>
				<if test="(type eq 'all') or (type eq 'title')">
				   title  LIKE #{pattern}
				</if>
				<if test="(type eq 'all') or (type eq 'body')">
				OR body   LIKE #{pattern}
				</if>
				<if test="(type eq 'all') or (type eq 'writer')">
				OR writer LIKE #{pattern}
				</if>
			</where>
			</script>
			""")
	Integer countAll(String search, String type);

	
	@Insert("""
			INSERT INTO FileName (boardId, fileName)
			VALUES (#{boardId}, #{filename})
			""")
	public void insertFileName(Integer boardId, String filename);

	//FileName 에서 해당하는 filename list 추출
	@Select("""
			SELECT f.FileName FROM Board b JOIN FileName f
			ON b.id = f.boardId 
			WHERE b.id = #{id}
			""")
	public List<String> selectFileNameByBoardId(Integer id);

	
	// 이미지 foreign key 삭제용
	@Delete("""
			DELETE FROM FileName
			WHERE boardId = #{id} 
			""")
	public void deleteFileNameByBoardId(Integer id);

	//수정에서 삭제 
	@Delete("""
			DELETE FROM FileName
			WHERE boardId = #{boardId}
			AND fileName = #{fileName}
			""")
	public void deleteFileNameByBoardIdAndFileName(Integer boardId, String fileName);


	@Select("""
			SELECT id
			FROM Board
			WHERE writer = #{writer}
			""")
	public List<Integer> selectIdByWriter(String writer);	
	
	
}
