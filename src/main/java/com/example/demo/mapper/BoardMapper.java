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
	
	
	
}
