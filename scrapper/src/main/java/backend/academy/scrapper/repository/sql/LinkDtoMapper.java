package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.validation.LinkType;
import java.net.URI;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class LinkDtoMapper implements RowMapper<LinkDto> {

    private List<String> params(final ResultSet rs, final String name) throws SQLException {
        final Array paramsArray = rs.getArray(name);
        return paramsArray != null ? Arrays.asList((String[]) paramsArray.getArray()) : List.of();
    }

    @Override
    public LinkDto mapRow(@NonNull final ResultSet rs, int rowNum) throws SQLException {
        return new LinkDto(
                URI.create(rs.getString("url")),
                params(rs, "tags"),
                params(rs, "filters"),
                LinkType.valueOf(rs.getString("type")),
                (String[]) rs.getArray("uri_variables").getArray());
    }
}
