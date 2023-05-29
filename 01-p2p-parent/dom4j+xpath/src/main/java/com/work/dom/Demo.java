package com.work.dom;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * ClassName:Demo
 * Package:com.work.dom
 * Description: dom4j+xpath解析xml文件
 *
 * @date:2023/4/24 14:24
 * @author:yueyue
 */
public class Demo {
    public static void main(String[] args) throws DocumentException {
        //xml文件
        String xmlStr = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "\n" +
                "<bookstore>\n" +
                "\n" +
                "<book>\n" +
                "  <title lang=\"eng\">Harry Potter</title>\n" +
                "  <price>29.99</price>\n" +
                "</book>\n" +
                "\n" +
                "<book>\n" +
                "  <title lang=\"eng\">Learning XML</title>\n" +
                "  <price>39.95</price>\n" +
                "</book>\n" +
                "\n" +
                "</bookstore>";
        /**根据xml文件生成一个文档对象 */
        Document document = DocumentHelper.parseText(xmlStr);
        // 通过xpath定位到某一个节点，返回节点对象
        Node node = document.selectSingleNode("/bookstore/book[1]/title[1]");//Harry Potter
        System.out.println(node.getText());



    }
}
