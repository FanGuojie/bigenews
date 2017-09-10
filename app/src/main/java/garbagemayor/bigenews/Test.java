package garbagemayor.bigenews;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        PageProvider c = new PageProvider();
        List<NewsList.ListBean> i = c.getNewsList(0, 5);
        for (NewsList.ListBean ii : i) {
            System.out.print(ii.getNews_Title());
        }
    }
}
