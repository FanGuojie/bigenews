package garbagemayor.bigenews;

public class Test {
    public static void main(String[] args) {
        PageProvider pageProvider = new PageProvider();
        MyCallBack callBack = new MyCallBack() {
            @Override
            public void callbackCall() {
                System.out.print("back");
            }
        };
        pageProvider.loadNewsList(1, 20, callBack);
    }
}
