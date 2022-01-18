package com.solo.board.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ReplyDto {
	private int r_num;//댓글번호 key
	private int r_bnum;//게시글 번호(검색)
	private String r_contents;//댓글 내용
	private String r_id;//댓글작성자 아이디(로그인 아이디)
	@JsonFormat(pattern = "yyyy-HH-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Timestamp r_date;//댓글 작성 시간
}
