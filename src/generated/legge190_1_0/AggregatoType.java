//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.20 at 10:51:34 PM CEST 
//


package generated.legge190_1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for aggregatoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aggregatoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="codiceFiscale" type="{legge190_1_0}codiceFiscaleType" minOccurs="0"/>
 *           &lt;element name="identificativoFiscaleEstero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="ragioneSociale" type="{legge190_1_0}denominazioneType"/>
 *         &lt;element name="ruolo" type="{legge190_1_0}ruoloType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aggregatoType", propOrder = {
    "codiceFiscale",
    "identificativoFiscaleEstero",
    "ragioneSociale",
    "ruolo"
})
public class AggregatoType {

    protected String codiceFiscale;
    protected String identificativoFiscaleEstero;
    @XmlElement(required = true)
    protected String ragioneSociale;
    @XmlElement(required = true)
    protected String ruolo;

    /**
     * Gets the value of the codiceFiscale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * Sets the value of the codiceFiscale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceFiscale(String value) {
        this.codiceFiscale = value;
    }

    /**
     * Gets the value of the identificativoFiscaleEstero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoFiscaleEstero() {
        return identificativoFiscaleEstero;
    }

    /**
     * Sets the value of the identificativoFiscaleEstero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoFiscaleEstero(String value) {
        this.identificativoFiscaleEstero = value;
    }

    /**
     * Gets the value of the ragioneSociale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRagioneSociale() {
        return ragioneSociale;
    }

    /**
     * Sets the value of the ragioneSociale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRagioneSociale(String value) {
        this.ragioneSociale = value;
    }

    /**
     * Gets the value of the ruolo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRuolo() {
        return ruolo;
    }

    /**
     * Sets the value of the ruolo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRuolo(String value) {
        this.ruolo = value;
    }

}
