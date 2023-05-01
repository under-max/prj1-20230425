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
			SELECT *
			FROM Board
			WHERE id = #{id}			
			""")
	public Board selectById(Integer id);


	@Update("""
			UPDATE Board
			SET
			title = #{title},
			body = #{body},
			writer = #{writer}
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
	@Options(useGeneratedKeys = true, keyColumn = "id")//자동증가하는 키 확인
	public int create(Board board);


	@Select("""
			<script>
			<bind name="pattern" value="'%' + search + '%'" />
			SELECT
				id,
				title,
				writer,
				inserted
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
			ORDER BY id DESC
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
	
	
	
}
