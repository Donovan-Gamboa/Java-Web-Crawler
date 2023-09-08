public class Main {
    public static void main(String[] args) {
        String[] seedUrls = {"https://en.wikipedia.org/wiki/The_Count_of_Monte_Cristo"};
        int maxPages = 200;
        CrawlerConfig config = new CrawlerConfig("output.txt", 10);
        Crawler crawler = new Crawler(seedUrls, maxPages, config);
        crawler.crawl();
    }

}
