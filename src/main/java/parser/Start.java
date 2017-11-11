package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Start {

//    public static final String url = "https://www.aboutyou.de/maenner/schuhe/boots-und-stiefel";
    public static final String url = "https://www.aboutyou.de/maenner/bekleidung";
//    public static final String url = "https://www.aboutyou.de/maenner/sport/sportarten/skate/accessoires/muetzen";


    public static void main(String[] args) throws IOException {
        List<Product> products = new ArrayList<>();

        Integer count = pagesCount(url);
        System.out.println(count);


        for (int i = 1; i <= count; i++) {
            String tempUrl = url+"?page="+i;
            List<Element> goods = initialSetUp(tempUrl);
            
           forProducts: for (Element good : goods) {
               //root tag of every product
               Element article = good.select("article").first();
               String productId = article.attr("data-product-id");
               //tag div with product name
               Element divProductName = article.getElementsByAttributeValue("class","js-product-name product-name").first();
               //tag div with brand name
               Element divBrandName = article.getElementsByAttributeValue("class","product-brand").first();
               //divProductName or divBrandName can be null if among list of all products some advertising is happened
               if(Objects.isNull(divProductName) || Objects.isNull(divBrandName)){
                   continue forProducts;
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
               //element which contains price
               Element divProductPrice = article.getElementsByAttributeValue("class","js-product-price product-price").first();

               String productPrice="0";
               String productInitialPrice = "0";

               Element divActualPrice = divProductPrice.getElementsByClass("price actual-price actual-price").first();
               //some products have price like ab100
               //for this prices was created the next element
               Element divActualABPrice = divProductPrice.getElementsByClass("price  actual-price").first();
               //depend on discounts, this 2 tabs appear when discount is existed
               Element divActualOldPrice = divProductPrice.getElementsByClass("price isStriked").first();
               //element which contains actual and discount price with  _ ab _
               Element divActualABNewPrice = divProductPrice.getElementsByClass("price isOffer actual-price").first();
               //element which NOT contains actual and discount price with  _ ab _
               Element divActualNewPrice = divProductPrice.getElementsByClass("price isOffer  actual-price actual-price").first();
               ////////////////////////////////////
               if(Objects.nonNull(divActualPrice)) {
                   //if price is unchangeable
                   //in div product_name is a tag <div itemprop="name"> and in this div is a tag <a>
                   productPrice = divActualPrice.child(0).text();
                   productInitialPrice = productPrice;
               }
               if(Objects.nonNull(divActualOldPrice)
                       || Objects.nonNull(divActualABNewPrice) || Objects.nonNull(divActualNewPrice)) {
                   //if price was changed ()
                   productPrice =divActualOldPrice.child(0).text();
                   if(Objects.nonNull(divActualABNewPrice)) {
                       productInitialPrice = divActualABNewPrice.child(0).text();
                   }else {
                       productInitialPrice = divActualNewPrice.child(0).text();
                   }
               }
               if(Objects.nonNull(divActualABPrice)){
                   productPrice = divActualABPrice.child(0).text();
                   productInitialPrice = productPrice;
               }
               products.add(new Product(productName,brandName, productPrice, productInitialPrice,productId));
            }
        }
        products.forEach(System.out::println);
        System.out.println(products.size());
        listToXML(new Offers(products));
    }

    public static void listToXML(Offers offers) {

        try {

            File file = new File("products.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Offers.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(offers, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }  

    }

    private static Integer pagesCount(String url) throws IOException {
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

        //obtain widget filter this amount of product pages (like 1 2 3 ... 10)
        Element rowWidgetFilter = divColXs12.getElementsByClass("row widget-filter widget-filter-b bottom-pagination").first();
        //<div class="rowWidgetFilter"> has subtag <div class="paper-wrapper">
        Element divPaperWrapper = rowWidgetFilter.child(0);
        /*
         *  it is subtag of <div class="paper-wrapper">
         *      which can be empty or not
         *      depend on product pages amount
         */
        Element divProductPage = divPaperWrapper.getElementsByClass("product-pager").first();
        //if there is no any widget filter return 1
        if(Objects.isNull(divProductPage)){
            return 1;
        }
        //amount of page will be returned
        int pages = 0;
        /*
         *counter for finding li elements which contain not a number symbols such as >, <
         *     <div class="next"> or <div class="previous"> contain symbols > and <
        */
        int i=0;
        //obtain all li elements
        Elements liElements = divProductPage.child(0).children();
        for (Element li : liElements) {
            if(li.hasClass("next") || li.hasClass("previous")){
                i++;
            }
            if(li.hasClass("gt9")){
                pages = Integer.valueOf(li.child(0).text());
                return pages;
            }
        }
        pages = liElements.size()-i;

        return pages;
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
