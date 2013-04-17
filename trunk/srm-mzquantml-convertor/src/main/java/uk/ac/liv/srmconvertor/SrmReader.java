/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.srmconvertor;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 07-Feb-2013 13:10:39
 */
public class SrmReader implements Closeable {

    private BufferedReader br;
    /*
     * PeptideSequence (== peptide)
     * ProteinName (== protein)
     * ReplicateName (== raw file (+".raw")
     * PrecursorMz
     * PrecursorCharge
     * ProductMz
     * ProductCharge
     * FragmentIon
     * RetentionTime
     * Area (== Q3 m/z)
     * Background
     * PeakRank
     * IsotopeLabelType (ReplicateName + "_" + IsotopeLabelType = assay name)
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
    private int IsotopeLabelTypePos = 0;
    private int PeptideRatioPos = 0;
    //ArrayList variables
//    private ArrayList<String> assayList;
//    private ArrayList<String> proteinList;
//    private ArrayList<String> peptideList;
    //hashmap one to one variables
    private LinkedHashMap<String, String> PeptideSequenceMap = new LinkedHashMap();
    private LinkedHashMap<String, String> ProteinNameMap = new LinkedHashMap();
    private LinkedHashMap<String, String> ReplicateNameMap = new LinkedHashMap();
    private LinkedHashMap<String, String> PrecursorMzMap = new LinkedHashMap();
    private LinkedHashMap<String, String> PrecursorChargeMap = new LinkedHashMap();
    private LinkedHashMap<String, String> ProductMzMap = new LinkedHashMap();
    private LinkedHashMap<String, String> ProductChargeMap = new LinkedHashMap();
    private LinkedHashMap<String, String> FragmentIonMap = new LinkedHashMap();
    private LinkedHashMap<String, String> RetentionTimeMap = new LinkedHashMap();
    private LinkedHashMap<String, String> AreaMap = new LinkedHashMap();
    private LinkedHashMap<String, String> BackgroundMap = new LinkedHashMap();
    private LinkedHashMap<String, String> PeakRankMap = new LinkedHashMap();
    private LinkedHashMap<String, String> IsotopeLabelTypeMap = new LinkedHashMap();
    private LinkedHashMap<String, String> AssayMap = new LinkedHashMap();
    //the orginal data from csv file, index-->ratio
    private LinkedHashMap<String, String> PeptideRatioMap = new LinkedHashMap();
    //derived hashmap one to many variables
    private LinkedHashMap<String, ArrayList<String>> proteinIdMap;
    private LinkedHashMap<String, ArrayList<String>> peptideIdMap;
    /*
     * @key assay name
     * @value list of ids
     */
    private LinkedHashMap<String, ArrayList<String>> assayIdMap;
    private LinkedHashMap<String, ArrayList<String>> replicateIdMap;
    //derived from the above four IdMap
    private LinkedHashMap<String, ArrayList<String>> proteinToPeptideMap;
    private LinkedHashMap<String, ArrayList<String>> peptideToProteinMap;
    private LinkedHashMap<String, ArrayList<String>> proteinToAssayMap;
    private LinkedHashMap<String, ArrayList<String>> assayToProteinMap;
    private LinkedHashMap<String, ArrayList<String>> peptideToAssayMap;
    private LinkedHashMap<String, ArrayList<String>> assayToPeptideMap;
    private LinkedHashMap<String, ArrayList<String>> replicateToAssayMap;
    private LinkedHashMap<String, ArrayList<String>> assayToReplicateMap;
    private LinkedHashMap<String, String> peptideToRatioMap;

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
            else if (nextLine[i].equals("IsotopeLabelType")) {
                IsotopeLabelTypePos = i;
            }
            else if (nextLine[i].equals("RatioToStandard")) {
                PeptideRatioPos = i;
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
            IsotopeLabelTypeMap.put(Integer.toString(index), nextLine[IsotopeLabelTypePos]);
            AssayMap.put(Integer.toString(index), nextLine[ReplicateNamePos] + "_" + nextLine[IsotopeLabelTypePos]);
            PeptideRatioMap.put(Integer.toString(index), nextLine[PeptideRatioPos]);

            index++;
        }

        reader.close();

        //create assayIdMap
        createAssayIdMap();

        //create proteinIdMap
        createProteinIdMap();

        //create peptideIdMap
        createPeptideIdMap();

        //create replicateIdMap, replicate name represents raw file name
        createReplicateIdMap();
    }

    //public methods
    public ArrayList<String> getAssayList() {
        return new ArrayList(assayIdMap.keySet());
    }

    public ArrayList<String> getProteinList() {
        return new ArrayList(proteinIdMap.keySet());
    }

    public ArrayList<String> getPeptideList() {
        return new ArrayList(peptideIdMap.keySet());
    }

    public ArrayList<String> getReplicateList() {
        return new ArrayList(replicateIdMap.keySet());
    }

    public LinkedHashMap<String, String> getPeptideMap() {
        return PeptideSequenceMap;
    }

    public LinkedHashMap<String, String> getProteinMap() {
        return ProteinNameMap;
    }

    public LinkedHashMap<String, String> getReplicateMap() {
        return ReplicateNameMap;
    }

    public LinkedHashMap<String, String> getPrecursorMzMap() {
        return PrecursorMzMap;
    }

    public LinkedHashMap<String, String> getPrecursorChargeMap() {
        return PrecursorChargeMap;
    }

    public LinkedHashMap<String, String> getProductMzMap() {
        return ProductMzMap;
    }

    public LinkedHashMap<String, String> getProductChargeMap() {
        return ProductChargeMap;
    }

    public LinkedHashMap<String, String> getFragmentIonMap() {
        return FragmentIonMap;
    }

    public LinkedHashMap<String, String> getRetentionTimeMap() {
        return RetentionTimeMap;
    }

    public LinkedHashMap<String, String> getAreaMap() {
        return AreaMap;
    }

    public LinkedHashMap<String, String> getBackgroundMap() {
        return BackgroundMap;
    }

    public LinkedHashMap<String, String> getPeakRankMap() {
        return PeakRankMap;
    }

    public LinkedHashMap<String, String> getAssayMap() {
        return AssayMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getAssayIdMap() {
        return assayIdMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getProteinIdMap() {
        return proteinIdMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getPeptideIdMap() {
        return peptideIdMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getAssayToPeptideMap() {
        createAssayToPeptideMap();
        return assayToPeptideMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getPeptideToAssayMap() {
        createPeptideToAssayMap();
        return peptideToAssayMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getAssayToProteinMap() {
        createAssayToProteinMap();
        return assayToProteinMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getProteinToAssayMap() {
        createProteinToAssayMap();
        return proteinToAssayMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getPeptideToProteinMap() {
        createPeptideToProteinMap();
        return peptideToProteinMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getProteinToPeptideMap() {
        createProteinToPeptideMap();
        return proteinToPeptideMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getAssayToReplicateMap() {
        createAssayToReplicateMap();
        return assayToReplicateMap;
    }

    public LinkedHashMap<String, ArrayList<String>> getReplicateToAssayMap() {
        createReplicateToAssayMap();
        return replicateToAssayMap;
    }

    public LinkedHashMap<String, String> getPeptideToRatioMap() {
        createPeptideToRatioMap();
        return peptideToRatioMap;
    }
    //private method

    private void createAssayIdMap() {
        assayIdMap = new LinkedHashMap();
        //ArrayList<String> assayList = new ArrayList();
        for (String id : AssayMap.keySet()) {
            String assay = AssayMap.get(id);
            ArrayList<String> idList = assayIdMap.get(assay);
            if (idList == null) {
                idList = new ArrayList();
                assayIdMap.put(assay, idList);
            }
            idList.add(id);
        }
    }

    private void createProteinIdMap() {
        proteinIdMap = new LinkedHashMap();
        //ArrayList<String> proteinList = new ArrayList();
        for (String id : ProteinNameMap.keySet()) {
            String protein = ProteinNameMap.get(id);
            ArrayList<String> idList = proteinIdMap.get(protein);
            if (idList == null) {
                idList = new ArrayList();
                proteinIdMap.put(protein, idList);
            }
            idList.add(id);
        }
    }

    private void createPeptideIdMap() {
        peptideIdMap = new LinkedHashMap();
        //ArrayList<String> peptideList = new ArrayList();
        for (String id : PeptideSequenceMap.keySet()) {
            String peptide = PeptideSequenceMap.get(id);
            ArrayList<String> idList = peptideIdMap.get(peptide);
            if (idList == null) {
                idList = new ArrayList();
                peptideIdMap.put(peptide, idList);
            }
            idList.add(id);
        }
    }

    private void createReplicateIdMap() {
        replicateIdMap = new LinkedHashMap();
        //ArrayList<String> replicateList = new ArrayList();
        for (String id : ReplicateNameMap.keySet()) {
            String replicate = ReplicateNameMap.get(id);
            ArrayList<String> idList = replicateIdMap.get(replicate);
            if (idList == null) {
                idList = new ArrayList();
                replicateIdMap.put(replicate, idList);
            }
            idList.add(id);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> proteinToPeptideMap
     * @key = protein name
     * @value = list of peptide sequence
     */
    private void createProteinToPeptideMap() {
        proteinToPeptideMap = new LinkedHashMap();
        for (String protein : proteinIdMap.keySet()) {
            ArrayList<String> idList = proteinIdMap.get(protein);

            // get peptideList from PeptideSequenceMap based on idList
            ArrayList<String> peptideList = getListFromId(idList, PeptideSequenceMap);

            proteinToPeptideMap.put(protein, peptideList);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> peptideToProteinMap
     * @key = peptide sequence
     * @value = list of protein name
     */
    private void createPeptideToProteinMap() {
        peptideToProteinMap = new LinkedHashMap();
        for (String peptide : peptideIdMap.keySet()) {
            ArrayList<String> idList = peptideIdMap.get(peptide);

            // get proteinList from ProteinNameMap based on idList
            ArrayList<String> proteinList = getListFromId(idList, ProteinNameMap);

            peptideToProteinMap.put(peptide, proteinList);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> peptideToAssayMap
     * @key = peptide sequence
     * @value = list of assay name
     */
    private void createPeptideToAssayMap() {
        peptideToAssayMap = new LinkedHashMap();
        for (String peptide : peptideIdMap.keySet()) {
            ArrayList<String> idList = peptideIdMap.get(peptide);

            // get assayList from AssayMap based on idList
            ArrayList<String> assayList = getListFromId(idList, AssayMap);

            peptideToAssayMap.put(peptide, assayList);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> assayToPeptideMap
     * @key = assay name
     * @value = list of peptide sequence
     */
    private void createAssayToPeptideMap() {
        assayToPeptideMap = new LinkedHashMap();
        for (String assay : assayIdMap.keySet()) {
            ArrayList<String> idList = assayIdMap.get(assay);

            // get peptideList from PeptideSequenceMap based on idList
            ArrayList<String> peptideList = getListFromId(idList, PeptideSequenceMap);

            assayToPeptideMap.put(assay, peptideList);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> proteinToAssayMap
     * @key = peptide sequence
     * @value = list of assay name
     */
    private void createProteinToAssayMap() {
        proteinToAssayMap = new LinkedHashMap();
        for (String protein : proteinIdMap.keySet()) {
            ArrayList<String> idList = proteinIdMap.get(protein);

            // get assayList from ProteinNameMap based on idList
            ArrayList<String> assayList = getListFromId(idList, AssayMap);

            peptideToAssayMap.put(protein, assayList);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> assayToProteinMap
     * @key = assay name
     * @value = list of protein name
     */
    private void createAssayToProteinMap() {
        assayToProteinMap = new LinkedHashMap();
        for (String assay : assayIdMap.keySet()) {
            ArrayList<String> idList = assayIdMap.get(assay);

            // get proteinList from ProteinNameMap based on idList
            ArrayList<String> proteinList = getListFromId(idList, ProteinNameMap);

            assayToProteinMap.put(assay, proteinList);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> replicateToAssayMap
     * @key = replicate name
     * @value = list of assay name
     */
    private void createReplicateToAssayMap() {
        replicateToAssayMap = new LinkedHashMap();
        for (String replicate : replicateIdMap.keySet()) {
            ArrayList<String> idList = replicateIdMap.get(replicate);

            // get assayList from ReplicateNameMap based on idList
            ArrayList<String> assayList = getListFromId(idList, AssayMap);

            replicateToAssayMap.put(replicate, assayList);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> assayToReplicateMap
     * @key = assay name
     * @value = list of replicate name
     */
    private void createAssayToReplicateMap() {
        assayToReplicateMap = new LinkedHashMap();
        for (String assay : assayIdMap.keySet()) {
            ArrayList<String> idList = assayIdMap.get(assay);

            // get replicateList from ProteinNameMap based on idList
            ArrayList<String> replicateList = getListFromId(idList, ReplicateNameMap);

            assayToReplicateMap.put(assay, replicateList);
        }
    }

    /*
     * @HashMap<String,String> peptideToRatioMap
     * @key = peptide sequence
     * @value = peptide ratio
     */
    private void createPeptideToRatioMap() {
        peptideToRatioMap = new LinkedHashMap();
        for (String id : PeptideRatioMap.keySet()) {
            String pepSeq = PeptideSequenceMap.get(id);
            String ratio = peptideToRatioMap.get(pepSeq);
            if (ratio == null) {
                peptideToRatioMap.put(pepSeq, PeptideRatioMap.get(id));
            }
        }
    }

    private ArrayList<String> getListFromId(ArrayList<String> idList,
                                            HashMap<String, String> aMap) {
        ArrayList<String> resList = new ArrayList();
        for (String id : idList) {
            resList.add(aMap.get(id));
        }
        return resList;

    }

    @Override
    public void close()
            throws IOException {
        br.close();
    }

}
