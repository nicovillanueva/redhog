package redhog;

import redis.clients.jedis.Jedis;

public class Hog extends Thread {

    private String hogName;
    private String pen;
    private Jedis jedis = null;

    public Hog(String name, String pen, String redisHost) {
        this.hogName = name;
        this.pen = pen;
        if (redisHost != null) {
            this.jedis = new Jedis(redisHost);
        }
    }

    public void run() {
        int starter = 2;
        while (true) {
            for (int i = starter; i < Integer.MAX_VALUE; i++) {
                if (isPrime(i)) {
                    System.out.println(String.format("I'm %s, from pen %s and I found the prime: %d", this.hogName, this.pen, i));
                    if (this.jedis != null)
                        jedis.hset(this.pen, this.hogName, String.valueOf(i));
                }
            }
        }
    }

    public boolean isPrime(int n) {
        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
