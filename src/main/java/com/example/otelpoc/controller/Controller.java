package com.example.otelpoc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.otelpoc.dto.EventData;
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

	@GetMapping("/spans")
	public ResponseEntity<String> getWithSpan(@RequestParam long delaySeconds) {
		HashMap<String, String> attrs = new HashMap<>();
		attrs.put("paramDelaySeconds", Long.toString(delaySeconds));
		ArrayList<EventData> events = new ArrayList<>();

		SpanData sd = new SpanData();
		sd.setSpanEvents(events);
		sd.setSpanAttributes(attrs);
		sd.setSpanName("get-with-span");

		ArrayList<SpanData> spans = new ArrayList<>();
		spans.add(sd);

		generateSpans(spans, delaySeconds);

		return new ResponseEntity<>("GET with span!", HttpStatus.OK);
	}

	@PostMapping("/spans")
	public ResponseEntity<String> postWithSpan(@RequestParam long delaySeconds, @RequestBody List<SpanData> spans) {
		generateSpans(spans, delaySeconds);
		return new ResponseEntity<>("POST with span!", HttpStatus.OK);
	}

	private void generateSpans(List<SpanData> spans, long delaySeconds) {
		Tracer tracer = otel.getTracer(otelLibName, otelLibVersion);

		for (SpanData sd : spans) {
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
				Thread.sleep(delaySeconds * 1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			} finally {
				span.end();
			}
		}
	}

	@GetMapping("/dummy")
	public ResponseEntity<String> getWithouSpan(@RequestParam long delaySeconds) {
		try {
			Thread.sleep(delaySeconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<String>("GET without span!", HttpStatus.OK);
	}

	@PostMapping("/dummy")
	public ResponseEntity<String> postWithouSpan(@RequestParam long delaySeconds, @RequestBody List<SpanData> spans) {
		try {
			Thread.sleep(delaySeconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>("POST without span!", HttpStatus.OK);
	}
}