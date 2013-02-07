/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.reader;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 07-Feb-2013 13:10:39
 */
public class SrmReader implements Closeable {

    private BufferedReader br;
    /*
     * PeptideSequence
     * ProteinName
     * ReplicateName
     * PrecursorMz
     * PrecursorCharge
     * ProductMz
     * ProductCharge
     * FragmentIon
     * RetentionTime
     * Area
     * Background
     * PeakRank
     *
     */
    //position variables
    private int PeptideSequencePos = 0;
    private int ProteinNamePos = 0;
    private int ReplicateNamePos = 0;
    private int PrecursorMzPos = 0;
    private int PrecursorChargePos = 0;
    private int ProductMzPos = 0;
    private int ProductChargePos = 0;
    private int FragmentIonPos = 0;
    private int RetentionTimePos = 0;
    private int AreaPos = 0;
    private int BackgroundPos = 0;
    private int PeakRankPos = 0;
    //hashmap variables
    private HashMap<String, String> PeptideSequenceMap = new HashMap();
    private HashMap<String, String> ProteinNameMap = new HashMap();
    private HashMap<String, String> ReplicateNameMap = new HashMap();
    private HashMap<String, String> PrecursorMzMap = new HashMap();
    private HashMap<String, String> PrecursorChargeMap = new HashMap();
    private HashMap<String, String> ProductMzMap = new HashMap();
    private HashMap<String, String> ProductChargeMap = new HashMap();
    private HashMap<String, String> FragmentIonMap = new HashMap();
    private HashMap<String, String> RetentionTimeMap = new HashMap();
    private HashMap<String, String> AreaMap = new HashMap();
    private HashMap<String, String> BackgroundMap = new HashMap();
    private HashMap<String, String> PeakRankMap = new HashMap();

    ////////// ////////// ////////// ////////// //////////
    //Constrctor
    public SrmReader(Reader rd)
            throws IOException {
        br = new BufferedReader(rd);

        CSVReader reader = new CSVReader(br);
        String nextLine[];

        // process the title row
        nextLine = reader.readNext();

        for (int i = 0; i < nextLine.length; i++) {
            if (nextLine[i].equals("PeptideSequence")) {
                PeptideSequencePos = i;
            }
            else if (nextLine[i].equals("ProteinName")) {
                ProteinNamePos = i;
            }
            else if (nextLine[i].equals("ReplicateName")) {
                ReplicateNamePos = i;
            }
            else if (nextLine[i].equals("PrecursorMz")) {
                PrecursorMzPos = i;
            }
            else if (nextLine[i].equals("PrecursorCharge")) {
                PrecursorChargePos = i;
            }
            else if (nextLine[i].equals("ProductMz")) {
                ProductMzPos = i;
            }
            else if (nextLine[i].equals("ProductCharge")) {
                ProductChargePos = i;
            }
            else if (nextLine[i].equals("FragmentIon")) {
                FragmentIonPos = i;
            }
            else if (nextLine[i].equals("RetentionTime")) {
                RetentionTimePos = i;
            }
            else if (nextLine[i].equals("Area")) {
                AreaPos = i;
            }
            else if (nextLine[i].equals("Background")) {
                BackgroundPos = i;
            }
            else if (nextLine[i].equals("PeakRank")) {
                PeakRankPos = i;
            }
        }

        // read the data into hashmap
        // key = index
        int index = 0;
        while ((nextLine = reader.readNext()) != null) {
            PeptideSequenceMap.put(Integer.toString(index), nextLine[PeptideSequencePos]);
            ProteinNameMap.put(Integer.toString(index), nextLine[ProteinNamePos]);
            ReplicateNameMap.put(Integer.toString(index), nextLine[ReplicateNamePos]);
            PrecursorMzMap.put(Integer.toString(index), nextLine[PrecursorMzPos]);
            PrecursorChargeMap.put(Integer.toString(index), nextLine[PrecursorChargePos]);
            ProductMzMap.put(Integer.toString(index), nextLine[ProductMzPos]);
            ProductChargeMap.put(Integer.toString(index), nextLine[ProductChargePos]);
            FragmentIonMap.put(Integer.toString(index), nextLine[FragmentIonPos]);
            RetentionTimeMap.put(Integer.toString(index), nextLine[RetentionTimePos]);
            AreaMap.put(Integer.toString(index), nextLine[AreaPos]);
            BackgroundMap.put(Integer.toString(index), nextLine[BackgroundPos]);
            PeakRankMap.put(Integer.toString(index), nextLine[PeakRankPos]);

            index++;
        }

        reader.close();
    }

    //public methods
    public ArrayList<String> getAssayList() {
        ArrayList<String> assayList = new ArrayList();
        for (String assay : ReplicateNameMap.values()) {
            if (!assayList.contains(assay)) {
                assayList.add(assay);
            }
        }
        return assayList;
    }

    public ArrayList<String> getProteinList() {
        ArrayList<String> proteinList = new ArrayList();
        for (String protein : ProteinNameMap.values()) {
            if (!proteinList.contains(protein)) {
                proteinList.add(protein);
            }
        }
        return proteinList;
    }

    public ArrayList<String> getPeptideList() {
        ArrayList<String> peptideList = new ArrayList();
        for (String peptide : PeptideSequenceMap.values()) {
            if (!peptideList.contains(peptide)) {
                peptideList.add(peptide);
            }
        }
        return peptideList;
    }
    
    //private method
    

    @Override
    public void close()
            throws IOException {
        br.close();
    }

}
