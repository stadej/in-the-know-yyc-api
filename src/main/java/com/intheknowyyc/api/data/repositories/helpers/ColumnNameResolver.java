package com.intheknowyyc.api.data.repositories.helpers;

import com.intheknowyyc.api.data.exceptions.BadRequestException;
import jakarta.persistence.Column;

import java.lang.reflect.Field;

/**
 * Utility class for resolving column names from entity fields.
 */
public class ColumnNameResolver {

    private ColumnNameResolver(){
    }

    /**
     * Retrieves the column name for a given field in the specified class.
     *
     * @param clazz     The class containing the field.
     * @param fieldName The name of the field for which to retrieve the column name.
     * @return The column name if found, or the field name if no @Column annotation exists.
     */
    public static String getDatabaseColumnName(Class<?> clazz, String fieldName) {
        try {
            // Get the field from the class by its name.
            Field field = clazz.getDeclaredField(fieldName);
            // Check if the field has the @Column annotation.
            if (field.isAnnotationPresent(Column.class)) {
                // Retrieve the annotation and return the specified column name.
                Column columnAnnotation = field.getAnnotation(Column.class);
                return columnAnnotation.name();
            }
            // If no @Column annotation is found, return the field name as a fallback.
            return fieldName;
        } catch (NoSuchFieldException e) {
            throw new BadRequestException("Field not found: " + fieldName);
        }
    }
}