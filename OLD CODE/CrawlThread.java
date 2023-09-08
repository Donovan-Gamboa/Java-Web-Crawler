// public class CrawlThread extends Thread {
// private final Crawler crawler;
// private final CrawlerConfig config;

// public CrawlThread(Crawler crawler, CrawlerConfig config) {
// this.crawler = crawler;
// this.config = config;
// }

// @Override
// public void run() {
// while (!crawler.isFinished()) {
// try {
// URL url = crawler.getNextUrl();
// if (url != null) {
// CrawlTask task = new CrawlTask(url, config, crawler);
// task.run();
// }
// } catch (MalformedURLException e) {
// e.printStackTrace();
// }
// }
// }
// }

// /*
// MAY NOT NEED
// MAY BE REDUNDANT
// */