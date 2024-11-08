package com.intheknowyyc.api.data.converters;

import com.intheknowyyc.api.data.models.CommentStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommentStatusConverter implements Converter<String, CommentStatus> {
    @Override
    public CommentStatus convert(String source) {
        return CommentStatus.valueOf(source.trim().toUpperCase());
    }
}
