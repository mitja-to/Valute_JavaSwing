/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.valuta;

import java.awt.HeadlessException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Mitja
 */
public class mainWindow {
    
  private static final String dbURL = "jdbc:derby:dbase/valute;create=true";        //JDBC niz, glavni niz za povezavo do podatkovne baze
  private static final String tableName = "VALUTA";                                 //Ime tabele
  private static Connection conn = null;                                            //Spremenljivka za povezavo na bazo
  private static Statement stmt = null;                                             //Spremenljivka za ustvarjanje poizvedb

  /*
    Naložimo gonilnik za podatkovno bazo
  */
  public void loadDriver() {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
      System.out.println("Loaded the appropriate driver");
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException except) {
    }
  }

  /*
    Ustvarimo povezavo na nastavljeno podatkovno bazo
  */
  public void createConnection() {
    try {
      conn = DriverManager.getConnection(dbURL, "mitja", "mitja");
      System.out.println("Connected to and created database ");
    } catch (Exception except) {
    }
  }
  
  /*
    Ustvarimo tabelo. Ob tem premerimo ali tabela že obstaja. Če že obstaja jo samo spraznimo. Lahko bi tudi preverili ali obstajajo novi podatki na spletu in glede na to
    ustvarili/dodajali podatke v tabelo...
  */
  public void createTable() throws SQLException
  {
    
    DatabaseMetaData dbmd = conn.getMetaData();
    ResultSet rs = dbmd.getTables(null, "MITJA", "VALUTA", null);
    if(!rs.next())
    {
        Statement st = conn.createStatement();
        st.execute("CREATE TABLE "
          + tableName
          + " (OZNAKA VARCHAR(5), SIFRA NUMERIC(3), TECAJ VARCHAR(10), DATUM DATE, ID INTEGER default 1 not null primary key)");
        System.out.println("Created table "+ tableName);
    }
    else
    {
        Statement st = conn.createStatement();
        st.execute("TRUNCATE TABLE "
          + tableName);
          
        System.out.println("Truncated table "+ tableName);
    }
    
  }
  
  /*
    Vstavljanje podatkov v tabelo
  */
  public void insertTable() throws SQLException
  {
      //Pridobivanje ter vstavljanje podatkov izvajamo v novi niti
      Runnable r = new Runnable() {
                    
                    //Med izvajanjem niti uporabniku prikazujemo progressbar
                    JProgressBar progressBar = new JProgressBar();
                    JFrame frame = new JFrame();
                    JPanel panel = new JPanel();

                    @Override
                    public void run() {
                        progressBar.setIndeterminate(true);
                        progressBar.setVisible(true);
                        progressBar.setStringPainted(true);
                        progressBar.setString("Nalagam podatke..");
                        frame.setUndecorated(true);
                        frame.setLocationRelativeTo(null);
                        frame.getContentPane().add(panel);
                        frame.setAlwaysOnTop(true);

                        panel.add(progressBar);
                        frame.pack();
                        frame.setEnabled(false);
                        frame.setVisible(true);

                        try {
                            
                            //S pomočjo SAXParserja beremo XML datoteko iz spleta
                            SAXParserFactory parserFactor = SAXParserFactory.newInstance();
                            SAXParser parser = parserFactor.newSAXParser();
                            MyContentHandler handler = new MyContentHandler();
                            parser.parse(new InputSource(new URL("http://www.bsi.si/_data/tecajnice/dtecbs-l.xml").openStream()), handler);
                            /*File file = new File("dtecbs-l.xml");
                            parser.parse(file, handler);*/
                            EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Valuta_jar_1.0-SNAPSHOTPU");
                            ValutaJpaController djc = new ValutaJpaController(emf);

                            //Vsak zapis v XML-u pretvorimo v objekt Valuta ter valuto dodamo v podatkovno bazo
                            try {
                                for (int i = 0; i < handler.listValut.size(); i++) {
                                    Valuta tmp = new Valuta();
                                    tmp.setId(handler.listValut.get(i).getId());
                                    tmp.setDatum(handler.listValut.get(i).getDatum());
                                    tmp.setOznaka(handler.listValut.get(i).getOznaka());
                                    tmp.setSifra(handler.listValut.get(i).getSifra());
                                    tmp.setTecaj(handler.listValut.get(i).getTecaj());
                                    djc.create(tmp); //Dodajanje objekta v bazo
                                }

                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, e.toString(), "InfoBox: " + "", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (ParserConfigurationException | SAXException | IOException | HeadlessException ex) {
                            Logger.getLogger(GlavnoOkno.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        //Ob končanju zapremo progressbar
                        progressBar.setIndeterminate(false);
                        progressBar.setString("Končano");
                        frame.setVisible(false);
                        GlavnoOkno go2 = new GlavnoOkno();
                        go2.setLocationRelativeTo(null);
                        go2.setVisible(true);

                    }
                };
                Thread t = new Thread(r);
                t.start();
  }
  
    
    public static void main(String[] args)
    {
        mainWindow mw = new mainWindow();
        mw.loadDriver();
        
        try{
            mw.createConnection();
            mw.createTable();
            mw.insertTable();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
}
