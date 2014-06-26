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
import java.util.List;
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
    private Map<String, String> PeptideSequenceMap = new HashMap<>();
    private Map<String, String> ProteinNameMap = new HashMap<>();
    private Map<String, String> ReplicateNameMap = new HashMap<>();
    private Map<String, String> ModificationSequenceMap = new HashMap<>();
    private Map<String, String> PrecursorMzMap = new HashMap<>();
    private Map<String, String> PrecursorChargeMap = new HashMap<>();
    private Map<String, String> ProductMzMap = new HashMap<>();
    private Map<String, String> ProductChargeMap = new HashMap<>();
    private Map<String, String> CleavageAaMap = new HashMap<>();
    private Map<String, String> FragmentIonMap = new HashMap<>();
    private Map<String, String> PeptideRetentionTimeMap = new HashMap<>();
    private Map<String, String> RetentionTimeMap = new HashMap<>();
    private Map<String, String> AreaMap = new HashMap<>();
    private Map<String, String> BackgroundMap = new HashMap<>();
    private Map<String, String> PeakRankMap = new HashMap<>();
    private Map<String, String> FileNameMap = new HashMap<>();
    private Map<String, String> IsotopeLabelTypeMap = new HashMap<>();
    private Map<String, String> HeightMap = new HashMap<>();
    private Map<String, String> TotalAreaRatioMap = new HashMap<>();
    private Map<String, String> AreaNormalizedMap = new HashMap<>();
    private Map<String, String> AssayMap = new HashMap<>();
    //the orginal data from csv file, index-->ratio
    private Map<String, String> PeptideRatioMap = new HashMap<>();
    //derived hashmap one to many variables
    private Map<String, List<String>> proteinIdMap;
    private Map<String, List<String>> peptideIdMap;
    private Map<String, List<String>> assayIdMap;
    private Map<String, List<String>> replicateIdMap;
    private Map<String, List<String>> rawFileNameIdMap;
    //derived from the above IdMap(s)
    private Map<String, List<String>> proteinToPeptideMap;
    private Map<String, List<String>> peptideToProteinMap;
    private Map<String, List<String>> proteinToAssayMap;
    private Map<String, List<String>> assayToProteinMap;
    private Map<String, List<String>> peptideToAssayMap;
    private Map<String, List<String>> assayToPeptideMap;
    private Map<String, List<String>> replicateToAssayMap;
    private Map<String, List<String>> assayToReplicateMap;
    private Map<String, List<String>> rawFileNameToAssayMap;
    private Map<String, List<String>> assayToRawFileNameMap;
    private Map<String, String> peptideToRatioMap = new HashMap<>();
    private Map<String, String> peptideToTotalAreaRatioMap = new HashMap<>();

    ////////// ////////// ////////// ////////// //////////
    //Constrctor
    public SrmReader(Reader rd)
            throws IOException {
        br = new BufferedReader(rd);

        try (CSVReader reader = new CSVReader(br)) {
            String nextLine[];

            // process the title row to get positions for measurements
            nextLine = reader.readNext();

            for (int i = 0; i < nextLine.length; i++) {
                switch (nextLine[i]) {
                    case "PeptideSequence":
                        posPeptideSequence = i;
                        hasPeptideSequence = true;
                        break;
                    case "ProteinName":
                        posProteinName = i;
                        hasProteinName = true;
                        break;
                    case "ReplicateName":
                        posReplicateName = i;
                        hasReplicateName = true;
                        break;
                    case "ModificationSequence":
                        posModificationSequence = i;
                        hasModificationSequence = true;
                        break;
                    case "PrecursorMz":
                        posPrecursorMz = i;
                        hasPrecursorMz = true;
                        break;
                    case "PrecursorCharge":
                        posPrecursorCharge = i;
                        hasPrecursorCharge = true;
                        break;
                    case "ProductMz":
                        posProductMz = i;
                        hasProductMz = true;
                        break;
                    case "ProductCharge":
                        posProductCharge = i;
                        hasProductCharge = true;
                        break;
                    case "CleavageAa":
                        posCleavageAa = i;
                        hasCleavageAa = true;
                        break;
                    case "FragmentIon":
                        posFragmentIon = i;
                        hasFragmentIon = true;
                        break;
                    case "PeptideRetentionTime":
                        posPeptideRetentionTime = i;
                        hasPeptideRetentionTime = true;
                        break;
                    case "RetentionTime":
                        posRetentionTime = i;
                        hasRetentionTime = true;
                        break;
                    case "Area":
                        posArea = i;
                        hasArea = true;
                        break;
                    case "Background":
                        posBackground = i;
                        hasBackground = true;
                        break;
                    case "FileName":
                        posFileName = i;
                        hasFileName = true;
                        break;
                    case "PeakRank":
                        posPeakRank = i;
                        hasPeakRank = true;
                        break;
                    case "IsotopeLabelType":
                        posIsotopeLabelType = i;
                        hasIsotopeLabelType = true;
                        break;
                    case "Height":
                        posHeight = i;
                        hasHeight = true;
                        break;
                    case "TotalAreaRatio":
                        posTotalAreaRatio = i;
                        hasTotalAreaRatio = true;
                        break;
                    case "AreaNormalized":
                        posAreaNormalized = i;
                        hasAreaNormalized = true;
                        break;
                    case "RatioToStandard":
                        posPeptideRatio = i;
                        hasPeptideRatio = true;
                        break;
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
        }

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

    public List<String> getAssayList() {
        return new ArrayList(assayIdMap.keySet());
    }

    public List<String> getProteinList() {
        return new ArrayList(proteinIdMap.keySet());
    }

    public List<String> getPeptideList() {
        return new ArrayList(peptideIdMap.keySet());
    }

    public List<String> getReplicateList() {
        return new ArrayList(replicateIdMap.keySet());
    }

    public List<String> getRawFileNameList() {
        return new ArrayList(rawFileNameIdMap.keySet());
    }

    /*
     * return private HashMap(s)
     */
    public Map<String, String> getPeptideSequenceMap() {
        return PeptideSequenceMap;
    }

    public Map<String, String> getProteinNameMap() {
        return ProteinNameMap;
    }

    public Map<String, String> getReplicateNameMap() {
        return ReplicateNameMap;
    }

    public Map<String, String> getModificationSequenceMap() {
        return ModificationSequenceMap;
    }

    public Map<String, String> getPrecursorMzMap() {
        return PrecursorMzMap;
    }

    public Map<String, String> getPrecursorChargeMap() {
        return PrecursorChargeMap;
    }

    public Map<String, String> getProductMzMap() {
        return ProductMzMap;
    }

    public Map<String, String> getProductChargeMap() {
        return ProductChargeMap;
    }

    public Map<String, String> getCleavageAaMap() {
        return CleavageAaMap;
    }

    public Map<String, String> getFragmentIonMap() {
        return FragmentIonMap;
    }

    public Map<String, String> getPeptideRetentionTimeMap() {
        return PeptideRetentionTimeMap;
    }

    public Map<String, String> getRetentionTimeMap() {
        return RetentionTimeMap;
    }

    public Map<String, String> getAreaMap() {
        return AreaMap;
    }

    public Map<String, String> getBackgroundMap() {
        return BackgroundMap;
    }

    public Map<String, String> getPeakRankMap() {
        return PeakRankMap;
    }

    public Map<String, String> getIsotopeLabelTypeMap() {
        return IsotopeLabelTypeMap;
    }

    public Map<String, String> getHeightMap() {
        return HeightMap;
    }

    public Map<String, String> getTotalAreaRatioMap() {
        return TotalAreaRatioMap;
    }

    public Map<String, String> getAreaNormalizedMap() {
        return AreaNormalizedMap;
    }

    public Map<String, String> getAssayMap() {
        return AssayMap;
    }

    public Map<String, String> getPeptideRatioMap() {
        return PeptideRatioMap;
    }

    /*
     * return private one to many id list HashMaps
     */
    public Map<String, List<String>> getAssayIdMap() {
        return assayIdMap;
    }

    public Map<String, List<String>> getProteinIdMap() {
        return proteinIdMap;
    }

    public Map<String, List<String>> getPeptideIdMap() {
        return peptideIdMap;
    }

    public Map<String, List<String>> getRawFileNameIdMap() {
        return rawFileNameIdMap;
    }

    /*
     * cross reference HashMap (one to many)
     */
    public Map<String, List<String>> getAssayToPeptideMap() {
        createAssayToPeptideMap();
        return assayToPeptideMap;
    }

    public Map<String, List<String>> getPeptideToAssayMap() {
        createPeptideToAssayMap();
        return peptideToAssayMap;
    }

    public Map<String, List<String>> getAssayToProteinMap() {
        createAssayToProteinMap();
        return assayToProteinMap;
    }

    public Map<String, List<String>> getProteinToAssayMap() {
        createProteinToAssayMap();
        return proteinToAssayMap;
    }

    public Map<String, List<String>> getPeptideToProteinMap() {
        createPeptideToProteinMap();
        return peptideToProteinMap;
    }

    public Map<String, List<String>> getProteinToPeptideMap() {
        createProteinToPeptideMap();
        return proteinToPeptideMap;
    }

    public Map<String, List<String>> getAssayToReplicateMap() {
        createAssayToReplicateMap();
        return assayToReplicateMap;
    }

    public Map<String, List<String>> getReplicateToAssayMap() {
        createReplicateToAssayMap();
        return replicateToAssayMap;
    }

    public Map<String, List<String>> getRawFileNameToAssayMap() {
        createRawFileNameToAssayMap();
        return rawFileNameToAssayMap;
    }

    public Map<String, List<String>> getAssayToRawFileNameMap() {
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
            if (AssayMap.get(assN).contains("heavy")) {
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
    /**
     * @assayIdMap: assay name to id list HashMap
     */
    private void createAssayIdMap() {
        assayIdMap = new HashMap();

        if (AssayMap.keySet() != null) {
            for (String id : AssayMap.keySet()) {
                String assay = AssayMap.get(id);
                List<String> idList = assayIdMap.get(assay);
                if (idList == null) {
                    idList = new ArrayList();
                    assayIdMap.put(assay, idList);
                }
                idList.add(id);
            }
        }
    }

    /**
     * @proteinIdMap: protein name to id list HashMap
     */
    private void createProteinIdMap() {
        proteinIdMap = new HashMap();

        if (ProteinNameMap.keySet() != null) {
            for (String id : ProteinNameMap.keySet()) {
                String protein = ProteinNameMap.get(id);
                List<String> idList = proteinIdMap.get(protein);
                if (idList == null) {
                    idList = new ArrayList();
                    proteinIdMap.put(protein, idList);
                }
                idList.add(id);
            }
        }
    }

    /**
     * @peptideIdMap: peptide sequence to id list HashMap
     */
    private void createPeptideIdMap() {
        peptideIdMap = new HashMap<>();

        if (PeptideSequenceMap.keySet() != null) {
            for (String id : PeptideSequenceMap.keySet()) {
                String peptide = PeptideSequenceMap.get(id);
                List<String> idList = peptideIdMap.get(peptide);
                if (idList == null) {
                    idList = new ArrayList();
                    peptideIdMap.put(peptide, idList);
                }
                idList.add(id);
            }
        }
    }

    private void createReplicateIdMap() {
        replicateIdMap = new HashMap<>();

        for (String id : ReplicateNameMap.keySet()) {
            String replicate = ReplicateNameMap.get(id);
            List<String> idList = replicateIdMap.get(replicate);
            if (idList == null) {
                idList = new ArrayList();
                replicateIdMap.put(replicate, idList);
            }
            idList.add(id);
        }
    }

    /**
     * @rawFileNameIdMap: raw file name (.raw) to id list HashMap
     */
    private void createRawFileNameIdMap() {
        rawFileNameIdMap = new HashMap<>();
        if (FileNameMap.keySet() != null) {
            for (String id : FileNameMap.keySet()) {
                String rawFileName = FileNameMap.get(id);
                List<String> idList = rawFileNameIdMap.get(rawFileName);
                if (idList == null) {
                    idList = new ArrayList();
                    rawFileNameIdMap.put(rawFileName, idList);
                }
                idList.add(id);
            }
        }
    }

    /**
     * @Map<String, List<String>> proteinToPeptideMap
     * @key = protein name
     * @value = list of peptide sequence
     */
    private void createProteinToPeptideMap() {
        proteinToPeptideMap = new HashMap<>();
        for (String protein : proteinIdMap.keySet()) {
            List<String> idList = proteinIdMap.get(protein);

            // get peptideList from PeptideSequenceMap based on idList
            List<String> peptideList = getListFromId(idList, PeptideSequenceMap);

            proteinToPeptideMap.put(protein, peptideList);
        }
    }

    /**
     * @Map<String, List<String>> peptideToProteinMap
     * @key = peptide sequence
     * @value = list of protein name
     */
    private void createPeptideToProteinMap() {
        peptideToProteinMap = new HashMap<>();
        for (String peptide : peptideIdMap.keySet()) {
            List<String> idList = peptideIdMap.get(peptide);

            // get proteinList from ProteinNameMap based on idList
            List<String> proteinList = getListFromId(idList, ProteinNameMap);

            peptideToProteinMap.put(peptide, proteinList);
        }
    }

    /**
     * @Map<String, List<String>> peptideToAssayMap
     * @key = peptide sequence
     * @value = list of assay name
     */
    private void createPeptideToAssayMap() {
        peptideToAssayMap = new HashMap<>();
        for (String peptide : peptideIdMap.keySet()) {
            List<String> idList = peptideIdMap.get(peptide);

            // get assayList from AssayMap based on idList
            List<String> assayList = getListFromId(idList, AssayMap);

            peptideToAssayMap.put(peptide, assayList);
        }
    }

    /**
     * @Map<String, List<String>> assayToPeptideMap
     * @key = assay name
     * @value = list of peptide sequence
     */
    private void createAssayToPeptideMap() {
        assayToPeptideMap = new HashMap<>();
        for (String assay : assayIdMap.keySet()) {
            List<String> idList = assayIdMap.get(assay);

            // get peptideSequenceList from PeptideSequenceMap based on idList
            List<String> peptideSequenceList = getListFromId(idList, PeptideSequenceMap);

            assayToPeptideMap.put(assay, peptideSequenceList);
        }
    }

    /**
     * @Map<String, List<String>> proteinToAssayMap
     * @key = peptide sequence
     * @value = list of assay name
     */
    private void createProteinToAssayMap() {
        proteinToAssayMap = new HashMap<>();
        for (String protein : proteinIdMap.keySet()) {
            List<String> idList = proteinIdMap.get(protein);

            // get assayList from AssayMap based on idList
            List<String> assayList = getListFromId(idList, AssayMap);

            peptideToAssayMap.put(protein, assayList);
        }
    }

    /**
     * @Map<String, List<String>> assayToProteinMap
     * @key = assay name
     * @value = list of protein name
     */
    private void createAssayToProteinMap() {
        assayToProteinMap = new HashMap<>();
        for (String assay : assayIdMap.keySet()) {
            List<String> idList = assayIdMap.get(assay);

            // get proteinList from ProteinNameMap based on idList
            List<String> proteinList = getListFromId(idList, ProteinNameMap);

            assayToProteinMap.put(assay, proteinList);
        }
    }

    /**
     * @Map<String, List<String>> replicateToAssayMap
     * @key = replicate name
     * @value = list of assay name
     */
    private void createReplicateToAssayMap() {
        replicateToAssayMap = new HashMap<>();
        for (String replicate : replicateIdMap.keySet()) {
            List<String> idList = replicateIdMap.get(replicate);

            // get assayList from AssayMap based on idList
            List<String> assayList = getListFromId(idList, AssayMap);

            replicateToAssayMap.put(replicate, assayList);
        }
    }

    /**
     * @Map<String, List<String>> assayToReplicateMap
     * @key = assay name
     * @value = list of replicate name
     */
    private void createAssayToReplicateMap() {
        assayToReplicateMap = new HashMap<>();
        for (String assay : assayIdMap.keySet()) {
            List<String> idList = assayIdMap.get(assay);

            // get replicateList from ReplicateNameMap based on idList
            List<String> replicateList = getListFromId(idList, ReplicateNameMap);

            assayToReplicateMap.put(assay, replicateList);
        }
    }

    /**
     * @Map<String, List<String>> rawFileNameToAssayMap
     * @key = raw file name
     * @value = list of assay name
     */
    private void createRawFileNameToAssayMap() {
        rawFileNameToAssayMap = new HashMap<>();
        for (String rawFn : rawFileNameIdMap.keySet()) {
            List<String> idList = rawFileNameIdMap.get(rawFn);

            // get assayList from AssayMap based on idList
            List<String> assayList = getListFromId(idList, AssayMap);

            rawFileNameToAssayMap.put(rawFn, assayList);
        }
    }

    private void createAssayToRawFileNameMap() {
        assayToRawFileNameMap = new HashMap<>();
        for (String assay : assayIdMap.keySet()) {
            List<String> idList = assayIdMap.get(assay);

            // get rawFileNameList from rawFileNameMap based on idList
            List<String> rawFileNameList = getListFromId(idList, FileNameMap);

            assayToRawFileNameMap.put(assay, rawFileNameList);
        }
    }

    /**
     * @Map<String,String> peptideToTotalAreaRatioMap
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

    private List<String> getListFromId(List<String> idList,
                                       Map<String, String> aMap) {
        List<String> retList = new ArrayList();
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
