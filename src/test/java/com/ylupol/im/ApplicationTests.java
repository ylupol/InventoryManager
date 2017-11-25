package com.ylupol.im;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testDbMigration() {
        int count = jdbcTemplate
            .queryForObject("SELECT count(*) FROM location", Integer.class);

        assertEquals(0, count);
    }

}
