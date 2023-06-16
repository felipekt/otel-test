package com.example.otelpoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.otelpoc.dto.EventData;
import com.example.otelpoc.dto.Req;
import com.example.otelpoc.dto.SpanData;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@RestController
public class Controller {

	@Value("${otel.lib-name}")
	private String otelLibName;

	@Value("${otel.lib-version}")
	private String otelLibVersion;

	@Autowired
	private OpenTelemetry otel;

	@PostMapping("/spans")
	public ResponseEntity<Void> mySpan(@RequestBody Req req) {

		Tracer tracer = otel.getTracer(otelLibName, otelLibVersion);

		for (SpanData sd : req.getSpanData()) {
			Span span = tracer.spanBuilder(sd.getSpanName()).startSpan();
			try {
				span.makeCurrent();
				sd.getSpanAttributes().forEach((k, v) -> {
					span.setAttribute(k, v);
				});

				for (EventData ed : sd.getSpanEvents()) {
					AttributesBuilder attrsEventBuilder = Attributes.builder();

					ed.getEventAttributes().forEach((k, v) -> {
						attrsEventBuilder.put(k, v);
					});

					span.addEvent(ed.getEventName(), attrsEventBuilder.build());
				}
				Thread.sleep(req.getSleepTime() * 1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			} finally {
				span.end();
			}
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/dummy")
	public ResponseEntity<String> mySpan(@RequestBody String dummy) {
		return new ResponseEntity<>("Nothing to see here", HttpStatus.OK);
	}
}