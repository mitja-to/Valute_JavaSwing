/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.valuta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Mitja
 */
public class MyContentHandler extends DefaultHandler  { 

    List<Valuta> listValut = new ArrayList<>();
    Valuta ob_val = null;
    Integer id = 0;
    Date date_datum = null;
    String content = null;

    private XMLReader xmlReader; 

    public MyContentHandler(XMLReader xmlReader) { 
        this.xmlReader = xmlReader; 
    } 

    MyContentHandler() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setDocumentLocator(Locator locator) { 
    } 

    public void startDocument() throws SAXException { 
    } 

    public void endDocument() throws SAXException { 
    } 

    public void startPrefixMapping(String prefix, String uri) 
            throws SAXException { 
    } 

    public void endPrefixMapping(String prefix) throws SAXException { 
    } 

    public void startElement(String uri, String localName, String qName, 
            Attributes atts) throws SAXException {
        
            switch(qName)
            {
                case "tecaj":
                    ob_val = new Valuta();
                    ob_val.setId(id);
                    id++;
                    ob_val.setOznaka(atts.getValue("oznaka"));
                    ob_val.setSifra(Short.parseShort(atts.getValue("sifra")));
                    ob_val.setDatum(date_datum);
                    break;
                case "tecajnica":
                    String datum = atts.getValue("datum");
                    String[] datumi = datum.split("-");
                    date_datum = new Date(Integer.parseInt(datumi[0])-1900, Integer.parseInt(datumi[1])-1, Integer.parseInt(datumi[2]));
                    break;
            }
    } 

    public void endElement(String uri, String localName, String qName) 
            throws SAXException { 
        switch(qName)
        {
            case "tecajnica":
                break;
            case "tecaj":
                ob_val.setTecaj(content);
                listValut.add(ob_val);
                break;
        }
    } 

    public void characters(char[] ch, int start, int length) 
            throws SAXException {
        content = String.copyValueOf(ch, start, length).trim();

    } 

    public void ignorableWhitespace(char[] ch, int start, int length) 
            throws SAXException { 
    } 

    public void processingInstruction(String target, String data) 
            throws SAXException { 
    } 

    public void skippedEntity(String name) throws SAXException { 
    } 

}
