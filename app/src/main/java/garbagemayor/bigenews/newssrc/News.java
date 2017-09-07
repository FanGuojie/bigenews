package garbagemayor.bigenews.newssrc;

public class News {
    private PageItem[] list;
    private String pageNo;
    private String pageSize;
    private String totalPages;
    private String totalRecords;

    public String print() {
        String res = "";
        for (PageItem c : list)
            res += c.print();
        return res + "pageNo:" + pageNo;
    }
    public String getPageNo() {
        return pageNo;
    }
    public String getTotalPages() {
        return totalPages;
    }
    public String getTotalRecords() {
        return totalRecords;
    }
    public PageItem[] getPageItem() {
        return list;
    }
}
