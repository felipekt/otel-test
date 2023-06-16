package com.example.otelpoc.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class SpanData {
	private String spanName;
	private Map<String, String> spanAttributes;
	private List<EventData> spanEvents;
}