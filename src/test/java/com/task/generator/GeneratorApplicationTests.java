package com.task.generator;

import kafka.utils.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class GeneratorApplicationTests {
    @Test
    public void oneMillionPerHowMuchSecond() {
        final var threads = new ArrayList<Thread>();
        final int countThread = 4000;
        IntStream.range(0, countThread).forEach(x -> threads.add(newThread()));
        threads.forEach(Thread::start);
        while (!threads.isEmpty()) threads.removeIf(t -> !t.isAlive());
        final var end = Calendar.getInstance().getTimeInMillis();
        final var timeTest = ((end - start) / 1000);
        System.out.println("COUNT REQUEST : " + countThread);
        System.out.println("RESULT TIME : " + timeTest + " s");
        if (!duplicate) {
            System.out.println("RESULT TEST COUNT PER SECOND: " + i.get() / timeTest);
        }
    }

    @Test
    public void onDuplicate() {
        duplicate = true;
        oneMillionPerHowMuchSecond();
        assert state;
        System.out.println("NOT DUPLICATES");
    }

    public synchronized long generateId() {
        try {
            final var mvcResult = mockMvc.perform(get("/generate/id").accept(MediaType.APPLICATION_JSON)).andReturn();
            if (start == 0) {
                start = Calendar.getInstance().getTimeInMillis();
            }
            final var result = Json.parseBytes(mvcResult.getResponse().getContentAsByteArray());
            final var id = result.get().asJsonObject().get("id").get();
            return Long.parseLong(String.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Thread newThread() {
        return new Thread(() -> {
            final var id = generateId();
            if (id != 0) {
                i.incrementAndGet();
                if (duplicate) {
                    if (array.contains(id)) {
                        state = false;
                    }
                    array.add(id);
                }
            }
        });
    }

    @Autowired
    private MockMvc mockMvc;

    final List<Long> array = new ArrayList<>(1_000);
    volatile AtomicInteger i = new AtomicInteger(0);
    volatile long start;
    volatile boolean state = true;
    volatile boolean duplicate = false;
}
