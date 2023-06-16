package com.example.otelpoc.dto;

import java.util.Map;

import lombok.Data;

@Data
public class EventData {
	private String eventName;
	private Map<String, String> eventAttributes;
}
