package com.example.http.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.val;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

class JacksonObjectMapper extends ObjectMapper {
	private static final long serialVersionUID = 1L;

	JacksonObjectMapper() {
		this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		setSerializationInclusion(JsonInclude.Include.NON_NULL);
		val dateFormatter = new DateTimeFormatterBuilder()
				.parseCaseInsensitive().append(DateTimeFormatter.ISO_LOCAL_DATE)
				.appendLiteral(' ').append(DateTimeFormatter.ISO_LOCAL_TIME)
				.toFormatter();
		val javaTimeModule = new JavaTimeModule();
		javaTimeModule.addDeserializer(LocalDateTime.class,
				new LocalDateTimeDeserializer(dateFormatter));
		javaTimeModule.addSerializer(LocalDateTime.class,
				new LocalDateTimeSerializer(dateFormatter));
		registerModule(javaTimeModule);
		registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module());
	}

}
