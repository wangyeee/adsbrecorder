package adsbrecorder.service.impl;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adsbrecorder.entity.MilitaryCallsign;
import adsbrecorder.repo.MilitaryCallsignRepository;
import adsbrecorder.service.MilitaryCallsignService;

@Service
public class MilitaryCallsignServiceImpl implements MilitaryCallsignService, RecordConsumer {

    private MilitaryCallsignRepository repo;

    @Autowired
    public MilitaryCallsignServiceImpl(MilitaryCallsignRepository repo) {
        this();
        this.repo = repo;
    }

    @Override
    public MilitaryCallsign getRecordByCallsign(String callsign) {
        Optional<MilitaryCallsign> o = repo.findByCallsign(callsign);
        if (o.isPresent())
            return o.get();
        return MilitaryCallsign.emptyRecord();
    }

    private boolean isMilitaryCallsignDataExist() {
        return repo.count() > 0L;
    }

    private File downloadFile(String url) throws IOException {
        File tmp = File.createTempFile("MIL_", ".pdf");
        FileOutputStream out = new FileOutputStream(tmp);
        ReadableByteChannel inChannel = Channels.newChannel(new URL(url).openStream());
        out.getChannel().transferFrom(inChannel, 0, Long.MAX_VALUE);
        out.flush();
        out.close();
        inChannel.close();
        return tmp;
    }

    @Override
    public synchronized void loadMilitaryCallsignData() {
        File pdf = null;
        PDDocument document = null;
        if (isMilitaryCallsignDataExist())
            return;
        try {
            MilCallDataParser parser = new MilCallDataParser(this);
            pdf = downloadFile(PDF_URL);
            PDFTextStripper stripper = PositionTextStripper.newInstance(parser);
            document = PDDocument.load(pdf);
            stripper.setSortByPosition(true);
            stripper.setStartPage(0);
            stripper.setEndPage(document.getNumberOfPages());
            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);
            dummy.close();
            if (curr > 0) {
                repo.saveAll(Arrays.asList(Arrays.copyOf(batch, curr)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (document != null)
                    document.close();
                if (pdf != null && pdf.exists())
                    pdf.delete();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private final int batchSize = 1000;
    private MilitaryCallsign[] batch;
    private int curr;

    protected MilitaryCallsignServiceImpl() {
        curr = 0;
        batch = new MilitaryCallsign[batchSize];
    }

    @Override
    public void newRecord(MilitaryCallsign record) {
        if (curr < batchSize) {
            batch[curr] = record;
            curr++;
        } else {
            repo.saveAll(Arrays.asList(batch));
            curr = 0;
        }
    }
}

class PositionTextStripper extends PDFTextStripper {

    private MilCallDataParser parser;

    public PositionTextStripper() throws IOException {
        super();
    }

    public static PositionTextStripper newInstance(MilCallDataParser parser) {
        try {
            PositionTextStripper s = new PositionTextStripper();
            s.parser = requireNonNull(parser);
            return s;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        StringBuilder line = new StringBuilder();
        for (TextPosition text : textPositions) {
            line.append(text.getUnicode());
        }
        parser.addNewLine(line.toString());
    }
}

interface RecordConsumer {
    void newRecord(MilitaryCallsign record);
}

class MilCallDataParser {

    public static final String DATE_PREFIX = "maandag";
    public static final String PAGE_PREFIX = "Pagina";

    public static final String MILITARY_CALLSIGNS = "Military Callsigns";
    public static final String COUNTRY = "Country";
    public static final String CALLSIGN = "Callsign";
    public static final String TYPE = "Type";
    public static final String UNIT = "Unit";

    private String currentCountry;

    private String callsign;
    private String type;
    private String unit;

    private boolean setCountry;

    private int step;

    private RecordConsumer dest;

    public MilCallDataParser(RecordConsumer dest) throws IOException {
        setCountry = false;
        step = 0;
        this.dest = requireNonNull(dest);
    }

    public void addNewLine(String line) {
        if (MILITARY_CALLSIGNS.equalsIgnoreCase(line))
            return;
        if (COUNTRY.equalsIgnoreCase(line)) {
            setCountry = true;
            return;
        }
        if (setCountry) {
            if (CALLSIGN.equalsIgnoreCase(line)) {
                setCountry = false;
            } else {
                currentCountry = line;
            }
            return;
        }
        if (TYPE.equalsIgnoreCase(line) || UNIT.equalsIgnoreCase(line))
            return;
        if (line.startsWith(DATE_PREFIX) || line.startsWith(PAGE_PREFIX))
            return;
        switch (step) {
        case 0:
            callsign = line;
            step++;
            break;
        case 1:
            type = line;
            step++;
            break;
        case 2:
            unit = line;
            step = 0;
            MilitaryCallsign c = new MilitaryCallsign();
            c.setCallsign(callsign);
            c.setCountry(currentCountry);
            c.setType(type);
            c.setUnit(unit);
            dest.newRecord(c);;
            break;
        default:
            throw new RuntimeException("Invalid step: " + step);
        }
    }
}
