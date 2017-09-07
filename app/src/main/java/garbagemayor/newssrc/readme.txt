import my_news

Page类获取页面信息
Page(int No, int Size) 获取latest news中第No页内容，每页news条数Size
Page(String keyword,int category, int No, int Size) 搜索category类中keyword关键词新闻
int getLength() 获取当前页条数
String getClassTag(int id) 获取第id条新闻分类
String getSource(int id) 获取第id条新闻来源
String getAuthor(int id) 获取第id条新闻作者
String getTitle(int id) 获取第id条新闻标题
String getTime(int id) 获取第id条新闻时间
String getIntro(int id) 获取第id条新闻简介

Detail getDetail(int id) 获取第id条新闻 返回Detail类
String getContent() 获取第id条新闻正文 
String getAuthor() 获取第id条新闻作者
String getTime() 获取第id条新闻时间
String getJournal() 获取第id条新闻作者