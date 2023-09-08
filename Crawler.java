import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Crawler {
    private ConcurrentLinkedQueue<String> urlQueue;
    private ExecutorService executorService;
    private int maxPages;
    private BlockingQueue<PageData> pageDataQueue;
    private CrawlerConfig config;
    private boolean firstThread;

    public Crawler(String[] seedUrls, int maxPages, CrawlerConfig config) {
        this.urlQueue = new ConcurrentLinkedQueue<String>();
        for (String url : seedUrls) {
            urlQueue.add(url);
        }
        this.executorService = Executors.newFixedThreadPool(config.getMaxThreads());
        this.maxPages = maxPages;
        this.config = config;
        this.pageDataQueue = new LinkedBlockingQueue<>();
        this.firstThread = true;
    }

    public void crawl() {
        int pagesCrawled = 0;
        while ((pagesCrawled < maxPages && !urlQueue.isEmpty()) || firstThread) {
            String url = urlQueue.poll();
            if (url != null) {
                executorService.execute(new CrawlTask(url));
                pagesCrawled++;
            }
        }
        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Error waiting for tasks to finish");
        }

        saveData();
    }

    private class CrawlTask implements Runnable {
        private String url;

        public CrawlTask(String url) {
            this.url = url;
        }

        public void run() {
            try {
                Document doc = Jsoup.connect(url).get();
                // Extract information from the document and add new URLs to the queue
                Elements links = doc.select("a[href]");
                for (org.jsoup.nodes.Element link : links) {
                    String newUrl = link.attr("abs:href");
                    if (newUrl.startsWith("http") && !urlQueue.contains(newUrl)) {
                        urlQueue.add(newUrl);
                    }
                }
                firstThread = false;
                // Store information about the page
                String title = doc.title();
                String text = doc.text();
                int numImages = doc.select("img").size();
                pageDataQueue.offer(new PageData(title, url, text, numImages));

                // Do something with the extracted information, such as storing it in a database
            } catch (Exception e) {
                // Handle any exceptions that occur during crawling
            }
        }
    }

    private void saveData() {
        try {
            FileWriter fileWriter = new FileWriter(config.getOutputDirectory());
            for (PageData pageData : pageDataQueue) {
                fileWriter.write(pageData.toString());
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
