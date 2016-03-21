package redhog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@SpringBootApplication
public class Application {

    private String redisHost = System.getenv("REDIS_HOST");
    private List<Thread> hogs = new ArrayList<>();
    private List<String> names = loadNames();

    private List<String> loadNames() {
        try {
            // Fuck you Java. Fuck you so much.
            Resource r = new ClassPathResource("/mock_data.txt");
            InputStream is = r.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line;
            List<String> templist = new ArrayList<>();
            while ((line = in.readLine()) != null) {
                templist.add(line);
            }
            return templist;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hog up CPU resources (by generating prime numbers) for a given time.
     * Can be cancelled sending a GET request to /hog/abort
     *
     * @param timeout Length (in seconds) to hog up resources
     * @param threads Threads to generate in order to consume resources (consider your amount of CPU cores)
     */
    @RequestMapping("/hog")
    public void hogCpu(@RequestParam(value = "timeout", defaultValue = "10") int timeout,
                       @RequestParam(value = "threads", defaultValue = "1") int threads)
            throws IOException, URISyntaxException {
        Random r = new Random();
        String hogPen = String.valueOf(r.nextInt(Integer.MAX_VALUE));
        List<Thread> localHogs = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            String s = names.get(r.nextInt(names.size()));
            Thread h = new Hog(s, hogPen, redisHost);
            h.start();
            localHogs.add(h);
        }
        this.hogs.addAll(localHogs);
        Sleeper.sleepFor(timeout, "seconds");
        localHogs.parallelStream().forEach(Thread::stop);
        this.hogs.removeAll(localHogs);
    }

    /**
     * Cancel any running CPU hogs
     */
    @RequestMapping("/hog/abort")
    public void abort() {
        this.hogs.parallelStream().forEach(Thread::stop);
        this.hogs.clear();
    }

    /**
     * Simulate a "realistic" query in time and response type. It takes a while, and replies a JSON
     *
     * @param timeout  How long should the request take to return (-1 for a random between 0 and 1000)
     * @param timeunit Timeunit to use (defaults to milliseconds, but accepts seconds and minutes too)
     * @return Mocked up JSON response (instance of 'Response')
     */
    @RequestMapping("/simulate")
    public Response simulateQuery(@RequestParam(value = "duration", defaultValue = "-1") int timeout,
                                  @RequestParam(value = "timeunit", defaultValue = "milliseconds", required = false) String timeunit) {
        Random r = new Random();
        if (timeout == -1) {
            timeout = r.nextInt(1000);
        }
        Sleeper.sleepFor(timeout, timeunit);
        String name = this.names.get(r.nextInt(this.names.size()));
        return new Response(name, String.valueOf(timeout), timeunit);
    }

    private class Response {
        public String name, time, unit;

        public Response(String name, String time, String unit) {
            this.name = name;
            this.time = time;
            this.unit = unit;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
