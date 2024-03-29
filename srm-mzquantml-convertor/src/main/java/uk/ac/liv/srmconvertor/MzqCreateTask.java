
package uk.ac.liv.srmconvertor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import org.apache.commons.lang3.math.NumberUtils;
import uk.ac.liv.jmzqml.model.mzqml.*;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLMarshaller;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 13-Jun-2014 14:55:53
 */
public class MzqCreateTask extends Task<Void> {

    // MzQuantML elements
    private CvList cvs = null;
    private AuditCollection auditCollection = null;
    private AnalysisSummary analysisSummary = null;
    private InputFiles inputFiles = null;
    private SoftwareList softwareList = null;
    private DataProcessingList dataProcessingList = null;
    private AssayList assays = null;
    private RatioList ratioList = null;
    private ProteinList proteins = null;
    private List<PeptideConsensusList> peptideConsensusListList = null;
    private List<FeatureList> featureLists = null;

    private final String in;
    private final File out;
    private final Parameter param;

    private SrmReader sRd;
    //private MzQuantMLMarshaller marshaller;
    //private MzQuantML mzq;
    private Cv cv;
    private Cv cv_mod;
    private Cv cv_unimod;
    private Cv cv_uo;
    private Label label;
    private Label label_heavy;
    private SearchDatabase db;
    private Map<String, List<String>> rgIdrawIdMap;
    private Map<String, String> rawIdrgIdMap;
    private Map<String, String> rawFileNameIdMap;
    private Map<String, String> rawFileIdNameMap;
    private Map<String, String> assayNameIdMap;
    private Map<String, String> assayIdNameMap;

    public MzqCreateTask(String input, File output, Parameter param) {
        this.in = input;
        this.out = output;
        this.param = param;
    }

    @Override
    protected Void call()
            throws Exception {
        updateMessage("Loading file ...");
        updateProgress(-1.0, 1.0);

//        try {
        sRd = new SrmReader(new FileReader(in));

        updateMessage("Start converting ...");

//        marshaller = new MzQuantMLMarshaller(out.getAbsolutePath());
//
//        //create MzQuantML instance
//        mzq = new MzQuantML();
//
//        String version = "1.0.0";
//        mzq.setVersion(version);
//
//        Calendar rightnow = Calendar.getInstance();
//        mzq.setCreationDate(rightnow);
//
//        int day = rightnow.get(Calendar.DATE);
//        int month = rightnow.get(Calendar.MONTH) + 1;
//        int year = rightnow.get(Calendar.YEAR);
//
//        /*
//         * set mzQuantML id
//         */
//        mzq.setId("SRM-" + String.valueOf(day) + String.valueOf(month) + String.valueOf(year));
        createCvList();

        createAnalysisSummary();

        createAuditCollection();

        createInputFiles();

        createAssayList();

        createSoftwareList();

        createDataProcessingList();

        createProteinList();

        createFeatureList();

        if (sRd.isLabelled() && sRd.hasTotalAreaRatio()) {
            createRatioList();
        }

        createPeptideConsensusList();

        /**
         * *
         * create a Marshaller and marshal to File
         */
        //marshaller.marshall(mzq);
        writeMzqFile(out);

        updateProgress(1.0, 1.0);
        updateMessage("Converting complete.");
//        }
//        catch (IOException ex) {
//            Logger.getLogger(MzqCreateTask.class.getName()).log(Level.SEVERE, null, ex);
//            //updateMessage("File loading failed, please check the input file.");
//            updateProgress(0, 1.0);
//        }
//        catch (NullPointerException nex) {
//            Logger.getLogger(MzqCreateTask.class.getName()).log(Level.SEVERE, null, nex);
//            //updateMessage("No input file is selected.");
//            updateProgress(0, 1.0);
//        }
//        catch (Exception e) {
//            Logger.getLogger(MzqCreateTask.class.getName()).log(Level.SEVERE, null, e);
//            //updateMessage("File loading failed:\n" + e.getMessage());
//            updateProgress(0, 1.0);
//        }

        return null;
    }

// private methods
    private void createCvList() {
        /**
         * create CvListType
         */
        cvs = new CvList();
        List<Cv> cvList = cvs.getCv();
        // psi-ms

        cv = MzQuantMLMarshaller.createCv("PSI-MS",
                                          "Proteomics Standards Initiative Mass Spectrometry Vocabularies",
                                          "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo",
                                          "2.25.0");
        cvList.add(cv);

        //unimod
        cv_unimod = MzQuantMLMarshaller.createCv("UNIMOD", "Unimod", "http://www.unimod.org/obo/unimod.obo", null);
        cvList.add(cv_unimod);

        //psi-mod
        cv_mod = MzQuantMLMarshaller.createCv("PSI-MOD",
                                              "Proteomics Standards Initiative Protein Modifications Vocabularies",
                                              "http://psidev.cvs.sourceforge.net/psidev/psi/mod/data/PSI-MOD.obo",
                                              null);
        cvList.add(cv_mod);

        //units ontology
        cv_uo = MzQuantMLMarshaller.createCv("UO", "Units Ontology", "http://obo.cvs.sourceforge.net/viewvc/obo/obo/ontology/phenotype/unit.obo", null);
        cvList.add(cv_uo);

        //mzq.setCvList(cvs);
        //light label
        label = new Label();
        CvParam labelCvParam = MzQuantMLMarshaller.createCvParam("unlabeled sample", cv, "MS:1002038");
        ModParam modParam = new ModParam();
        modParam.setCvParam(labelCvParam);
        label.getModification().add(modParam);

        //heavy label
        //TODO: to detect the heavy label from the file or get it from Skyline setting
        label_heavy = new Label();
        CvParam label_silac = MzQuantMLMarshaller.createCvParam("13C(6) Silac label", cv_mod, "MOD:00188", "6.020129", "", "", "");
        ModParam modParam_silac = new ModParam();
        modParam_silac.setCvParam(label_silac);
        label_heavy.getModification().add(modParam_silac);
    }

    private void createAnalysisSummary() {
        /*
         * create AnalysisSummary
         */

        analysisSummary = new AnalysisSummary();
        analysisSummary.getParamGroup().add(MzQuantMLMarshaller.createCvParam("SRM quantitation analysis", cv, "MS:1001838"));

        if (sRd.isLabelled()) {
            CvParam labelBasedCv = MzQuantMLMarshaller.createCvParam("MS1 Label-based analysis", cv, "MS:1002018");
            analysisSummary.getParamGroup().add(labelBasedCv);
        }
        else {
            CvParam labelFreeCv = MzQuantMLMarshaller.createCvParam("LC-MS label-free quantitation analysis", cv, "MS:1001834");
            analysisSummary.getParamGroup().add(labelFreeCv);
        }

        // for absolute quantitation analysis only
//        if (param.isAbsQuant()) {
//            analysisSummary.getParamGroup().add(MzQuantMLMarshaller.createCvParam("absolute quantitation analysis", "PSI-MS", "MS:1002514"));
//        }
        CvParam analysisSummaryCv = MzQuantMLMarshaller.createCvParam("SRM feature level quantitation", cv, "MS:1002281", "true", "", "", "");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        analysisSummaryCv = MzQuantMLMarshaller.createCvParam("SRM peptide level quantitation", cv, "MS:1002282", "true", "", "", "");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        analysisSummaryCv = MzQuantMLMarshaller.createCvParam("SRM protein level quantitation", cv, "MS:1002283", "false", "", "", "");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        analysisSummaryCv = MzQuantMLMarshaller.createCvParam("SRM proteingroup level quantitation", cv, "MS:1002284", "false", "", "", "");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        // for absolute quantitation analysis only
//        if (param.isAbsQuant()) {
//            analysisSummaryCv = MzQuantMLMarshaller.createCvParam("internal peptide reference used", "PSI-MS", "MS:1002515");
//            analysisSummaryCv.setValue("true");
//            analysisSummary.getParamGroup().add(analysisSummaryCv);
//
//            analysisSummaryCv = MzQuantMLMarshaller.createCvParam("internal protein reference used", "PSI-MS", "MS:1002516");
//            analysisSummaryCv.setValue("false");
//            analysisSummary.getParamGroup().add(analysisSummaryCv);
//        }
        //mzq.setAnalysisSummary(analysisSummary);
    }

    private void createAuditCollection() {
        /**
         * create AuditCollection
         */
        auditCollection = new AuditCollection();

        Organization org = new Organization();
        org.setId("ORG1");
        org.setName(param.getOrg());

        Person per = new Person();
        per.setFirstName(param.getFirstName());
        per.setLastName(param.getLastName());

        Affiliation aff = new Affiliation();
        aff.setOrganization(org);
        per.getAffiliation().add(aff);
        per.setId("PERSON1");
        auditCollection.getPerson().add(per);

        // the schema require person before organization
        auditCollection.getOrganization().add(org);

        //mzq.setAuditCollection(auditCollection);
    }

    private void createInputFiles() {
        /**
         * *
         * create InputFiles
         */
        inputFiles = new InputFiles();
        List<RawFilesGroup> rawFilesGroupList = inputFiles.getRawFilesGroup();

        /*
         * create rawFilesGroup
         */
        //all current rawFilesGroup examples are one to one mapping between rg_Id and raw_Id
        //TODO: find some prefractionation examples 
        //initialisation
        rawFileNameIdMap = new HashMap<>();
        rawFileIdNameMap = new HashMap<>();
        rgIdrawIdMap = new HashMap<>();
        rawIdrgIdMap = new HashMap<>();

        int rawFileCounter = 0;

        for (String rawFn : sRd.getRawFileNameList()) {

            // create raw file ID
            String rawId = "raw_" + Integer.toString(rawFileCounter);

            RawFilesGroup rawFilesGroup = new RawFilesGroup();
            List<RawFile> rawFiles = rawFilesGroup.getRawFile();

            RawFile rawFile = new RawFile();
            rawFile.setId(rawId);
            rawFile.setName(rawFn);
            /*
             * TODO:make up some raw file locations as we don't know the real
             * location from progenesis files
             */
            rawFile.setLocation("../msmsdata/" + rawFn);
            rawFileNameIdMap.put(rawFn, rawId);
            rawFileIdNameMap.put(rawId, rawFn);
            rawFiles.add(rawFile);

            String rgId = "rg_" + Integer.toString(rawFileCounter);
            rawFilesGroup.setId(rgId);
            rawFilesGroupList.add(rawFilesGroup);

            /*
             * build rgIdrawIdMap raw files group id as key raw file ids
             * (ArrayList) as value
             */
            List<String> rawIds = rgIdrawIdMap.get(rgId);
            if (rawIds == null) {
                rawIds = new ArrayList<>();
                rgIdrawIdMap.put(rgId, rawIds);
            }
            rawIds.add(rawId);
            rawIdrgIdMap.put(rawId, rgId);

            rawFileCounter++;
        }

        //add search databases
        List<SearchDatabase> searchDBs = inputFiles.getSearchDatabase();
        db = new SearchDatabase();
        db.setId("SD1");
        db.setLocation("sgd_orfs_plus_ups_prots.fasta");
        searchDBs.add(db);
        Param dbName = new Param();
        db.setDatabaseName(dbName);
        UserParam dbNameParam = new UserParam();
        dbNameParam.setName("sgd_orfs_plus_ups_prots.fasta");
        dbName.setParam(dbNameParam);

        //mzq.setInputFiles(inputFiles);
    }

    private void createAssayList() {
        /**
         * create AssayList
         */
        assays = new AssayList();
        assays.setId("AssayList_1");
        List<Assay> assayList = assays.getAssay();
        int assayCounter = 0;

        //initialisation
        assayNameIdMap = new HashMap<>();
        assayIdNameMap = new HashMap<>();

        for (String assayN : sRd.getAssayList()) {
            Assay assay = new Assay();
            String assayId = "ass_" + Integer.toString(assayCounter);
            assay.setId(assayId);
            assay.setName(assayN);
            if (assayN.contains("light")) {
                assay.setLabel(label);
            }
            else if (assayN.contains("heavy")) {
                assay.setLabel(label_heavy);
            }
            assayList.add(assay);
            assayCounter++;

            assayNameIdMap.put(assayN, assayId);
            assayIdNameMap.put(assayId, assayN);

            RawFilesGroup rawFilesGroup = new RawFilesGroup();

            rawFilesGroup.setId(getRawFilesGroupIdFromAssayName(assayN));

            assay.setRawFilesGroup(rawFilesGroup);
        }
        //mzq.setAssayList(assays);
    }

    private void createSoftwareList() {
        /**
         * *
         * create SoftwareList
         */
        softwareList = new SoftwareList();
        // Skyline
        Software skyline = new Software();
        softwareList.getSoftware().add(skyline);
        skyline.setId("Skyline");
        skyline.setVersion(sRd.getVersion());
        skyline.getCvParam().add(MzQuantMLMarshaller.createCvParam("Skyline", "PSI-MS", "MS:1000922"));

        // SRM convertor
        Software srmConv = new Software();
        softwareList.getSoftware().add(srmConv);
        srmConv.setId("Srm_Convertor");
        srmConv.setVersion("1.0");
        srmConv.getCvParam().add(MzQuantMLMarshaller.createCvParam("Skyline mzQuantML converter", cv, "MS:1002536"));

        //mzq.setSoftwareList(softwareList);
    }

    private void createDataProcessingList() {
        /**
         * *
         * create DataProcessingList
         */
        dataProcessingList = new DataProcessingList();
        // first data processing 
        DataProcessing dataProcessing = new DataProcessing();
        dataProcessing.setId("feature_quantification");
        dataProcessing.setSoftware(softwareList.getSoftware().get(0));
        dataProcessing.setOrder(BigInteger.ONE);
        ProcessingMethod processingMethod = new ProcessingMethod();
        processingMethod.setOrder(BigInteger.ONE);
        processingMethod.getParamGroup().add(MzQuantMLMarshaller.createCvParam("quantification data processing", "PSI-MS", "MS:1001861"));
        dataProcessing.getProcessingMethod().add(processingMethod);
        dataProcessingList.getDataProcessing().add(dataProcessing);

        // second data processing
        dataProcessing = new DataProcessing();
        dataProcessing.setId("conversion");
        dataProcessing.setSoftware(softwareList.getSoftware().get(1));
        dataProcessing.setOrder(BigInteger.valueOf(2));
        processingMethod = new ProcessingMethod();
        processingMethod.setOrder(BigInteger.ONE);
        UserParam up = new UserParam();
        up.setName("Skyline file conversion");
        processingMethod.getUserParam().add(up);
        dataProcessing.getProcessingMethod().add(processingMethod);
        dataProcessingList.getDataProcessing().add(dataProcessing);

        // third data processing
        dataProcessing = new DataProcessing();
        dataProcessing.setId("peptide_quantification");
        dataProcessing.setSoftware(softwareList.getSoftware().get(1));
        dataProcessing.setOrder(BigInteger.valueOf(2));
        processingMethod = new ProcessingMethod();
        processingMethod.setOrder(BigInteger.valueOf(2));
        up = new UserParam();
        up.setName("peptide quantification calculation by summing feature quantification");
        processingMethod.getUserParam().add(up);
        dataProcessing.getProcessingMethod().add(processingMethod);
        dataProcessingList.getDataProcessing().add(dataProcessing);

        //mzq.setDataProcessingList(dataProcessingList);
    }

    private void createProteinList() {
        /**
         * *
         * create ProteinList
         */
        // get protein to peptide map from SRMReader
        Map<String, List<String>> protToPepMap = sRd.getProteinToPeptideMap();

        proteins = new ProteinList();
        proteins.setId("ProtList1");
        List<Protein> proteinList = proteins.getProtein();
        int protCounter = 0;
        for (String protAcc : sRd.getProteinList()) {

            Protein protein = new Protein();

            // set protein ID
            protein.setId("prot_" + Integer.toString(protCounter));

            protCounter++;

            // set protein accession
            protein.setAccession(protAcc);
            protein.setSearchDatabase(db);

            List<String> pepSeqs = protToPepMap.get(protAcc);

            if (pepSeqs != null) {

                //List<Object> peptideConsensusRefList = protein.getPeptideConsensusRefs();  //jmzquantml 1.0.0-1.0.0
                //List<PeptideConsensus> peptides = new ArrayList();
                List<String> peptideIds = protein.getPeptideConsensusRefs();
                List<String> pepIds = new ArrayList();

                for (String pepSeq : pepSeqs) {
                    PeptideConsensus pepCon = new PeptideConsensus();
                    pepCon.setId("pep_" + pepSeq);
                    pepCon.setPeptideSequence(pepSeq);

                    if (!pepIds.contains("pep_" + pepSeq)) {

                        peptideIds.add("pep_" + pepSeq);
                        pepIds.add("pep_" + pepSeq);
                    }
                }
                //protein.setPeptideConsensuses(peptides);
            }

            proteinList.add(protein);
        }
        //mzq.setProteinList(proteins);
    }

    private void createFeatureList() {
        /**
         * *
         * create FeatureList
         */
        featureLists = new ArrayList<>();
        Map<String, FeatureList> rgIdFeatureListMap = new HashMap<>();

        // for each feature
        for (String id : sRd.getAreaMap().keySet()) {

            // deal with feature
            Feature feature = new Feature();
            feature.setId("ft_" + id);

            // get precursor charge
            String charge = sRd.getPrecursorChargeMap().get(id);
            feature.setCharge(charge);

            // get precursor m/z
            String mz = sRd.getPrecursorMzMap().get(id);
            feature.setMz(Double.valueOf(mz));

            // get retention time
            String rt = sRd.getPeptideRetentionTimeMap().get(id);
            if (rt == null) {
                rt = "null";
            }
            else if (rt.equals("#N/A")) {
                rt = "null";
            }
            feature.setRt(rt);

            // get product charge
            String proCharge = sRd.getProductChargeMap().get(id);

            // get product m/z
            String proMz = sRd.getProductMzMap().get(id);

            // get product rt
            String proRt = sRd.getRetentionTimeMap().get(id);
            if (proRt.equals("#N/A")) {
                proRt = "null";
            }

            // set cv term for Q3 mz
            CvParam cpMz = MzQuantMLMarshaller.createCvParam("isolation window target m/z", "PSI-MS", "MS:1000827");
            cpMz.setValue(proMz);
            feature.getCvParam().add(cpMz);

            // set cv term for Q3 charge
            CvParam cpCharge = MzQuantMLMarshaller.createCvParam("charge state", "PSI-MS", "MS:1000041");
            cpCharge.setValue(proCharge);
            feature.getCvParam().add(cpCharge);

            // set cv term for Q3 rt
            CvParam cpRt = MzQuantMLMarshaller.createCvParam("local retention time", "PSI-MS", "MS:1000895");
            cpRt.setValue(proRt);
            cpRt.setUnitCv(cv_uo);
            cpRt.setUnitAccession("UO:0000031");
            cpRt.setUnitName("minute");
            feature.getCvParam().add(cpRt);

            // find out this feature belongs to which raw files group via assay name
            String assayN = sRd.getAssayMap().get(id);
            //String rgId = assayNrgIdMap.get(assayN);  //jmzquantml 1.0.0-1.0.0

            String rgId = getRawFilesGroupIdFromAssayName(assayN);

            // get a FeatureList from rgIdFeatureListMap first
            FeatureList featureList = rgIdFeatureListMap.get(rgId);
            if (featureList == null) {
                featureList = new FeatureList();
                RawFilesGroup rawFilesGroup = new RawFilesGroup();
                rawFilesGroup.setId(rgId);
                featureList.setRawFilesGroup(rawFilesGroup);
                String fListId = "Flist_" + rgId.substring(3);
                featureList.setId(fListId);
                rgIdFeatureListMap.put(rgId, featureList);
                featureLists.add(featureList);

                //add this featureList to data processing step 3 as InputObject_refs
                DataProcessing dp = dataProcessingList.getDataProcessing().get(2);
                dp.getInputObjectRefs().add(featureList.getId());

                /*
                 * add featureQuantLayers
                 *
                 */
                List<GlobalQuantLayer> featureQuantLayers = featureList.getFeatureQuantLayer();
                GlobalQuantLayer featureQuantLayer = new GlobalQuantLayer();
                featureQuantLayers.add(featureQuantLayer);
                featureQuantLayer.setId("FQL_" + fListId.substring(6));
                ColumnDefinition featureColumnIndex = new ColumnDefinition();
                featureQuantLayer.setColumnDefinition(featureColumnIndex);

                /*
                 * The first column for Q3 area
                 */
                Column columnArea = new Column();
                columnArea.setIndex(BigInteger.ZERO);
                CvParamRef cpRefArea = new CvParamRef();

                //cv term for Q3 area
                cpRefArea.setCvParam(MzQuantMLMarshaller.createCvParam("XIC area", "PSI-MS", "MS:1001858"));
                columnArea.setDataType(cpRefArea);

                featureColumnIndex.getColumn().add(columnArea);

                /*
                 * The second column for Q3 background
                 */
                Column columnBg = new Column();
                columnBg.setIndex(BigInteger.ONE);
                CvParamRef cpRefBg = new CvParamRef();

                //cv term for Q3 background
                cpRefBg.setCvParam(MzQuantMLMarshaller.createCvParam("product background", "PSI-MS", "MS:1002414"));
                columnBg.setDataType(cpRefBg);

                featureColumnIndex.getColumn().add(columnBg);

                /*
                 * The third column for Q3 peakrank
                 */
                Column columnPr = new Column();
                columnPr.setIndex(BigInteger.valueOf(2));
                CvParamRef cpRefPr = new CvParamRef();

                //cv term for Q3 background
                cpRefPr.setCvParam(MzQuantMLMarshaller.createCvParam("product interpretation rank", "PSI-MS", "MS:1000926"));
                columnPr.setDataType(cpRefPr);

                featureColumnIndex.getColumn().add(columnPr);

                /*
                 * The fourth column for Q3 height
                 */
                Column columnHt = new Column();
                columnHt.setIndex(BigInteger.valueOf(3));
                CvParamRef cpRefHt = new CvParamRef();

                //cv term for Q3 height
                cpRefHt.setCvParam(MzQuantMLMarshaller.createCvParam("peak intensity", "PSI-MS", "MS:1000042"));
                columnHt.setDataType(cpRefHt);

                featureColumnIndex.getColumn().add(columnHt);

                /*
                 * The fifth column for Q3 AreaNormalized
                 */
                Column columnAn = new Column();
                columnAn.setIndex(BigInteger.valueOf(4));
                CvParamRef cpRefAn = new CvParamRef();

                //cv term for Q3 AreaNormalized
                cpRefAn.setCvParam(MzQuantMLMarshaller.createCvParam("normalized XIC area", "PSI-MS", "MS:1001859"));
                columnAn.setDataType(cpRefAn);

                featureColumnIndex.getColumn().add(columnAn);

                // deal with DM for GlobalQuantLayer
                DataMatrix DM = new DataMatrix();

                Row row = new Row();
                row.setObject(feature);
                /*
                 * first column
                 */
                String area = sRd.getAreaMap().get(id);
                if (area.equals("#N/A")) {
                    area = "0";
                }
                row.getValue().add(area);

                /*
                 * second column
                 */
                String background = sRd.getBackgroundMap().get(id);
                if (background.equals("#N/A")) {
                    background = "0";
                }
                row.getValue().add(background);

                /*
                 * third column
                 */
                String peakRank = sRd.getPeakRankMap().get(id);
                if (peakRank.equals("#N/A")) {
                    peakRank = "0";
                }
                row.getValue().add(peakRank);

                /*
                 * fourth column
                 */
                String height = sRd.getHeightMap().get(id);
                if (height.equals("#N/A")) {
                    height = "0";
                }
                row.getValue().add(height);

                /*
                 * fifth column
                 */
                String areaNormalized = sRd.getAreaNormalizedMap().get(id);
                if (areaNormalized.equals("#N/A")) {
                    areaNormalized = "0";
                }
                //change the percentage format to double format
                areaNormalized = String.valueOf(Double.parseDouble(areaNormalized.replace('%', ' ')) / 100);

                row.getValue().add(areaNormalized);

                //add row
                DM.getRow().add(row);
                featureQuantLayer.setDataMatrix(DM);
            }
            else {
                Row row = new Row();
                row.setObject(feature);

                /*
                 * first column
                 */
                String area = sRd.getAreaMap().get(id);
                if (area.equals("#N/A")) {
                    area = "0";
                }
                row.getValue().add(area);

                /*
                 * second column
                 */
                String background = sRd.getBackgroundMap().get(id);
                if (background.equals("#N/A")) {
                    background = "0";
                }
                row.getValue().add(background);

                /*
                 * third column
                 */
                String peakRank = sRd.getPeakRankMap().get(id);
                if (peakRank.equals("#N/A")) {
                    peakRank = "0";
                }
                row.getValue().add(peakRank);

                /*
                 * fourth column
                 */
                String height = sRd.getHeightMap().get(id);
                if (height.equals("#N/A")) {
                    height = "0";
                }
                row.getValue().add(height);

                /*
                 * fifth column
                 */
                String areaNormalized = sRd.getAreaNormalizedMap().get(id);
                if (areaNormalized.equals("#N/A")) {
                    areaNormalized = "0";
                }
                //change the percentage format to double format
                areaNormalized = String.valueOf(Double.parseDouble(areaNormalized.replace('%', ' ')) / 100);

                row.getValue().add(areaNormalized);

                //add row
                featureList.getFeatureQuantLayer().get(0).getDataMatrix().getRow().add(row);
            }
            featureList.getFeature().add(feature);

        }
    }

    private void createRatioList() {
        /**
         * *
         * create RatioList
         */
        ratioList = new RatioList();
        Ratio pepRatio = new Ratio();
        pepRatio.setId("ratio1");
        pepRatio.setName("Ratio for peptide");

        //work out which assay ref used in numerator and denominator
        for (String assayN : assayNameIdMap.keySet()) {

            if (assayN.contains("light")) {
                //numerator_ref
                Assay assay = new Assay();
                assay.setId(assayNameIdMap.get(assayN));
                pepRatio.setNumerator(assay);
            }
            else if (assayN.contains("heavy")) {
                //denominator_ref
                Assay assay = new Assay();
                assay.setId(assayNameIdMap.get(assayN));
                pepRatio.setDenominator(assay);
            }
            else {
                throw new IllegalArgumentException("neight light nor heavy assay is provided");
            }
        }

        CvParamRef denominator_cpRef = new CvParamRef();
        denominator_cpRef.setCvParam(MzQuantMLMarshaller.createCvParam("XIC area", cv, "MS:1001858"));
        pepRatio.setDenominatorDataType(denominator_cpRef);

        CvParamRef numerator_cpRef = new CvParamRef();
        numerator_cpRef.setCvParam(MzQuantMLMarshaller.createCvParam("XIC area", cv, "MS:1001858"));
        pepRatio.setNumeratorDataType(numerator_cpRef);

        //add to RatioList
        ratioList.getRatio().add(pepRatio);

        //mzq.setRatioList(ratioList);
    }

    private void createPeptideConsensusList() {
        /*
         * create PeptideConsensusList
         */
        peptideConsensusListList = new ArrayList<>();

        PeptideConsensusList peptideConsensuses = new PeptideConsensusList();
        peptideConsensuses.setId("PepList1");

        /*
         * set RatioQuantLayer
         */
        DataMatrix pepRatioDM = new DataMatrix();
        if (sRd.isLabelled() && sRd.hasTotalAreaRatio()) {

            RatioQuantLayer pepRQL = new RatioQuantLayer();
            pepRQL.setId("PepRQL_1");
            for (Ratio rat : ratioList.getRatio()) {
                pepRQL.getColumnIndex().add(rat.getId());
            }
            //pepRQL.getColumns().add(mzq.getRatioList().getRatio().get(0));

            pepRQL.setDataMatrix(pepRatioDM);
            peptideConsensuses.setRatioQuantLayer(pepRQL);
        }

        // PeptideConsensusList
        List<PeptideConsensus> peptideList = peptideConsensuses.getPeptideConsensus();
        Map<String, String> lhSeqMap = sRd.getLightHeavySequenceMap();
        //for (String pepSeq : sRd.getPeptideList()) {
        for (String lpepSeq : sRd.getLightPeptideIdMap().keySet()) {

            //get heavy label sequence
            String hpepSeq = "";
            if (!lhSeqMap.isEmpty()) {
                hpepSeq = lhSeqMap.get(lpepSeq);
            }

            PeptideConsensus pepCon = new PeptideConsensus();
            pepCon.setId("pep_" + lpepSeq);
            //pepCon.setPeptideSequence(pepSeq);
            pepCon.setSearchDatabase(db);

            List<String> lids = sRd.getLightPeptideIdMap().get(lpepSeq);
            pepCon.setPeptideSequence(sRd.getPeptideSequenceMap().get(lids.get(0)));

            // add modifications if there is any
            
            List<Modification> modList = sRd.getModificationMap().get(lids.get(0));

            // set CV (Unimod) for each Modification as they are not set when reading from csv file
            for (Modification mod : modList) {
                for (CvParam cp : mod.getCvParam()) {
                    cp.setCv(cv_unimod);
                }
            }

            if (!modList.isEmpty()) {
                pepCon.getModification().addAll(modList);
            }

            List<String> hids = new ArrayList<>();
            if (!sRd.getHeavyPeptideIdMap().isEmpty()) {
                hids.addAll(sRd.getHeavyPeptideIdMap().get(hpepSeq));
            }

            List<String> ids = new ArrayList<>();
            ids.addAll(lids);
            ids.addAll(hids);

            //add peptide charge list
            List<String> charges = new ArrayList<>();
            for (String id : ids) {
                String charge = sRd.getPrecursorChargeMap().get(id);
                if (!charges.contains(charge)) {
                    charges.add(charge);
                }
            }
            pepCon.getCharge().addAll(charges);

            // Set EvidenceRef for each PeptideConsensus
            for (String id : ids) {
                // set List<EvidenceRef> to pepCon
                EvidenceRef evidenceRef = new EvidenceRef();
                pepCon.getEvidenceRef().add(evidenceRef);

                // set Feature to evidenceRef
                Feature feature = new Feature();
                feature.setId("ft_" + id);
                evidenceRef.setFeature(feature);

                // set AssayRefs to evidenceRef
                String assayN = sRd.getAssayMap().get(id);
                String assayId = assayNameIdMap.get(assayN);
                Assay assay = new Assay();
                assay.setId(assayId);

                List<Assay> assayas = new ArrayList<>();
                if (!evidenceRef.getAssayRefs().contains(assay.getId())) {
                    //evidenceRef.getAssayRefs().add(assay.getId());
                    //evidenceRef.getAssays().add(assay);
                    assayas.add(assay);
                }
                evidenceRef.setAssays(assayas);
            }
            peptideList.add(pepCon);

            /*
             * add DataMatrix for RatioQuantLayer
             */
//            if (sRd.hasPeptideRatio()) {
//                Row row = new Row();
//                row.setObject(pepCon);
//                String ratio = sRd.getPeptideToRatioMap().get(pepSeq);
//                row.getValue().add(ratio);
//                pepRatioDM.getRow().add(row);
//            }
//            else if (sRd.hasTotalAreaRatio()) {
            Row row = new Row();
            row.setObject(pepCon);
            String ratio = sRd.getPeptideToTotalAreaRatioMap().get(lpepSeq);
            row.getValue().add(ratio);
            pepRatioDM.getRow().add(row);
//            }
        }

        // Calculate peptide level quant (area value)
        Map<String, List<String>> peptideIdsMap = sRd.getPeptideIdMap();

        // Store calculated peptide area values for each modified peptide of each assay
        Map<String, List<String>> peptideAreaMap = new HashMap<>();

        for (String lpepSeq : sRd.getLightPeptideIdMap().keySet()) {
            //get heavy label sequence
            String hpepSeq = "";
            if (!lhSeqMap.isEmpty()) {
                hpepSeq = lhSeqMap.get(lpepSeq);
            }

            List<String> lids = sRd.getLightPeptideIdMap().get(lpepSeq);

            List<String> hids = new ArrayList<>();
            if (!sRd.getHeavyPeptideIdMap().isEmpty()) {
                hids.addAll(sRd.getHeavyPeptideIdMap().get(hpepSeq));
            }

            List<String> ids = new ArrayList<>();
            ids.addAll(lids);
            ids.addAll(hids);

            double totalLight = 0;
            double totalHeavy = 0;

            // Add calculate peptide area value to peptideAreaMap
            List<String> peptideAreaList = peptideAreaMap.get(lpepSeq);
            if (peptideAreaList == null) {
                peptideAreaList = new ArrayList<>();
                peptideAreaMap.put(lpepSeq, peptideAreaList);
            }

            // Store the fragment type of those missing value in either pairing (light or heavy) assays
            List<String> unpairedFragmentList = new ArrayList<>();

            for (String id : ids) {
                if (sRd.getIsotopeLabelTypeMap().get(id).equalsIgnoreCase("light")) {
                    // if unpairedFragmentList doesn't contain this fragment ion type and the value is a number, then add to totalLight
                    if (!unpairedFragmentList.contains(sRd.getFragmentIonMap().get(id)) && NumberUtils.isNumber(sRd.getAreaMap().get(id))) {
                        totalLight = totalLight + Double.parseDouble(sRd.getAreaMap().get(id));
                        //peptideAreaList.add(String.valueOf(totalLight));
                    }
                    // if unpairedFragmentList doesn't contain this fragment ton type and the value is not a number, then add this fragment ion type
                    else if (!unpairedFragmentList.contains(sRd.getFragmentIonMap().get(id))) {
                        unpairedFragmentList.add(sRd.getFragmentIonMap().get(id));
                    }

                }
                else if (sRd.getIsotopeLabelTypeMap().get(id).equalsIgnoreCase("heavy")) {
                    if (!unpairedFragmentList.contains(sRd.getFragmentIonMap().get(id)) && NumberUtils.isNumber(sRd.getAreaMap().get(id))) {
                        totalHeavy = totalHeavy + Double.parseDouble(sRd.getAreaMap().get(id));
                        //peptideAreaList.add(String.valueOf(totalHeavy))
                    }
                    else if (!unpairedFragmentList.contains(sRd.getFragmentIonMap().get(id))) {
                        unpairedFragmentList.add(sRd.getFragmentIonMap().get(id));
                    }
                }
            }

            peptideAreaList.add(String.valueOf(totalLight));
            if (sRd.isLabelled()) {
                peptideAreaList.add(String.valueOf(totalHeavy));
            }
        }

        // AssayQuantLayer for peptide level area measurement
        QuantLayer assayQLArea = new QuantLayer();

        assayQLArea.setId("Peptide_AQL_AREA");
        CvParamRef cvParamRef_ref = new CvParamRef();
        CvParam cp_ref = MzQuantMLMarshaller.createCvParam("XIC area", cv, "MS:1001858");
        cvParamRef_ref.setCvParam(cp_ref);
        assayQLArea.setDataType(cvParamRef_ref);

        // Add light assay first to ColumnIndex
        for (String assN : sRd.getAssayList()) {
            if (assN.contains("light")) {
                assayQLArea.getColumnIndex().add(assayNameIdMap.get(assN));
            }
        }

        // Then add heavy assay to ColumnIndex
        for (String assN : sRd.getAssayList()) {
            if (assN.contains("heavy")) {
                assayQLArea.getColumnIndex().add(assayNameIdMap.get(assN));
            }
        }

        if (assayQLArea.getColumnIndex().isEmpty()) {
            throw new IllegalStateException("There is not light or heavy assay!");
        }

        DataMatrix DMArea = new DataMatrix();

        for (String lpepSeq : sRd.getLightPeptideIdMap().keySet()) {
            Row row = new Row();
            row.setObjectRef("pep_" + lpepSeq);
            row.getValue().addAll(peptideAreaMap.get(lpepSeq));
            DMArea.getRow().add(row);
        }

        assayQLArea.setDataMatrix(DMArea);
        peptideConsensuses.getAssayQuantLayer().add(assayQLArea);

        peptideConsensuses.setFinalResult(true);

        //add this peptideConsensuses to data processing step 3 as OutputObject_refs
        DataProcessing dp = dataProcessingList.getDataProcessing().get(2);
        dp.getOutputObjectRefs().add(peptideConsensuses.getId());

        peptideConsensusListList.add(peptideConsensuses);
    }

    /**
     * Assay name always maps to one raw file name not list
     * TODO: check if the above statement is true
     *
     * @param assN assay name
     *
     * @return rawFilesGroup id
     */
    private String getRawFilesGroupIdFromAssayName(String assN) {

        String rawFn = sRd.getAssayToRawFileNameMap().get(assN).get(0);
        String rawId = rawFileNameIdMap.get(rawFn);
        String rgId = rawIdrgIdMap.get(rawId);

        return rgId;
    }

    private void writeMzqFile(File out) {
        FileWriter writer = null;

        try {
            MzQuantMLMarshaller mzqMsh = new MzQuantMLMarshaller();
            writer = new FileWriter(out);

            // XML header
            writer.write("<?xml version=\"1.0\" encoding=\"" + "UTF-8" + "\"?>" + "\n");

            // mzQuantML start tag
            Calendar rightnow = Calendar.getInstance();
            int day = rightnow.get(Calendar.DATE);
            int month = rightnow.get(Calendar.MONTH) + 1;
            int year = rightnow.get(Calendar.YEAR);

            /*
             * set mzQuantML id
             */
            String mzqId = "Skyline-" + String.valueOf(day) + String.valueOf(month) + String.valueOf(year);

            writer.write(MzQuantMLMarshaller.createMzQuantMLStartTag(mzqId) + "\n");

            if (cvs != null) {
                mzqMsh.marshall(cvs, writer);
                writer.write("\n");
            }
            if (auditCollection != null) {
                mzqMsh.marshall(auditCollection, writer);
                writer.write("\n");
            }
            if (analysisSummary != null) {
                mzqMsh.marshall(analysisSummary, writer);
                writer.write("\n");
            }
            if (inputFiles != null) {
                mzqMsh.marshall(inputFiles, writer);
                writer.write("\n");
            }
            if (softwareList != null) {
                mzqMsh.marshall(softwareList, writer);
                writer.write("\n");
            }
            if (dataProcessingList != null) {
                mzqMsh.marshall(dataProcessingList, writer);
                writer.write("\n");
            }
            if (assays != null) {
                mzqMsh.marshall(assays, writer);
                writer.write("\n");
            }
            if (ratioList != null) {
                mzqMsh.marshall(ratioList, writer);
                writer.write("\n");
            }
            if (proteins != null) {
                mzqMsh.marshall(proteins, writer);
                writer.write("\n");
            }
            if (peptideConsensusListList != null) {
                for (PeptideConsensusList pepConList : peptideConsensusListList) {
                    mzqMsh.marshall(pepConList, writer);
                    writer.write("\n");
                }
            }
            if (featureLists != null) {
                for (FeatureList ftList : featureLists) {
                    mzqMsh.marshall(ftList, writer);
                    writer.write("\n");
                }
            }

            writer.write(MzQuantMLMarshaller.createMzQuantMLClosingTag());
        }
        catch (IOException ex) {
            Logger.getLogger(MzqCreateTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                writer.close();
            }
            catch (IOException ex) {
                Logger.getLogger(MzqCreateTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
