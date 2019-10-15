package nextstep.jdbc;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcTemplateTest {

    @BeforeEach
    public void setup() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
    }

    @Test
    void execute_query() throws SQLException {
        // given
        try (JdbcTemplate jdbcTemplate = new JdbcTemplate(ConnectionManager.getConnection())) {
            // when
            Map<String, Object> params = new HashMap<>();
            params.put("questionId", 1);

            Map<String, Object> results = Maps.newHashMap();
            jdbcTemplate.executeQuery("SELECT * FROM questions WHERE questionId=:questionId",
                    params,
                    resultSet -> {
                        results.put("questionId", resultSet.getLong("questionId"));
                        results.put("title", resultSet.getString("title"));
                    });

            // then
            assertThat((Long) results.get("questionId")).isEqualTo(1);
            assertThat((String) results.get("title")).isEqualTo("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?");
        }

    }

}