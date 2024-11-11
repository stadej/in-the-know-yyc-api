package com.intheknowyyc.api.data.converters;

import com.intheknowyyc.api.data.models.EventStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EventStatusConverter  implements Converter<String, EventStatus> {
    @Override
    public EventStatus convert(String source) {
        return EventStatus.valueOf(source.trim().toUpperCase());

    }
}