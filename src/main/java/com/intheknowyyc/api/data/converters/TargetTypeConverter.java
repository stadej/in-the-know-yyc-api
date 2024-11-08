package com.intheknowyyc.api.data.converters;

import com.intheknowyyc.api.data.models.TargetType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TargetTypeConverter implements Converter<String, TargetType> {
    @Override
    public TargetType convert(String source) {
        return TargetType.valueOf(source.trim().toUpperCase());
    }
}
