package com.solo.board.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class BoardDto {
	private int bnum;
	private String btitle;
	private String bcontents;
	private String bid;
	private String mname;
	private Timestamp bdate;
	private int bviews;
}
