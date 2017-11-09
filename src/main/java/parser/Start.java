package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Start {

    public static final String url = "https://www.aboutyou.de/maenner/bekleidung/jacken";

    public static void main(String[] args) throws IOException {
        List<Product> products = new ArrayList<>();

        Integer count = pagesCount(url);
        System.out.println(count);
        for (int i = 1; i <= 28; i++) {
            String tempUrl = url+"?page="+i;

            List<Element> goods = initialSetUp(tempUrl);
           forGoods: for (Element good : goods) {
               //root tag of every product
               Element article = good.select("article").first();
               String productId = article.attr("data-product-id");
               //tag div with product name
               Element divProductName = article.getElementsByAttributeValue("class","js-product-name product-name").first();
               //tag div with brand name
               Element divBrandName = article.getElementsByAttributeValue("class","product-brand").first();
               //divProductName or divBrandName can be null if among list of all products some advertising is happened
               if(Objects.isNull(divProductName) || Objects.isNull(divBrandName)){
                   continue forGoods;
               }
               /* in div product_name is a tag <div itemprop="name"> and in this div is a tag <a>
               * <div class='js-product-name product-name'>
               *     <div itemprop="name">
               *         <a>Product name</a>
               *         ....
               * */
               String productName = divProductName.child(0).child(0).text();
               /*in div brand_name is a tag <div itemprop="name"> and in this div is a tag <a>
               * <div class='product-brand'>
               *     <div itemprop="name">
               *         <a>Brand name</a>
               *         ....
               * */
               String brandName = divBrandName.child(0).child(0).text();
               Element divProductPrice = article.getElementsByAttributeValue("class","js-product-price product-price").first();

               //in div product_name is a tag <div itemprop="name"> and in this div is a tag <a>
               Element d1 = divProductPrice.getElementsByClass("price actual-price actual-price").first();

//                String productPrice =d1.child(0).text();


               products.add(new Product(productName,brandName,"0",productId));
            }

        }


//
        products.forEach(System.out::println);
        System.out.println(products.size());


    }

    private static Integer pagesCount(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Element mainContentElements = document.getElementById("main_content");
        Elements container_wrapperElements =
                mainContentElements.getElementsByAttributeValue("class","container-wrapper");
        //js_content_wrapper
        Element js_content_wrapper = container_wrapperElements.first().child(1);
        //
        Element divContainer = js_content_wrapper.child(5);

        //row js-productlist-region js-productlist productlist-container
        Element divRowJs = divContainer.child(0);
        Element divColXs12 =
                divRowJs.getElementsByAttributeValue("class","col-xs-12 js-dialog-inside-region hasSizeTooltip").first();

        Element divRow = divColXs12.child(5);//getElementsByAttributeValue("class","row").first();
        Element divColXs12Inner = divRow.getElementsByAttributeValue("class","col-xs-12").first();
        Element divRowList = divColXs12Inner.getElementsByAttributeValue("class","row list-wrapper product-image-list").first();

        Element productPager =
                divColXs12Inner
                        .child(1).child(0).child(0);
        Element liElement = productPager.child(0).getElementsByClass("gt9").first();
        return Integer.valueOf(liElement.text());
    }

    private static Elements initialSetUp(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        //root tag
        Element mainContentElements = document.getElementById("main_content");
        Elements containerWrapperElements =
                mainContentElements.getElementsByAttributeValue("class","container-wrapper");
        //<div class='js_content_wrapper'> is situated on 2 place in <div class='container-wrapper'>
        Element jsContentWrapper = containerWrapperElements.first().child(1);
        //<div class='container'> is situated on 6-th place in <div class='js_content_wrapper'>
        Element divContainer = jsContentWrapper.child(5);

        //<div class='row js-productlist-region js-productlist productlist-container'>
        Element divRowJs =
                divContainer.getElementsByClass("row js-productlist-region js-productlist productlist-container").first();
        //take <div class='col-xs-12 js-dialog-inside-region hasSizeTooltip'> from tag
        Element divColXs12 =
                divRowJs.getElementsByClass("col-xs-12 js-dialog-inside-region hasSizeTooltip").first();

        //tag <div class='row'> is situated on 6-th place in tag divColXs12
        Element divRow = divColXs12.child(5);
        //take <div class='col-xs-12'> from tag <div class='row'>
        Element divColXs12Inner = divRow.getElementsByAttributeValue("class","col-xs-12").first();
        //take element with all products
        Element divRowList = divColXs12Inner.getElementsByAttributeValue("class","row list-wrapper product-image-list").first();
        //return all products
        return divRowList.getElementsByClass("col-xs-4 isLayout3");
    }


}
