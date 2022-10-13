//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.10.13 at 01:10:36 AM IDT 
//


package engine.enigmaEngine.schemaBinding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}CTE-Machine"/>
 *         &lt;element ref="{}CTE-Decipher"/>
 *         &lt;element ref="{}CTE-Battlefield"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cteMachine",
    "cteDecipher",
    "cteBattlefield"
})
@XmlRootElement(name = "CTE-Enigma")
public class CTEEnigma {

    @XmlElement(name = "CTE-Machine", required = true)
    protected CTEMachine cteMachine;
    @XmlElement(name = "CTE-Decipher", required = true)
    protected CTEDecipher cteDecipher;
    @XmlElement(name = "CTE-Battlefield", required = true)
    protected CTEBattlefield cteBattlefield;

    /**
     * Gets the value of the cteMachine property.
     * 
     * @return
     *     possible object is
     *     {@link CTEMachine }
     *     
     */
    public CTEMachine getCTEMachine() {
        return cteMachine;
    }

    /**
     * Sets the value of the cteMachine property.
     * 
     * @param value
     *     allowed object is
     *     {@link CTEMachine }
     *     
     */
    public void setCTEMachine(CTEMachine value) {
        this.cteMachine = value;
    }

    /**
     * Gets the value of the cteDecipher property.
     * 
     * @return
     *     possible object is
     *     {@link CTEDecipher }
     *     
     */
    public CTEDecipher getCTEDecipher() {
        return cteDecipher;
    }

    /**
     * Sets the value of the cteDecipher property.
     * 
     * @param value
     *     allowed object is
     *     {@link CTEDecipher }
     *     
     */
    public void setCTEDecipher(CTEDecipher value) {
        this.cteDecipher = value;
    }

    /**
     * Gets the value of the cteBattlefield property.
     * 
     * @return
     *     possible object is
     *     {@link CTEBattlefield }
     *     
     */
    public CTEBattlefield getCTEBattlefield() {
        return cteBattlefield;
    }

    /**
     * Sets the value of the cteBattlefield property.
     * 
     * @param value
     *     allowed object is
     *     {@link CTEBattlefield }
     *     
     */
    public void setCTEBattlefield(CTEBattlefield value) {
        this.cteBattlefield = value;
    }

}
