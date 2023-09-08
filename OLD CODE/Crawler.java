import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
    private final CrawlerConfig config;
    private final Set<String> visitedUrls;
    private final BlockingQueue<PageData> pageDataQueue;
    private ThreadQueue threadQueue; // potentially overkill?? seems unnecessary for the tasks performed,
                                     // could be replaced with a set or list
    private boolean titleFound = false;

    public Crawler(CrawlerConfig config) {
        this.config = config;
        visitedUrls = ConcurrentHashMap.newKeySet(); // uhhhh, no, just use a hash or array set
        pageDataQueue = new LinkedBlockingQueue<>();
    }

    public void crawl() {
        threadQueue = ThreadQueue.newFixedThreadPool(config.getMaxThreads());
        int depth = 0;
        for (String url : config.getStartUrls()) {
            threadQueue.execute(new CrawlTask(url, depth));
        }
        try {
            threadQueue.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (config.isSaveData()) {
            saveData();
        }
    }

    /**
     * Maybe extract this into the container class
     */
    private class CrawlTask implements Runnable {
        private final String url;
        private final int depth;

        public CrawlTask(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }

        @Override
        public void run() {
            if (visitedUrls.contains(url)) {
                threadQueue.threadFinished();
                return;
            }
            visitedUrls.add(url);
            PageData pageData = getPageData(url);
            if (pageData != null) {
                pageDataQueue.offer(pageData);
                if (depth < config.getMaxDepth()) {
                    for (String link : getLinks(pageData.getHTML())) {
                        threadQueue.execute(new CrawlTask(link, depth+1));
                    }
                }
            }
            threadQueue.threadFinished();
        }
    }

    private PageData getPageData(String url) { // does not include method of counting images, otherwise looks good
        try {
            URLConnection connection = new URL(url).openConnection();
            String contentType = connection.getContentType();
            if (contentType != null && contentType.startsWith("text/html")) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder textContent = new StringBuilder();
                int numImages = 0;
                while ((line = in.readLine()) != null) {
                    textContent.append(line).append("\n");
                    if (line.contains("<img")) {
                        numImages++;
                    }
                }
                in.close();
                String title = extractTitle(textContent.toString());
                return new PageData(title, url, extractVisibleText(textContent.toString()), numImages,
                        textContent.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractVisibleText(String html) {
        Document doc = Jsoup.parse(html);
        StringBuilder sb = new StringBuilder();

        for (Element element : doc.getAllElements()) {
            if (isVisibleTextElement(element)) {
                sb.append(element.text()).append(" ");
            }
        }

        return sb.toString().trim();
    }

    private static boolean isVisibleTextElement(Element element) {
        String tag = element.tagName().toLowerCase();
        if (tag.equals("script") || tag.equals("style")) {
            return false;
        }
        if (element.hasAttr("hidden") || element.hasAttr("aria-hidden") || element.hasAttr("type")) {
            return false;
        }
        String text = element.text().trim();
        if (text.isEmpty()) {
            return false;
        }
        return true;
    }

    public static List<String> getLinks(String html) {
        List<String> links = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements linkElements = doc.select("a[href]");

        for (Element linkElement : linkElements) {
            String link = linkElement.attr("abs:href");
            links.add(link);
        }
        links.removeAll(Arrays.asList("", null));
        return links;
    }

    private String extractTitle(String textContent) {
        int startIndex = textContent.indexOf("<title>");
        if (titleFound != true) {
            if (startIndex != -1) {
                startIndex += 7;
                int endIndex = textContent.indexOf("</title>", startIndex);
                if (endIndex != -1) {
                    titleFound = true;
                    return textContent.substring(startIndex, endIndex).trim();
                }
            }
        }
        return "";
    }

    

}
