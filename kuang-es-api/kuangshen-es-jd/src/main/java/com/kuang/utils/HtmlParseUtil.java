package com.kuang.utils;

import com.kuang.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import sun.misc.Contended;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ygl
 * @description
 * @date 2020/9/23 20:26
 */
@Component
public class HtmlParseUtil {
//    public static void main(String[] args) throws IOException {
//        List<Content> java = new HtmlParseUtil().parseJD("java");
//        for (Content content:java) {
//            System.out.println(content);
//        }
//    }

    public List<Content> parseJD(String keywords) throws IOException {
        //获取请求    https://search.jd.com/Search?keyword=java
        String url="https://search.jd.com/Search?keyword="+keywords;
        //解析网页
        Document document = Jsoup.parse(new URL(url), 30000);

        Element element = document.getElementById("J_goodsList");
        // 获取所有的li标签
        Elements elements = element.getElementsByTag("li");
        ArrayList<Content> goodLists = new ArrayList<>();
        //获取元素中的内容，这里的el就是每一个li标签
        for(Element el:elements){
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            System.out.println("img+++++:"+img);
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            Content content = new Content();
            content.setTitle(title);
            content.setImg(img);
            content.setPrice(price);

            goodLists.add(content);
        }
        return goodLists;
    }
}
