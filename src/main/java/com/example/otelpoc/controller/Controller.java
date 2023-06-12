package com.example.otelpoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.otelpoc.dto.SpanReq;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

@RestController
public class Controller {
	
	@Value("${otel.lib-name}")
	private String otelLibName;
	
	@Value("${otel.lib-version}")
	private String otelLibVersion;
	
	@Autowired
	private OpenTelemetry otel;

	@PostMapping("/span")
	public ResponseEntity<Void> mySpan(@RequestBody SpanReq req) {
		
		Tracer tracer = otel.getTracer(otelLibName, otelLibVersion);
		Span span = tracer.spanBuilder("otel-test.my-span").startSpan();

		// Make the span the current span
		try (Scope ss = span.makeCurrent()) {
			try {
				Attributes attrs = Attributes.builder()
						.put("testString", "value1")
						.put("testBool", true)
						.put("testNumber", 99)
						.build();
				span.setAllAttributes(attrs);
				
				Attributes attrsEvent = Attributes.builder()
						.put("eventString", "value1")
						.put("eventBool", true)
						.put("eventNumber", 99)
						.build();
				span.addEvent("test event", attrsEvent);
			    Thread.sleep(req.getSleepTime() * 1000);
			} catch (InterruptedException ie) {
			    Thread.currentThread().interrupt();
			}
		} finally {
		    span.end();
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}