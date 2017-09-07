package garbagemayor.bigenews;


public class NewsItem {
    private String title;
    private String content;
    private String time;

    public NewsItem(String title, String contect, String time) {
        this.title = title;
        this.content = contect;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getTime() {
        return time;
    }
}
