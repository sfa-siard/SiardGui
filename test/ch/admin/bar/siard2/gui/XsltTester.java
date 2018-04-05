package ch.admin.bar.siard2.gui;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.bootstrap.*;

import static org.junit.Assert.*;
import org.junit.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.*;
import ch.enterag.utils.EU;

public class XsltTester
{
  @Test
  public void test()
  {
    try
    {
      System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
      System.setProperty("javax.xml.parsers.SAXParserFactory","com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
      System.setProperty("javax.xml.transform.TransformerFactory","com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
      System.setProperty("org.w3c.dom.DOMImplementationSourceList","com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
      /*
       * big Oracle idiocy:
       * in xmlparserv2.jar they define (META-INF\services
       *  
       *  java.xml.parsers.DocumentBuilderFactory
       *  java.xml.parsers.SAXParserFactory
       *  java.xml.transform.TransformerFactory
       *  org.w3c.dom.DOMImplementationSourceList
       *  
       * to be replaced by their own buggy implementations.
       * 
       * Unfortunately they are also called internally ...
       * 
       * The easiest way to avoid this, is by defining one's own
       * application-wide standard using the system properties.
       */
      try
      {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        TransformerFactory tf = TransformerFactory.newInstance();
        DOMImplementationRegistry dir = DOMImplementationRegistry.newInstance();
        
        System.out.println(dbf.getClass().getName());
        System.out.println(spf.getClass().getName());
        System.out.println(tf.getClass().getName());
        System.out.println(dir.getDOMImplementation("XML 1.0").getClass().getName());
      }
      catch(Exception e) { fail(EU.getExceptionMessage(e)); }
      /* without xmlparserv2 in the class path we get:
       * com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
       * com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl
       * com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl
       * com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl
       * 
       * with xmlparsv2 in the class path we have:
       * oracle.xml.jaxp.JXDocumentBuilderFactory
       * oracle.xml.jaxp.JXSAXParserFactory
       * oracle.xml.jaxp.JXSAXTransformerFactory
       * oracle.xml.parser.v2.XMLDOMImplementation
       *
       * The problem is: some of those are called internally.
       */
      System.out.println("Registered DocumentsBuilderFactory providers:");
      ServiceLoader<DocumentBuilderFactory> sldbf = ServiceLoader.load(DocumentBuilderFactory.class);
      for (Iterator<DocumentBuilderFactory> iterDbf = sldbf.iterator(); iterDbf.hasNext(); )
      {
        DocumentBuilderFactory dbf1 = iterDbf.next();
        System.out.println("  "+dbf1.getClass().getName());
      }
      System.out.println("Registered SAXParserFactory providers:");
      ServiceLoader<SAXParserFactory> slspf = ServiceLoader.load(SAXParserFactory.class);
      for (Iterator<SAXParserFactory> iterSpf = slspf.iterator(); iterSpf.hasNext(); )
      {
        SAXParserFactory spf1 = iterSpf.next();
        System.out.println("  "+spf1.getClass().getName());
      }
      System.out.println("Registered TransformerFactory providers:");
      ServiceLoader<TransformerFactory> sltf = ServiceLoader.load(TransformerFactory.class);
      for (Iterator<TransformerFactory> iterTf = sltf.iterator(); iterTf.hasNext(); )
      {
        TransformerFactory tf1 = iterTf.next();
        System.out.println("  "+tf1.getClass().getName());
      }
      /* unfortunately the stupid SourceList cannot be enumerated using the ServiceLoader
      System.out.println("Registered DOMImplementationSource providers:");
      ServiceLoader<DOMImplementationSource> sldis = ServiceLoader.load("org.w3c.dom.DOMImplementationSourceList");
      for (Iterator<DOMImplementationSource> iterDis = sldis.iterator(); iterDis.hasNext(); )
      {
        DOMImplementationSource dis = iterDis.next();
        System.out.println("  "+dis.getClass().getName());
      }
      */
      
      /* create transformer for XSL */
      StreamSource ssXsl = new StreamSource(new File("etc/metadata.xsl"));
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer xformer = tf.newTransformer(ssXsl);
  
      /* export metadata.xml from sample.siard */
      Archive archive = ArchiveImpl.newInstance();
      archive.open(new File("testfiles/sample.siard"));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      archive.exportMetaData(baos);
      baos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      StreamSource ssXml = new StreamSource(bais);
      
      /* create output stream for HTML */
      StreamResult srHtml = new StreamResult(new File("logs/metadata.html"));
      xformer.transform(ssXml, srHtml);
    }
    catch (TransformerConfigurationException tce) { fail(EU.getExceptionMessage(tce)); }
    catch (IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch (TransformerException te) { fail(EU.getExceptionMessage(te)); }
  }

}
