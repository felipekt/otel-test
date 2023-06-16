package com.example.otelpoc.dto;

import java.util.List;

import lombok.Data;

@Data
public class Req {
	private long sleepTime;
	private List<SpanData> spanData;
}