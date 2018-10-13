package adsbrecorder.service.impl;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import adsbrecorder.entity.Airline;
import adsbrecorder.service.AirlineService;

@Service
public class AirlineServiceImpl implements AirlineService {

    private CrudRepository<Airline, Long> airlineRepo;

    @Autowired
    public AirlineServiceImpl(CrudRepository<Airline, Long> airlineRepo) {
        this.airlineRepo = requireNonNull(airlineRepo);
    }

    @Override
    public void createDefaultAirline() {
        Airline dft = new Airline();
        dft.setAirlineID(DEFAULT_AIRLINE_ID);
        dft.setICAO(new String());
        dft.setIATA(new String());
        dft.setComments(new String());
        dft.setName(new String());
        dft.setCallSign(new String());
        dft.setCountry("ZZ");
        airlineRepo.save(dft);
    }

    /**
     * Import data from <a href="https://en.wikipedia.org/wiki/List_of_airline_codes">https://en.wikipedia.org/wiki/List_of_airline_codes</a>
     */
    @Override
    public void loadKnownAirlines() {
        List<Airline> airlines = new ArrayList<Airline>();
        InputStream xml = null;
        try {
            xml = this.getClass().getClassLoader().getResourceAsStream("air.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xml);

            // doc.getDocumentElement().normalize();  // not needed
            NodeList list = doc.getElementsByTagName("airline");
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Airline air = new Airline();
                    Element e = (Element) node;
                    air.setCallSign(e.getElementsByTagName("callsign").item(0).getTextContent());
                    air.setComments(e.getElementsByTagName("comments").item(0).getTextContent());
                    air.setCountry(e.getElementsByTagName("country").item(0).getTextContent());
                    air.setName(e.getElementsByTagName("name").item(0).getTextContent());
                    air.setIATA(e.getElementsByTagName("IATA").item(0).getTextContent());
                    air.setICAO(e.getElementsByTagName("ICAO").item(0).getTextContent());
                    airlines.add(air);
                }
            }
            System.out.println(list.getLength() + " records loaded.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (xml != null)
                    xml.close();
            } catch (IOException e) {
            }
        }
        airlineRepo.saveAll(airlines);
    }

    @Override
    public boolean checkDefaultAirline() {
        Optional<Airline> air = airlineRepo.findById(DEFAULT_AIRLINE_ID);
        if (air.isPresent())
            return false;
        return true;
    }

    @Override
    public boolean checkKnownAirlines() {
        long cnt = airlineRepo.count();
        return cnt < 2L;
    }

    @Override
    public List<Airline> findByIds(List<Long> ids) {
        return IteratorUtils.toList(airlineRepo.findAllById(ids).iterator(), ids.size());
    }
}
