package com.intheknowyyc.api.data.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intheknowyyc.api.controllers.requests.EventRequest;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter
public class SpeakersConverter implements AttributeConverter<List<EventRequest.Speaker>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<EventRequest.Speaker> speakers) {
        try {
            return objectMapper.writeValueAsString(speakers); // Конвертуємо список у JSON
        } catch (JsonProcessingException e) {
            return "[]"; // Повертаємо порожній JSON масив у разі помилки
        }
    }

    @Override
    public List<EventRequest.Speaker> convertToEntityAttribute(String speakersJson) {
        try {
            return objectMapper.readValue(speakersJson, new TypeReference<List<EventRequest.Speaker>>() {});
        } catch (Exception e) {
            return Collections.emptyList(); // Повертаємо порожній список у разі помилки
        }
    }
}
