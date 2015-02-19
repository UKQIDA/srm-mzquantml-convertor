
package uk.ac.liv.srmconvertor;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.ac.liv.jmzqml.model.mzqml.Modification;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 07-Feb-2013 13:10:39
 */
public class SrmReader implements Closeable {

    // Skyline version 1.4 title names
    final String PEPTIDE_SEQUENCE_V14 = "peptidesequence";
    final String PROTEIN_NAME_V14 = "proteinname";
    final String REPLICATE_NAME_V14 = "replicatename";
    final String MODIFIED_SEQUENCE_V14 = "modifiedsequence";
    final String PRECURSOR_MZ_V14 = "precursormz";
    final String PRECURSOR_CHARGE_V14 = "precursorcharge";
    final String PRODUCT_MZ_V14 = "productmz";
    final String PRODUCT_CHARGE_V14 = "productcharge";
    final String CLEAVAGE_AA_V14 = "cleavageaa";
    final String FRAGMENT_ION_V14 = "fragmention";
    final String PEPTIDE_RETENTION_TIME_V14 = "peptideretentiontime";
    final String RETENTION_TIME_V14 = "retentiontime";
    final String AREA_V14 = "area";
    final String BACKGROUND_V14 = "background";
    final String FILE_NAME_V14 = "filename";
    final String PEAK_RANK_V14 = "peakrank";
    final String ISOTOPE_LABEL_TYPE_V14 = "isotopelabeltype";
    final String HEIGHT_V14 = "height";
    final String TOTAL_AREA_RATIO_V14 = "totalarearatio";
    final String AREA_NORMALIZED_V14 = "areanormalized";
    //final String RATIO_TO_STANDARD_V14 = "ratiotostandard";

    // Skyline version 2 title names
    final String PEPTIDE_SEQUENCE_V2 = "peptide sequence";
    final String PROTEIN_NAME_V2 = "protein name";
    final String REPLICATE_NAME_V2 = "replicate name";
    final String MODIFIED_SEQUENCE_V2 = "modified sequence";
    final String PRECURSOR_MZ_V2 = "precursor mz";
    final String PRECURSOR_CHARGE_V2 = "precursor charge";
    final String PRODUCT_MZ_V2 = "product mz";
    final String PRODUCT_CHARGE_V2 = "product charge";
    final String CLEAVAGE_AA_V2 = "cleavage aa";
    final String FRAGMENT_ION_V2 = "fragment ion";
    final String PEPTIDE_RETENTION_TIME_V2 = "peptide retention time";
    final String RETENTION_TIME_V2 = "retention time";
    final String AREA_V2 = "area";
    final String BACKGROUND_V2 = "background";
    final String FILE_NAME_V2 = "file name";
    final String PEAK_RANK_V2 = "peak rank";
    final String ISOTOPE_LABEL_TYPE_V2 = "isotope label type";
    final String HEIGHT_V2 = "height";
    final String TOTAL_AREA_RATIO_V2 = "total area ratio";
    final String AREA_NORMALIZED_V2 = "area normalized";
    //final String RATIO_TO_STANDARD_V2 = "ratio to standard";

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
    //private boolean hasPeptideRatio = false;

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
    //private int posPeptideRatio = 0;
    /*
     * HashMap one to one variables
     */
    private Map<String, String> PeptideSequenceMap = new HashMap<>();
    private Map<String, String> lightModifiedSequenceMap = new HashMap<>();
    private Map<String, String> heavyModifiedSequenceMap = new HashMap<>();
    private Map<String, String> ProteinNameMap = new HashMap<>();
    private Map<String, String> ReplicateNameMap = new HashMap<>();
    private Map<String, String> ModifiedSequenceMap = new HashMap<>();
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
    //private Map<String, String> PeptideRatioMap = new HashMap<>();
    //derived hashmap one to many variables
    private Map<String, List<String>> proteinIdMap;
    private Map<String, List<String>> peptideIdMap;
    private Map<String, List<String>> lightPeptideIdMap;
    private Map<String, List<String>> heavyPeptideIdMap;
    private Map<String, List<String>> modifiedSequenceIdMap;
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
    //private Map<String, String> peptideToRatioMap = new HashMap<>();
    private Map<String, String> peptideToTotalAreaRatioMap = new HashMap<>();
    private Map<String, List<Modification>> modificationMap;

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
                switch (nextLine[i].toLowerCase()) {
                    case PEPTIDE_SEQUENCE_V14:
                    case PEPTIDE_SEQUENCE_V2:
                        posPeptideSequence = i;
                        hasPeptideSequence = true;
                        break;
                    case PROTEIN_NAME_V14:
                    case PROTEIN_NAME_V2:
                        posProteinName = i;
                        hasProteinName = true;
                        break;
                    case REPLICATE_NAME_V14:
                    case REPLICATE_NAME_V2:
                        posReplicateName = i;
                        hasReplicateName = true;
                        break;
                    case MODIFIED_SEQUENCE_V14:
                    case MODIFIED_SEQUENCE_V2:
                        posModificationSequence = i;
                        hasModificationSequence = true;
                        break;
                    case PRECURSOR_MZ_V14:
                    case PRECURSOR_MZ_V2:
                        posPrecursorMz = i;
                        hasPrecursorMz = true;
                        break;
                    case PRECURSOR_CHARGE_V14:
                    case PRECURSOR_CHARGE_V2:
                        posPrecursorCharge = i;
                        hasPrecursorCharge = true;
                        break;
                    case PRODUCT_MZ_V14:
                    case PRODUCT_MZ_V2:
                        posProductMz = i;
                        hasProductMz = true;
                        break;
                    case PRODUCT_CHARGE_V14:
                    case PRODUCT_CHARGE_V2:
                        posProductCharge = i;
                        hasProductCharge = true;
                        break;
                    case CLEAVAGE_AA_V14:
                    case CLEAVAGE_AA_V2:
                        posCleavageAa = i;
                        hasCleavageAa = true;
                        break;
                    case FRAGMENT_ION_V14:
                    case FRAGMENT_ION_V2:
                        posFragmentIon = i;
                        hasFragmentIon = true;
                        break;
                    case PEPTIDE_RETENTION_TIME_V14:
                    case PEPTIDE_RETENTION_TIME_V2:
                        posPeptideRetentionTime = i;
                        hasPeptideRetentionTime = true;
                        break;
                    case RETENTION_TIME_V14:
                    case RETENTION_TIME_V2:
                        posRetentionTime = i;
                        hasRetentionTime = true;
                        break;
                    case AREA_V14:
                        posArea = i;
                        hasArea = true;
                        break;
                    case BACKGROUND_V14:
                        posBackground = i;
                        hasBackground = true;
                        break;
                    case FILE_NAME_V14:
                    case FILE_NAME_V2:
                        posFileName = i;
                        hasFileName = true;
                        break;
                    case PEAK_RANK_V14:
                    case PEAK_RANK_V2:
                        posPeakRank = i;
                        hasPeakRank = true;
                        break;
                    case ISOTOPE_LABEL_TYPE_V14:
                    case ISOTOPE_LABEL_TYPE_V2:
                        posIsotopeLabelType = i;
                        hasIsotopeLabelType = true;
                        break;
                    case HEIGHT_V14:
                        posHeight = i;
                        hasHeight = true;
                        break;
                    case TOTAL_AREA_RATIO_V14:
                    case TOTAL_AREA_RATIO_V2:
                        posTotalAreaRatio = i;
                        hasTotalAreaRatio = true;
                        break;
                    case AREA_NORMALIZED_V14:
                    case AREA_NORMALIZED_V2:
                        posAreaNormalized = i;
                        hasAreaNormalized = true;
                        break;
//                    case RATIO_TO_STANDARD_V14:
//                    case RATIO_TO_STANDARD_V2:
//                        posPeptideRatio = i;
//                        hasPeptideRatio = true;
//                        break;
                }
            }

            // read the data into hashmap
            // key = index
            int index = 0;
            while ((nextLine = reader.readNext()) != null) {

                if (hasIsotopeLabelType) {
                    IsotopeLabelTypeMap.put(Integer.toString(index), nextLine[posIsotopeLabelType]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + ISOTOPE_LABEL_TYPE_V14 + "\" or \"" + ISOTOPE_LABEL_TYPE_V2 + "\"");
                }
                if (hasPeptideSequence) {
                    PeptideSequenceMap.put(Integer.toString(index), nextLine[posPeptideSequence]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + PEPTIDE_SEQUENCE_V14 + "\" or \"" + PEPTIDE_SEQUENCE_V2 + "\"");
                }
                if (hasProteinName) {
                    ProteinNameMap.put(Integer.toString(index), nextLine[posProteinName]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + PROTEIN_NAME_V14 + "\" or \"" + PROTEIN_NAME_V2 + "\"");
                }
                if (hasReplicateName) {
                    ReplicateNameMap.put(Integer.toString(index), nextLine[posReplicateName]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + REPLICATE_NAME_V14 + "\" or \"" + REPLICATE_NAME_V2 + "\"");
                }
                if (hasModificationSequence) {
                    ModifiedSequenceMap.put(Integer.toString(index), nextLine[posModificationSequence]);
                    if (nextLine[posIsotopeLabelType].equalsIgnoreCase("light")) {
                        lightModifiedSequenceMap.put(Integer.toString(index), nextLine[posModificationSequence]);
                    }
                    else if (nextLine[posIsotopeLabelType].equalsIgnoreCase("heavy")) {
                        heavyModifiedSequenceMap.put(Integer.toString(index), nextLine[posModificationSequence]);
                    }
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + MODIFIED_SEQUENCE_V14 + "\" or \"" + MODIFIED_SEQUENCE_V2 + "\"");
                }
                if (hasPrecursorMz) {
                    PrecursorMzMap.put(Integer.toString(index), nextLine[posPrecursorMz]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + PRECURSOR_MZ_V14 + "\" or \"" + PRECURSOR_MZ_V2 + "\"");
                }
                if (hasPrecursorCharge) {
                    PrecursorChargeMap.put(Integer.toString(index), nextLine[posPrecursorCharge]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + PRECURSOR_CHARGE_V14 + "\" or \"" + PRECURSOR_CHARGE_V2 + "\"");
                }
                if (hasProductMz) {
                    ProductMzMap.put(Integer.toString(index), nextLine[posProductMz]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + PRODUCT_MZ_V14 + "\" or \"" + PRODUCT_MZ_V2 + "\"");
                }
                if (hasProductCharge) {
                    ProductChargeMap.put(Integer.toString(index), nextLine[posProductCharge]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + PRODUCT_CHARGE_V14 + "\" or \"" + PRODUCT_CHARGE_V2 + "\"");
                }
                if (hasCleavageAa) {
                    CleavageAaMap.put(Integer.toString(index), nextLine[posCleavageAa]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + CLEAVAGE_AA_V14 + "\" or \"" + CLEAVAGE_AA_V2 + "\"");
                }
                if (hasFragmentIon) {
                    FragmentIonMap.put(Integer.toString(index), nextLine[posFragmentIon]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + FRAGMENT_ION_V14 + "\" or \"" + FRAGMENT_ION_V2 + "\"");
                }
                if (hasPeptideRetentionTime) {
                    PeptideRetentionTimeMap.put(Integer.toString(index), nextLine[posPeptideRetentionTime]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + PEPTIDE_RETENTION_TIME_V14 + "\" or \"" + PEPTIDE_RETENTION_TIME_V2 + "\"");
                }
                if (hasRetentionTime) {
                    RetentionTimeMap.put(Integer.toString(index), nextLine[posRetentionTime]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + RETENTION_TIME_V14 + "\" or \"" + RETENTION_TIME_V2 + "\"");
                }
                if (hasArea) {
                    AreaMap.put(Integer.toString(index), nextLine[posArea]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + AREA_V14 + "\"");
                }
                if (hasBackground) {
                    BackgroundMap.put(Integer.toString(index), nextLine[posBackground]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + BACKGROUND_V14 + "\"");
                }
                if (hasFileName) {
                    FileNameMap.put(Integer.toString(index), nextLine[posFileName]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + FILE_NAME_V14 + "\" or \"" + FILE_NAME_V2 + "\"");
                }
                if (hasPeakRank) {
                    PeakRankMap.put(Integer.toString(index), nextLine[posPeakRank]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + PEAK_RANK_V14 + "\" or \"" + PEAK_RANK_V2 + "\"");
                }
                if (hasHeight) {
                    HeightMap.put(Integer.toString(index), nextLine[posHeight]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + HEIGHT_V14 + "\"");
                }
                if (hasTotalAreaRatio) {
                    TotalAreaRatioMap.put(Integer.toString(index), nextLine[posTotalAreaRatio]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + TOTAL_AREA_RATIO_V14 + "\" or \"" + TOTAL_AREA_RATIO_V2 + "\"");
                }
                if (hasAreaNormalized) {
                    AreaNormalizedMap.put(Integer.toString(index), nextLine[posAreaNormalized]);
                }
                else {
                    throw new IllegalStateException("Could not find column with name \"" + AREA_NORMALIZED_V14 + "\" or \"" + AREA_NORMALIZED_V2 + "\"");
                }
                if (hasReplicateName && hasIsotopeLabelType) {
                    AssayMap.put(Integer.toString(index), nextLine[posReplicateName] + "_" + nextLine[posIsotopeLabelType]);
                }
                else if (hasReplicateName && !hasIsotopeLabelType) {
                    AssayMap.put(Integer.toString(index), nextLine[posReplicateName]);
                }
//                if (hasPeptideRatio) {
//                    PeptideRatioMap.put(Integer.toString(index), nextLine[posPeptideRatio]);
//                }
//                else{
//                    throw new IllegalStateException("Could not find column with name \"" + RATIO_TO_STANDARD_V14 + "\" or \"" + RATIO_TO_STANDARD_V2 + "\"");
//                }

                index++;
            }
        }

        //create assayIdMap
        createAssayIdMap();

        //create proteinIdMap
        createProteinIdMap();

        //create peptideIdMap
        createPeptideIdMap();

        createLightHeavyPeptideIdMap();

        createModifiedSequenceIdMap();

        //create replicateIdMap, replicate name represents raw file name
        createReplicateIdMap();

        //create rawFileNameIdMap
        createRawFileNameIdMap();

//        if (allFalse()) {
//            throw new IllegalStateException("File is broken.");
//        }
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

//    public boolean hasPeptideRatio() {
//        return hasPeptideRatio;
//    }
    public List<String> getAssayList() {
        return new ArrayList(assayIdMap.keySet());
    }

    public List<String> getProteinList() {
        return new ArrayList(proteinIdMap.keySet());
    }

    public List<String> getPeptideList() {
        return new ArrayList(lightPeptideIdMap.keySet());
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
        return ModifiedSequenceMap;
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

//    public Map<String, String> getPeptideRatioMap() {
//        return PeptideRatioMap;
//    }

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

    public Map<String, List<String>> getLightPeptideIdMap() {
        return lightPeptideIdMap;
    }

    public Map<String, List<String>> getHeavyPeptideIdMap() {
        return heavyPeptideIdMap;
    }

    public Map<String, List<String>> getModifiedSequenceIdMap() {
        return modifiedSequenceIdMap;
    }

    public Map<String, List<String>> getRawFileNameIdMap() {
        return rawFileNameIdMap;
    }

    /*
     * cross reference HashMap (one to many)
     */
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

    public Map<String, List<Modification>> getModificationMap() {
        createModificationMap();
        return modificationMap;
    }

//    public Map<String, String> getPeptideToRatioMap() {
//        createPeptideToRatioMap();
//        return peptideToRatioMap;
//    }
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
        assayIdMap = new HashMap<>();

        if (!AssayMap.keySet().isEmpty()) {
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
        else {
            throw new IllegalStateException("Could not find column with name \"" + REPLICATE_NAME_V14 + "\" or \"" + ISOTOPE_LABEL_TYPE_V14
                    + "\" or \"" + REPLICATE_NAME_V2 + "\" or \"" + ISOTOPE_LABEL_TYPE_V2 + "\"");
        }
    }

    /**
     * @proteinIdMap: protein name to id list HashMap
     */
    private void createProteinIdMap() {
        proteinIdMap = new HashMap();

        if (!ProteinNameMap.keySet().isEmpty()) {
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
        else {
            throw new IllegalStateException("Could not find column with name \"" + PROTEIN_NAME_V14 + "\" or \"" + PROTEIN_NAME_V2 + "\"");
        }
    }

    /**
     * @peptideIdMap: peptide sequence to id list HashMap
     */
    private void createPeptideIdMap() {
        peptideIdMap = new HashMap<>();

        if (!PeptideSequenceMap.keySet().isEmpty()) {
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
        else {
            //throw new IllegalStateException("Could not find column with name \"" + PEPTIDE_SEQUENCE_V14 + "\" or \"" + PEPTIDE_SEQUENCE_V2 + "\"");
        }
    }

    private int getLengthOfLabels() {
        int modLength = 0;
        int lightSeqLength = 0;
        int heavySeqLength = 0;
        Set<String> lightModSeqSet = new HashSet<>();
        Set<String> heavyModSeqSet = new HashSet<>();

        // decide the length difference between light and heavy sequence
        for (String pepSeq : peptideIdMap.keySet()) {
            List<String> idList = peptideIdMap.get(pepSeq);
            if (idList != null) {
                for (String id : idList) {
                    if (IsotopeLabelTypeMap.get(id).equalsIgnoreCase("light")) {
                        lightModSeqSet.add(ModifiedSequenceMap.get(id));
                        lightSeqLength = ModifiedSequenceMap.get(id).length();
                    }
                    else if (IsotopeLabelTypeMap.get(id).equalsIgnoreCase("heavy")) {
                        heavyModSeqSet.add(ModifiedSequenceMap.get(id));
                        heavySeqLength = ModifiedSequenceMap.get(id).length();
                    }
                }

                // there is only one form of light and heavy sequence
                if (lightModSeqSet.size() == 1 && heavyModSeqSet.size() == 1) {
                    modLength = Math.abs(heavySeqLength - lightSeqLength);
                    break;
                }
            }
        }
        return modLength;
    }

    /**
     * Find the pairing light and heavy modified sequence and store them in a hash map
     *
     * @return HashMap of light and heavy paired sequence. Empty map if label free
     */
    public Map<String, String> getLightHeavySequenceMap() {
        Map<String, String> lhSeqMap = new HashMap<>();
        int modLength = getLengthOfLabels();

        // loop through light sequence
        for (String lightSeq : lightPeptideIdMap.keySet()) {
            List<String> idlightList = lightPeptideIdMap.get(lightSeq);
            if (idlightList != null) {
                // get the peptide sequence of the light sequence
                // most of time the sequence are the same unless there is modification
                String pepSeq = PeptideSequenceMap.get(idlightList.get(0));

                List<String> idAllList = peptideIdMap.get(pepSeq);
                // loop through all IDs of the peptide sequence and try to find the heavy sequence
                for (String id : idAllList) {
                    if (IsotopeLabelTypeMap.get(id).equalsIgnoreCase("heavy")) {
                        String heavySeq = ModifiedSequenceMap.get(id);

                        // compare the lenght of heavy sequence and light sequence
                        // if they have the specified length difference, they are pairs
                        // TODO: HOWEVER, two different light modified sequences of same peptide could have same length
                        // TODO: for example EAVS[+79.9]GILGK and EAVSGI[+79.9]LGK
                        // TODO: this is a potential bug to be fixed
                        if (heavySeq.length() == lightSeq.length() + modLength) {
                            lhSeqMap.put(lightSeq, heavySeq);
                        }
                    }
                }
            }
        }

        return lhSeqMap;
    }

    private void createLightHeavyPeptideIdMap() {
        lightPeptideIdMap = new HashMap<>();
        heavyPeptideIdMap = new HashMap<>();

        if (!ModifiedSequenceMap.keySet().isEmpty()) {
            for (String id : ModifiedSequenceMap.keySet()) {
                if (IsotopeLabelTypeMap.get(id).equalsIgnoreCase("light")) {
                    String modSeq = ModifiedSequenceMap.get(id);
                    List<String> idList = lightPeptideIdMap.get(modSeq);
                    if (idList == null) {
                        idList = new ArrayList();
                        lightPeptideIdMap.put(modSeq, idList);
                    }
                    idList.add(id);
                }
                //label free example, heavyPeptideIdMap will be an empty map
                else if (IsotopeLabelTypeMap.get(id).equalsIgnoreCase("heavy")) {
                    String modSeq = ModifiedSequenceMap.get(id);
                    List<String> idList = heavyPeptideIdMap.get(modSeq);
                    if (idList == null) {
                        idList = new ArrayList();
                        heavyPeptideIdMap.put(modSeq, idList);
                    }
                    idList.add(id);
                }
            }
        }
        else {
            //throw new IllegalStateException("Could not find column with name \"" + PEPTIDE_SEQUENCE_V14 + "\" or \"" + PEPTIDE_SEQUENCE_V2 + "\"");
        }
    }

    private void createModifiedSequenceIdMap() {
        modifiedSequenceIdMap = new HashMap<>();

        if (!ModifiedSequenceMap.keySet().isEmpty()) {
            for (String id : ModifiedSequenceMap.keySet()) {
                String modSeq = ModifiedSequenceMap.get(id);
                List<String> idList = modifiedSequenceIdMap.get(modSeq);
                if (idList == null) {
                    idList = new ArrayList();
                    modifiedSequenceIdMap.put(modSeq, idList);
                }
                idList.add(id);
            }
        }
    }

    private void createReplicateIdMap() {
        replicateIdMap = new HashMap<>();

        if (!ReplicateNameMap.keySet().isEmpty()) {
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
        else {
            throw new IllegalStateException("Could not find column with name \"" + REPLICATE_NAME_V14 + "\" or \"" + REPLICATE_NAME_V2 + "\"");
        }
    }

    /**
     * @rawFileNameIdMap: raw file name (.raw) to id list HashMap
     */
    private void createRawFileNameIdMap() {
        rawFileNameIdMap = new HashMap<>();

        if (!FileNameMap.keySet().isEmpty()) {
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
        else {
            throw new IllegalStateException("Could not find column with name \"" + FILE_NAME_V14 + "\" or \"" + FILE_NAME_V2 + "\"");
        }
    }

    /**
     * @Map<String, List<String>> proteinToPeptideMap
     * @key = protein name
     * @value = list of peptide sequence
     */
    private void createProteinToPeptideMap() {
        proteinToPeptideMap = new HashMap<>();

        if (!proteinIdMap.keySet().isEmpty()) {
            for (String protein : proteinIdMap.keySet()) {
                List<String> idList = proteinIdMap.get(protein);

                // get peptideList from PeptideSequenceMap based on idList
                List<String> peptideList = getListFromIds(idList, lightModifiedSequenceMap);
                proteinToPeptideMap.put(protein, peptideList);
            }
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
            List<String> proteinList = getListFromIds(idList, ProteinNameMap);

            peptideToProteinMap.put(peptide, proteinList);
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
            List<String> assayList = getListFromIds(idList, AssayMap);

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
            List<String> proteinList = getListFromIds(idList, ProteinNameMap);

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
            List<String> assayList = getListFromIds(idList, AssayMap);

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
            List<String> replicateList = getListFromIds(idList, ReplicateNameMap);

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
            List<String> assayList = getListFromIds(idList, AssayMap);

            rawFileNameToAssayMap.put(rawFn, assayList);
        }
    }

    private void createAssayToRawFileNameMap() {
        assayToRawFileNameMap = new HashMap<>();
        for (String assay : assayIdMap.keySet()) {
            List<String> idList = assayIdMap.get(assay);

            // get rawFileNameList from rawFileNameMap based on idList
            List<String> rawFileNameList = getListFromIds(idList, FileNameMap);

            assayToRawFileNameMap.put(assay, rawFileNameList);
        }
    }

    /**
     * @Map<String,String> peptideToTotalAreaRatioMap
     * @key = modified peptide sequence
     * @value = total area ratio
     */
    private void createPeptideToTotalAreaRatioMap() {
        for (String id : TotalAreaRatioMap.keySet()) {
            String pepSeq = ModifiedSequenceMap.get(id);
            String ratio = peptideToTotalAreaRatioMap.get(pepSeq);
            if (ratio == null && TotalAreaRatioMap.get(id) != null && !TotalAreaRatioMap.get(id).equals("#N/A")) {
                peptideToTotalAreaRatioMap.put(pepSeq, TotalAreaRatioMap.get(id));
            }
        }
    }

    private void createModificationMap() {
        modificationMap = new HashMap<>();

        Pattern modPattern = Pattern.compile("\\[[-+]?[0-9]*\\.?[0-9]+\\]", Pattern.CASE_INSENSITIVE);

        for (String id : ModifiedSequenceMap.keySet()) {
            String modSeq = ModifiedSequenceMap.get(id);
            Matcher matcher = modPattern.matcher(modSeq);

            int length = 0; //store the modification mass string legth so far. It will be removed when calculating mod location

            List<Modification> modList = modificationMap.get(id);

            if (modList == null) {
                modList = new ArrayList<>();
                modificationMap.put(id, modList);
            }

            while (matcher.find()) {
                Modification mod = new Modification();

                String mass = matcher.group().replaceAll("]", "").replace("+", "").trim().substring(1);
                char residue = modSeq.charAt(matcher.start() - 1);
                int location = matcher.start() - length;
                length = length + (matcher.end() - matcher.start());

                mod.setAvgMassDelta(Double.valueOf(mass));
                mod.getResidues().add(String.valueOf(residue));
                mod.setLocation(location);

                modList.add(mod);
            }
        }
    }

    /**
     * @HashMap<String,String> peptideToRatioMap
     * @key = peptide sequence
     * @value = peptide ratio
     */
//    private void createPeptideToRatioMap() {
//        for (String id : PeptideRatioMap.keySet()) {
//            String pepSeq = PeptideSequenceMap.get(id);
//            String ratio = peptideToRatioMap.get(pepSeq);
//            if (ratio == null && PeptideRatioMap.get(id) != null && !PeptideRatioMap.get(id).equals("#N/A")) {
//                peptideToRatioMap.put(pepSeq, PeptideRatioMap.get(id));
//            }
//        }
//    }
    /**
     * Get list of value from given map and list of id
     *
     * @param idList a given id list
     * @param aMap   a map with id as key
     *
     * @return list of value from aMap
     */
    private List<String> getListFromIds(List<String> idList,
                                        Map<String, String> aMap) {
        List<String> retList = new ArrayList<>();
        for (String id : idList) {
            if (aMap.get(id) != null && !retList.contains(aMap.get(id))) {
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
                || hasAreaNormalized //                || hasPeptideRatio
                );
    }

}
