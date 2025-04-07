package backend.academy.base.schema.scrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddTagRequest(@JsonProperty("name") String name) {}
