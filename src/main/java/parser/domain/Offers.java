package parser.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "offers")
@XmlAccessorType(XmlAccessType.FIELD)
public class Offers {
    @XmlElement
    private List<Product> offer;

    public Offers() {
    }

    public Offers(List<Product> offer) {
        this.offer = offer;
    }

    public List<Product> getOffer() {
        return offer;
    }

    public void setOffer(List<Product> offer) {
        this.offer = offer;
    }
}
