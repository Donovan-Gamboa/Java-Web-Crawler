public class PageData {
    private String title;
    private String url;
    private String textContent;
    private int numImages;

    public PageData(String title, String url, String textContent, int numImages) {
        this.title = title;
        this.url = url;
        this.textContent = textContent;
        this.numImages = numImages;
    }

    // Getters and setters for each field
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static String limit(String value, int length) {
        StringBuilder buf = new StringBuilder(value);
        if (buf.length() > length) {
            buf.setLength(length);
            buf.append("...");
        }
        return buf.toString().replaceAll("(.{100})", "$1\n\t");

    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public int getNumImages() {
        return numImages;
    }

    public void setNumImages(int numImages) {
        this.numImages = numImages;
    }


    // Override the toString() method to provide a string representation of the
    // PageData object
    @Override
    public String toString() {
        return "Title: \n\t" + title + "\n" +
                "URL: \n\t" + url + "\n" +
                "Text content: \n\t" + limit(textContent, 2000) + "\n" +
                "Number of images: \n\t" + numImages + "\n\n\n";
    }
}
