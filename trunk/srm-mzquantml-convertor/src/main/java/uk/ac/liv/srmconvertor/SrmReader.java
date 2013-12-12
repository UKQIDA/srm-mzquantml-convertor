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
import java.util.Map;

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
     * ReplicateName (== raw file (+".raw") /TODO: duplicate with FileName?
     * ModifiedSequence
     * PrecursorMz
     * PrecursorCharge
     * ProductMz
     * ProductCharge
     * CleavageAa
     * FragmentIon
     * PeptideRetentionTime
     * RetentionTime
     * Area (== Q3 m/z)
     * Background
     * PeakRank
     * FileName (raw file name with .raw
     * IsotopeLabelType (ReplicateName + "_" + IsotopeLabelType = assay name)
     * Height
     * TotalAreaRatio
     * AreaNormalized
     */
    //existence of column
    private boolean hasPeptideSequence = false;
    private boolean hasProteinName = false;
    private boolean hasReplicateName = false;
    private boolean hasModificationSequence = false;
    private boolean hasPrecursorMz = false;
    private boolean hasPrecursorCharge = false;
    private boolean hasProductMz = false;
    private boolean hasProductCharge = false;
    private boolean hasCleavageAa = false;
    private boolean hasFragmentIon = false;
    private boolean hasPeptideRetentionTime = false;
    private boolean hasRetentionTime = false;
    private boolean hasArea = false;
    private boolean hasBackground = false;
    private boolean hasPeakRank = false;
    private boolean hasFileName = false;
    private boolean hasIsotopeLabelType = false;
    private boolean hasHeight = false;
    private boolean hasTotalAreaRatio = false;
    private boolean hasAreaNormalized = false;
    private boolean hasPeptideRatio = false;
    //position variables
    private int posPeptideSequence = 0;
    private int posProteinName = 0;
    private int posReplicateName = 0;
    private int posModificationSequence = 0;
    private int posPrecursorMz = 0;
    private int posPrecursorCharge = 0;
    private int posProductMz = 0;
    private int posProductCharge = 0;
    private int posCleavageAa = 0;
    private int posFragmentIon = 0;
    private int posPeptideRetentionTime = 0;
    private int posRetentionTime = 0;
    private int posArea = 0;
    private int posBackground = 0;
    private int posPeakRank = 0;
    private int posFileName = 0;
    private int posIsotopeLabelType = 0;
    private int posHeight = 0;
    private int posTotalAreaRatio = 0;
    private int posAreaNormalized = 0;
    // PeptideRatio is not in the mzQuantML report
    // this is just to show how to deal with non exist column
    private int posPeptideRatio = 0;
    /*
     * HashMap one to one variables
     */
    private HashMap<String, String> PeptideSequenceMap = new HashMap();
    private HashMap<String, String> ProteinNameMap = new HashMap();
    private HashMap<String, String> ReplicateNameMap = new HashMap();
    private HashMap<String, String> ModificationSequenceMap = new HashMap();
    private HashMap<String, String> PrecursorMzMap = new HashMap();
    private HashMap<String, String> PrecursorChargeMap = new HashMap();
    private HashMap<String, String> ProductMzMap = new HashMap();
    private HashMap<String, String> ProductChargeMap = new HashMap();
    private HashMap<String, String> CleavageAaMap = new HashMap();
    private HashMap<String, String> FragmentIonMap = new HashMap();
    private HashMap<String, String> PeptideRetentionTimeMap = new HashMap();
    private HashMap<String, String> RetentionTimeMap = new HashMap();
    private HashMap<String, String> AreaMap = new HashMap();
    private HashMap<String, String> BackgroundMap = new HashMap();
    private HashMap<String, String> PeakRankMap = new HashMap();
    private HashMap<String, String> FileNameMap = new HashMap();
    private HashMap<String, String> IsotopeLabelTypeMap = new HashMap();
    private HashMap<String, String> HeightMap = new HashMap();
    private HashMap<String, String> TotalAreaRatioMap = new HashMap();
    private HashMap<String, String> AreaNormalizedMap = new HashMap();
    private HashMap<String, String> AssayMap = new HashMap();
    //the orginal data from csv file, index-->ratio
    private HashMap<String, String> PeptideRatioMap = new HashMap();
    //derived hashmap one to many variables
    private HashMap<String, ArrayList<String>> proteinIdMap;
    private HashMap<String, ArrayList<String>> peptideIdMap;
    private HashMap<String, ArrayList<String>> assayIdMap;
    private HashMap<String, ArrayList<String>> replicateIdMap;
    private HashMap<String, ArrayList<String>> rawFileNameIdMap;
    //derived from the above IdMap(s)
    private HashMap<String, ArrayList<String>> proteinToPeptideMap;
    private HashMap<String, ArrayList<String>> peptideToProteinMap;
    private HashMap<String, ArrayList<String>> proteinToAssayMap;
    private HashMap<String, ArrayList<String>> assayToProteinMap;
    private HashMap<String, ArrayList<String>> peptideToAssayMap;
    private HashMap<String, ArrayList<String>> assayToPeptideMap;
    private HashMap<String, ArrayList<String>> replicateToAssayMap;
    private HashMap<String, ArrayList<String>> assayToReplicateMap;
    private HashMap<String, ArrayList<String>> rawFileNameToAssayMap;
    private HashMap<String, ArrayList<String>> assayToRawFileNameMap;
    private Map<String, String> peptideToRatioMap = new HashMap<String, String>();
    private Map<String, String> peptideToTotalAreaRatioMap = new HashMap<String, String>();

    ////////// ////////// ////////// ////////// //////////
    //Constrctor
    public SrmReader(Reader rd)
            throws IOException {
        br = new BufferedReader(rd);

        CSVReader reader = new CSVReader(br);
        String nextLine[];

        // process the title row to get positions for measurements 
        nextLine = reader.readNext();

        for (int i = 0; i < nextLine.length; i++) {
            if (nextLine[i].equals("PeptideSequence")) {
                posPeptideSequence = i;
                hasPeptideSequence = true;
            }
            else if (nextLine[i].equals("ProteinName")) {
                posProteinName = i;
                hasProteinName = true;
            }
            else if (nextLine[i].equals("ReplicateName")) {
                posReplicateName = i;
                hasReplicateName = true;
            }
            else if (nextLine[i].equals("ModificationSequence")) {
                posModificationSequence = i;
                hasModificationSequence = true;
            }
            else if (nextLine[i].equals("PrecursorMz")) {
                posPrecursorMz = i;
                hasPrecursorMz = true;
            }
            else if (nextLine[i].equals("PrecursorCharge")) {
                posPrecursorCharge = i;
                hasPrecursorCharge = true;
            }
            else if (nextLine[i].equals("ProductMz")) {
                posProductMz = i;
                hasProductMz = true;
            }
            else if (nextLine[i].equals("ProductCharge")) {
                posProductCharge = i;
                hasProductCharge = true;
            }
            else if (nextLine[i].equals("CleavageAa")) {
                posCleavageAa = i;
                hasCleavageAa = true;
            }
            else if (nextLine[i].equals("FragmentIon")) {
                posFragmentIon = i;
                hasFragmentIon = true;
            }
            else if (nextLine[i].equals("PeptideRetentionTime")) {
                posPeptideRetentionTime = i;
                hasPeptideRetentionTime = true;
            }
            else if (nextLine[i].equals("RetentionTime")) {
                posRetentionTime = i;
                hasRetentionTime = true;
            }
            else if (nextLine[i].equals("Area")) {
                posArea = i;
                hasArea = true;
            }
            else if (nextLine[i].equals("Background")) {
                posBackground = i;
                hasBackground = true;
            }
            else if (nextLine[i].equals("FileName")) {
                posFileName = i;
                hasFileName = true;
            }
            else if (nextLine[i].equals("PeakRank")) {
                posPeakRank = i;
                hasPeakRank = true;
            }
            else if (nextLine[i].equals("IsotopeLabelType")) {
                posIsotopeLabelType = i;
                hasIsotopeLabelType = true;
            }
            else if (nextLine[i].equals("Height")) {
                posHeight = i;
                hasHeight = true;
            }
            else if (nextLine[i].equals("TotalAreaRatio")) {
                posTotalAreaRatio = i;
                hasTotalAreaRatio = true;
            }
            else if (nextLine[i].equals("AreaNormalized")) {
                posAreaNormalized = i;
                hasAreaNormalized = true;
            }
            else if (nextLine[i].equals("RatioToStandard")) {
                posPeptideRatio = i;
                hasPeptideRatio = true;
            }
        }

        // read the data into hashmap
        // key = index
        int index = 0;
        while ((nextLine = reader.readNext()) != null) {

            if (hasPeptideSequence) {
                PeptideSequenceMap.put(Integer.toString(index), nextLine[posPeptideSequence]);
            }
            if (hasProteinName) {
                ProteinNameMap.put(Integer.toString(index), nextLine[posProteinName]);
            }
            if (hasReplicateName) {
                ReplicateNameMap.put(Integer.toString(index), nextLine[posReplicateName]);
            }
            if (hasModificationSequence) {
                ModificationSequenceMap.put(Integer.toString(index), nextLine[posModificationSequence]);
            }
            if (hasPrecursorMz) {
                PrecursorMzMap.put(Integer.toString(index), nextLine[posPrecursorMz]);
            }
            if (hasPrecursorCharge) {
                PrecursorChargeMap.put(Integer.toString(index), nextLine[posPrecursorCharge]);
            }
            if (hasProductMz) {
                ProductMzMap.put(Integer.toString(index), nextLine[posProductMz]);
            }
            if (hasProductCharge) {
                ProductChargeMap.put(Integer.toString(index), nextLine[posProductCharge]);
            }
            if (hasCleavageAa) {
                CleavageAaMap.put(Integer.toString(index), nextLine[posCleavageAa]);
            }
            if (hasFragmentIon) {
                FragmentIonMap.put(Integer.toString(index), nextLine[posFragmentIon]);
            }
            if (hasPeptideRetentionTime) {
                PeptideRetentionTimeMap.put(Integer.toString(index), nextLine[posPeptideRetentionTime]);
            }
            if (hasRetentionTime) {
                RetentionTimeMap.put(Integer.toString(index), nextLine[posRetentionTime]);
            }
            if (hasArea) {
                AreaMap.put(Integer.toString(index), nextLine[posArea]);
            }
            if (hasBackground) {
                BackgroundMap.put(Integer.toString(index), nextLine[posBackground]);
            }
            if (hasFileName) {
                FileNameMap.put(Integer.toString(index), nextLine[posFileName]);
            }
            if (hasPeakRank) {
                PeakRankMap.put(Integer.toString(index), nextLine[posPeakRank]);
            }
            if (hasIsotopeLabelType) {
                IsotopeLabelTypeMap.put(Integer.toString(index), nextLine[posIsotopeLabelType]);
            }
            if (hasHeight) {
                HeightMap.put(Integer.toString(index), nextLine[posHeight]);
            }
            if (hasTotalAreaRatio) {
                TotalAreaRatioMap.put(Integer.toString(index), nextLine[posTotalAreaRatio]);
            }
            if (hasAreaNormalized) {
                AreaNormalizedMap.put(Integer.toString(index), nextLine[posAreaNormalized]);
            }
            if (hasReplicateName && hasIsotopeLabelType) {
                AssayMap.put(Integer.toString(index), nextLine[posReplicateName] + "_" + nextLine[posIsotopeLabelType]);
            }
            else if (hasReplicateName && !hasIsotopeLabelType) {
                AssayMap.put(Integer.toString(index), nextLine[posReplicateName]);
            }
            if (hasPeptideRatio) {
                PeptideRatioMap.put(Integer.toString(index), nextLine[posPeptideRatio]);
            }

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

        //create rawFileNameIdMap
        createRawFileNameIdMap();

        if (allFalse()) {
            throw new IllegalStateException("File is broken.");
        }
    }

    //public methods
    public boolean hasPeptideSequence() {
        return hasPeptideSequence;
    }

    public boolean hasProteinName() {
        return hasProteinName;
    }

    public boolean hasReplicateName() {
        return hasReplicateName;
    }

    public boolean hasModificationSequence() {
        return hasModificationSequence;
    }

    public boolean hasPrecursorMz() {
        return hasPrecursorMz;
    }

    public boolean hasPrecursorCharge() {
        return hasPrecursorCharge;
    }

    public boolean hasProductMz() {
        return hasProductMz;
    }

    public boolean hasProductCharge() {
        return hasProductCharge;
    }

    public boolean hasCleavageAa() {
        return hasCleavageAa;
    }

    public boolean hasFragmentIon() {
        return hasFragmentIon;
    }

    public boolean hasPeptideRetentionTime() {
        return hasPeptideRetentionTime;
    }

    public boolean hasRetentionTime() {
        return hasRetentionTime;
    }

    public boolean hasArea() {
        return hasArea;
    }

    public boolean hasBackground() {
        return hasBackground;
    }

    public boolean hasPeakRank() {
        return hasPeakRank;
    }

    public boolean hasFileName() {
        return hasFileName;
    }

    public boolean hasIsotopeLabelType() {
        return hasIsotopeLabelType;
    }

    public boolean hasHeight() {
        return hasHeight;
    }

    public boolean hasTotalAreaRatio() {
        return hasTotalAreaRatio;
    }

    public boolean hasAreaNormalized() {
        return hasAreaNormalized;
    }

    public boolean hasPeptideRatio() {
        return hasPeptideRatio;
    }

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

    public ArrayList<String> getRawFileNameList() {
        return new ArrayList(rawFileNameIdMap.keySet());
    }

    /*
     * return private HashMap(s)
     */
    public HashMap<String, String> getPeptideSequenceMap() {
        return PeptideSequenceMap;
    }

    public HashMap<String, String> getProteinNameMap() {
        return ProteinNameMap;
    }

    public HashMap<String, String> getReplicateNameMap() {
        return ReplicateNameMap;
    }

    public HashMap<String, String> getModificationSequenceMap() {
        return ModificationSequenceMap;
    }

    public HashMap<String, String> getPrecursorMzMap() {
        return PrecursorMzMap;
    }

    public HashMap<String, String> getPrecursorChargeMap() {
        return PrecursorChargeMap;
    }

    public HashMap<String, String> getProductMzMap() {
        return ProductMzMap;
    }

    public HashMap<String, String> getProductChargeMap() {
        return ProductChargeMap;
    }

    public HashMap<String, String> getCleavageAaMap() {
        return CleavageAaMap;
    }

    public HashMap<String, String> getFragmentIonMap() {
        return FragmentIonMap;
    }

    public HashMap<String, String> getPeptideRetentionTimeMap() {
        return PeptideRetentionTimeMap;
    }

    public HashMap<String, String> getRetentionTimeMap() {
        return RetentionTimeMap;
    }

    public HashMap<String, String> getAreaMap() {
        return AreaMap;
    }

    public HashMap<String, String> getBackgroundMap() {
        return BackgroundMap;
    }

    public HashMap<String, String> getPeakRankMap() {
        return PeakRankMap;
    }

    public HashMap<String, String> getIsotopeLabelTypeMap() {
        return IsotopeLabelTypeMap;
    }

    public HashMap<String, String> getHeightMap() {
        return HeightMap;
    }

    public HashMap<String, String> getTotalAreaRatioMap() {
        return TotalAreaRatioMap;
    }

    public HashMap<String, String> getAreaNormalizedMap() {
        return AreaNormalizedMap;
    }

    public HashMap<String, String> getAssayMap() {
        return AssayMap;
    }

    public HashMap<String, String> getPeptideRatioMap() {
        return PeptideRatioMap;
    }

    /*
     * return private one to many id list HashMaps
     */
    public HashMap<String, ArrayList<String>> getAssayIdMap() {
        return assayIdMap;
    }

    public HashMap<String, ArrayList<String>> getProteinIdMap() {
        return proteinIdMap;
    }

    public HashMap<String, ArrayList<String>> getPeptideIdMap() {
        return peptideIdMap;
    }

    public HashMap<String, ArrayList<String>> getRawFileNameIdMap() {
        return rawFileNameIdMap;
    }

    /*
     * cross reference HashMap (one to many)
     */
    public HashMap<String, ArrayList<String>> getAssayToPeptideMap() {
        createAssayToPeptideMap();
        return assayToPeptideMap;
    }

    public HashMap<String, ArrayList<String>> getPeptideToAssayMap() {
        createPeptideToAssayMap();
        return peptideToAssayMap;
    }

    public HashMap<String, ArrayList<String>> getAssayToProteinMap() {
        createAssayToProteinMap();
        return assayToProteinMap;
    }

    public HashMap<String, ArrayList<String>> getProteinToAssayMap() {
        createProteinToAssayMap();
        return proteinToAssayMap;
    }

    public HashMap<String, ArrayList<String>> getPeptideToProteinMap() {
        createPeptideToProteinMap();
        return peptideToProteinMap;
    }

    public HashMap<String, ArrayList<String>> getProteinToPeptideMap() {
        createProteinToPeptideMap();
        return proteinToPeptideMap;
    }

    public HashMap<String, ArrayList<String>> getAssayToReplicateMap() {
        createAssayToReplicateMap();
        return assayToReplicateMap;
    }

    public HashMap<String, ArrayList<String>> getReplicateToAssayMap() {
        createReplicateToAssayMap();
        return replicateToAssayMap;
    }

    public HashMap<String, ArrayList<String>> getRawFileNameToAssayMap() {
        createRawFileNameToAssayMap();
        return rawFileNameToAssayMap;
    }

    public HashMap<String, ArrayList<String>> getAssayToRawFileNameMap() {
        createAssayToRawFileNameMap();
        return assayToRawFileNameMap;
    }

    public Map<String, String> getPeptideToTotalAreaRatioMap() {
        createPeptideToTotalAreaRatioMap();
        return peptideToTotalAreaRatioMap;
    }

    public Map<String, String> getPeptideToRatioMap() {
        createPeptideToRatioMap();
        return peptideToRatioMap;
    }

    public boolean isLabelled() {
        boolean ret = false;
        for (String assN : AssayMap.keySet()) {
            if (assN.contains("heavy")) {
                ret = true;
            }
        }
        return ret;
    }

    /*
     * ******************
     *
     * private methods
     *
     * ******************
     */
    /*
     * @assayIdMap: assay name to id list HashMap
     */
    private void createAssayIdMap() {
        assayIdMap = new HashMap();
        //ArrayList<String> assayList = new ArrayList();
        if (AssayMap.keySet() != null) {
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
    }

    /*
     * @proteinIdMap: protein name to id list HashMap
     */
    private void createProteinIdMap() {
        proteinIdMap = new HashMap();
        //ArrayList<String> proteinList = new ArrayList();
        if (ProteinNameMap.keySet() != null) {
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
    }

    /*
     * @peptideIdMap: peptide sequence to id list HashMap
     */
    private void createPeptideIdMap() {
        peptideIdMap = new HashMap();
        //ArrayList<String> peptideList = new ArrayList();
        if (PeptideSequenceMap.keySet() != null) {
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
    }

    private void createReplicateIdMap() {
        replicateIdMap = new HashMap();
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
     * @rawFileNameIdMap: raw file name (.raw) to id list HashMap
     */
    private void createRawFileNameIdMap() {
        rawFileNameIdMap = new HashMap();
        if (FileNameMap.keySet() != null) {
            for (String id : FileNameMap.keySet()) {
                String rawFileName = FileNameMap.get(id);
                ArrayList<String> idList = rawFileNameIdMap.get(rawFileName);
                if (idList == null) {
                    idList = new ArrayList();
                    rawFileNameIdMap.put(rawFileName, idList);
                }
                idList.add(id);
            }
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> proteinToPeptideMap
     * @key = protein name
     * @value = list of peptide sequence
     */
    private void createProteinToPeptideMap() {
        proteinToPeptideMap = new HashMap();
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
        peptideToProteinMap = new HashMap();
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
        peptideToAssayMap = new HashMap();
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
        assayToPeptideMap = new HashMap();
        for (String assay : assayIdMap.keySet()) {
            ArrayList<String> idList = assayIdMap.get(assay);

            // get peptideSequenceList from PeptideSequenceMap based on idList
            ArrayList<String> peptideSequenceList = getListFromId(idList, PeptideSequenceMap);

            assayToPeptideMap.put(assay, peptideSequenceList);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> proteinToAssayMap
     * @key = peptide sequence
     * @value = list of assay name
     */
    private void createProteinToAssayMap() {
        proteinToAssayMap = new HashMap();
        for (String protein : proteinIdMap.keySet()) {
            ArrayList<String> idList = proteinIdMap.get(protein);

            // get assayList from AssayMap based on idList
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
        assayToProteinMap = new HashMap();
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
        replicateToAssayMap = new HashMap();
        for (String replicate : replicateIdMap.keySet()) {
            ArrayList<String> idList = replicateIdMap.get(replicate);

            // get assayList from AssayMap based on idList
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
        assayToReplicateMap = new HashMap();
        for (String assay : assayIdMap.keySet()) {
            ArrayList<String> idList = assayIdMap.get(assay);

            // get replicateList from ReplicateNameMap based on idList
            ArrayList<String> replicateList = getListFromId(idList, ReplicateNameMap);

            assayToReplicateMap.put(assay, replicateList);
        }
    }

    /*
     * @HashMap<String, ArrayList<String>> rawFileNameToAssayMap
     * @key = raw file name
     * @value = list of assay name
     */
    private void createRawFileNameToAssayMap() {
        rawFileNameToAssayMap = new HashMap();
        for (String rawFn : rawFileNameIdMap.keySet()) {
            ArrayList<String> idList = rawFileNameIdMap.get(rawFn);

            // get assayList from AssayMap based on idList
            ArrayList<String> assayList = getListFromId(idList, AssayMap);

            rawFileNameToAssayMap.put(rawFn, assayList);
        }
    }

    private void createAssayToRawFileNameMap() {
        assayToRawFileNameMap = new HashMap();
        for (String assay : assayIdMap.keySet()) {
            ArrayList<String> idList = assayIdMap.get(assay);

            // get rawFileNameList from rawFileNameMap based on idList
            ArrayList<String> rawFileNameList = getListFromId(idList, FileNameMap);

            assayToRawFileNameMap.put(assay, rawFileNameList);
        }
    }

    /**
     * @HashMap<String,String> peptideToTotalAreaRatioMap
     * @key = peptide sequence
     * @value = total area ratio
     */
    private void createPeptideToTotalAreaRatioMap() {
        for (String id : TotalAreaRatioMap.keySet()) {
            String pepSeq = PeptideSequenceMap.get(id);
            String ratio = peptideToTotalAreaRatioMap.get(pepSeq);
            if (ratio == null && TotalAreaRatioMap.get(id) != null && !TotalAreaRatioMap.get(id).equals("#N/A")) {
                peptideToTotalAreaRatioMap.put(pepSeq, TotalAreaRatioMap.get(id));
            }
        }
    }

    /**
     * @HashMap<String,String> peptideToRatioMap
     * @key = peptide sequence
     * @value = peptide ratio
     */
    private void createPeptideToRatioMap() {
        for (String id : PeptideRatioMap.keySet()) {
            String pepSeq = PeptideSequenceMap.get(id);
            String ratio = peptideToRatioMap.get(pepSeq);
            if (ratio == null && PeptideRatioMap.get(id) != null && !PeptideRatioMap.get(id).equals("#N/A")) {
                peptideToRatioMap.put(pepSeq, PeptideRatioMap.get(id));
            }
        }
    }

    private ArrayList<String> getListFromId(ArrayList<String> idList,
                                            HashMap<String, String> aMap) {
        ArrayList<String> retList = new ArrayList();
        for (String id : idList) {
            if (!retList.contains(aMap.get(id))) {
                retList.add(aMap.get(id));
            }
        }
        return retList;

    }

    @Override
    public void close()
            throws IOException {
        br.close();
    }

    private boolean allFalse() {
        return !(hasPeptideSequence
                || hasProteinName
                || hasReplicateName
                || hasModificationSequence
                || hasPrecursorMz
                || hasPrecursorCharge
                || hasProductMz
                || hasProductCharge
                || hasCleavageAa
                || hasFragmentIon
                || hasPeptideRetentionTime
                || hasRetentionTime
                || hasArea
                || hasBackground
                || hasPeakRank
                || hasFileName
                || hasIsotopeLabelType
                || hasHeight
                || hasTotalAreaRatio
                || hasAreaNormalized
                || hasPeptideRatio);
    }

}
