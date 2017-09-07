package garbagemayor.bigenews.newssrc;


public class PageItem {
    private String newsClassTag;            //<!--新闻所属的分类-->
    private String news_ID;                  //<!-- 新闻id-->
    private String news_Source;             //<!-- 新闻来源 -->
    private String news_Title;              //<!--标题 -->
    private String news_Time;               //<!--时间 -->
    private String news_URL;                //<!--新闻的URL链接 -->
    private String news_Author;            //<!--新闻的作者-->
    private String lang_Type;              //<!--语言类型 -->
    private String news_Pictures;          //<!--新闻的图片路径-->
    private String news_Video;
    private String news_Intro;             //<!-- 简介 -->

    public String print() {
        return "newsClassTag; " + newsClassTag + "\n" +
                "Source: " + news_Source + "\n" +
                "Title: " + news_Title + "\n" +
                "Time: " + news_Time+ "\n" +
                "ID: " + news_ID + "\n";
    }
    public String getId() {
        return news_ID;
    }
    public String getClassTag() {
        return newsClassTag;
    }
    public String getSource() {
        return news_Source;
    }
    public String getAuthor() {
        return news_Author;
    }
    public String getTitle() {
        return news_Title;
    }
    public String getTime() {
        return news_Time;
    }
    public String getIntro() {
        return news_Intro;
    }
}
