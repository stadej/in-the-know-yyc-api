package com.intheknowyyc.api.controllers.responses;

import com.intheknowyyc.api.data.models.Event;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Paginated response for events")
public class PaginatedEventResponse {

    @ArraySchema(schema = @Schema(implementation = Event.class))
    private List<Event> content;

    @Schema(description = "Total number of items available")
    private long totalElements;

    @Schema(description = "Total number of pages available")
    private int totalPages;

    @Schema(description = "Current page number")
    private int number;

    @Schema(description = "Size of the page")
    private int size;
}
