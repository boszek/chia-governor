package pl.thatisit.plotter;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

public class DateUUIDTest {

    @Test
    public void shouldFormatDateUUID() {
        var value = DateUUID.randomDateUUID().toString();

        assertThat(value).matches("^\\d{8}-\\d{6}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}$");
    }

    @Test
    public void shouldIdentifyDateUUID() {
        var example = "20210501-233908-4164-9d27-46e1e6faff1f";

        assertThat(DateUUID.isDateUUID(example)).isTrue();
    }

    @Test
    public void shouldDiscardNonDateUUID() {
        var example = "aaaaa-233908-4164-9d27-46e1e6faff1f";

        assertThat(DateUUID.isDateUUID(example)).isFalse();
    }

}