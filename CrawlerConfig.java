public class CrawlerConfig {
    private int maxThreads;
    private String outputDirectory;

    public CrawlerConfig(String outputDirectory, int maxThreads) {
        this.outputDirectory = outputDirectory;
        this.maxThreads = maxThreads;
    }

    // Getters and setters for each field
    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
