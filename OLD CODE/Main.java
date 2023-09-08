import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        List<String> startURLS = new ArrayList<>(1);
        startURLS.add("https://www.se.rit.edu/~swen-340/02/");
        CrawlerConfig config = new CrawlerConfig(startURLS, 2, 10, true, "output.txt");
        Crawler crawler = new Crawler(config);
        // List<CrawlThread> threadList = new ArrayList<>();

        // START -- irrelevant if we cut CrawlThread, mostly unnecessary anyways
        // for (int i = 0; i < config.getNumThreads(); i++) {
        // CrawlThread thread = new CrawlThread(crawler, config);
        // threadList.add(thread);
        // thread.start();
        // }

        // for (CrawlThread thread : threadList) {
        // try {
        // thread.join();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }

        // END

        // replace with starting crawler
        crawler.crawl();
    }
}
