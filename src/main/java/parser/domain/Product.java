package parser.domain;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Product {
//    @XmlValue
    private String name;

    private String brand;
    private String color;

    private String price;

    private String initialPrice;
    private String description;

    private String id;
    private String shippingCost;

    public Product() {
    }

    public Product(String name, String brand, String price, String initialPrice, String id) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.initialPrice = initialPrice;
        this.id = id;
    }

    public Product(String name, String brand, String color,
                   String price, String initialPrice, String description,
                   String id, String shippingCost) {
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.price = price;
        this.initialPrice = initialPrice;
        this.description = description;
        this.id = id;
        this.shippingCost = shippingCost;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(String initialPrice) {
        this.initialPrice = initialPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(String shippingCost) {
        this.shippingCost = shippingCost;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", price='" + price + '\'' +
                ", initialPrice='" + initialPrice + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
