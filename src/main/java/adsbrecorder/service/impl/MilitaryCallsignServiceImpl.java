package adsbrecorder.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adsbrecorder.entity.MilitaryCallsign;
import adsbrecorder.repo.MilitaryCallsignRepository;
import adsbrecorder.service.MilitaryCallsignService;

@Service
public class MilitaryCallsignServiceImpl implements MilitaryCallsignService {

    private MilitaryCallsignRepository repo;

    @Autowired
    public MilitaryCallsignServiceImpl(MilitaryCallsignRepository repo) {
        this.repo = repo;
    }

    @Override
    public MilitaryCallsign getRecordByCallsign(String callsign) {
        Optional<MilitaryCallsign> o = repo.findByCallsign(callsign);
        if (o.isPresent())
            return o.get();
        return MilitaryCallsign.emptyRecord();
    }

    @Override
    public void loadMilitaryCallsignData() {
        File txt = new File("MilitaryCallsigns.txt");
        File unsolved = new File("MilitaryCallsignsNew.txt");
        Scanner sn = null;
        PrintWriter out = null;
        final char SEPARATOR = ' ';
        final int BUF_SIZE = 1000;
        try {
            sn = new Scanner(txt);
            if (unsolved.exists())
                unsolved.delete();
            unsolved.createNewFile();
            out = new PrintWriter(unsolved);
            String line = null;
            String country = null;
            MilitaryCallsign[] calls = new MilitaryCallsign[BUF_SIZE];
            int i = 0;
            while (sn.hasNextLine()) {
                line = sn.nextLine().trim();
                if (line.charAt(0) == '#' || line.startsWith("maandag") || line.startsWith("Callsign"))
                    continue;
                if (line.startsWith("Country")) {
                    country = WordUtils.capitalizeFully(line.substring("Country".length()).trim());
                    continue;
                }
                String[] d = StringUtils.split(line, SEPARATOR);
                if (d.length == 3 || line.charAt(line.length() - 1) == '-' || line.contains("|")) {
                    MilitaryCallsign call = new MilitaryCallsign();
                    call.setCountry(country);
                    call.setCallsign(d[0]);
                    if (d.length == 3) {
                        call.setType(d[1]);
                        call.setUnit(d[2]);
                    } else if (line.charAt(line.length() - 1) == '-') {
                        StringBuilder sb = new StringBuilder();
                        for (int j = 1; j < d.length - 1; j++) {
                            sb.append(d[j]);
                            sb.append(SEPARATOR);
                        }
                        call.setType(sb.toString().substring(0, sb.length() - 1));
                        call.setUnit(d[d.length - 1]);
                    } else {
                        int s = search(d, "|");
                        if (s > 0) {
                            StringBuilder type = new StringBuilder();
                            for (int k = 1; k < s - 1; k++) {
                                type.append(d[k]);
                                type.append(' ');
                            }
                            StringBuilder unit = new StringBuilder();
                            for (int k = s - 1; k < d.length; k++) {
                                unit.append(d[k]);
                                unit.append(' ');
                            }
                            call.setType(type.toString().substring(0, type.length() - 1));
                            call.setUnit(unit.toString().substring(0, unit.length() - 1));
                        }
                    }
                    calls[i] = call;
                    i++;
                    if (i == BUF_SIZE) {
                        repo.saveAll(Arrays.asList(calls));
                        System.err.println("Save batch callsigns");
                        i = 0;
                    }
                } else {
                    out.println(String.format("%s\t%s", country, line.replaceFirst(" ", "\t")));
                }
            }
            if (i != 0) {
                repo.saveAll(Arrays.asList(Arrays.copyOf(calls, i)));
                System.err.println("Save final batch callsigns: " + i);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sn != null)
                sn.close();
            if (out != null)
                out.close();
        }
    }

    int search(String[] array, String chr) {
        for (int i = 0; i < array.length; i++) {
            String s = array[i];
            if (s.equals(chr))
                return i;
        }
        return -1;
    }
}
