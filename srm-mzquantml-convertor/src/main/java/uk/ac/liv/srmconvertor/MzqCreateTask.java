/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.srmconvertor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import uk.ac.liv.jmzqml.model.mzqml.Affiliation;
import uk.ac.liv.jmzqml.model.mzqml.AnalysisSummary;
import uk.ac.liv.jmzqml.model.mzqml.Assay;
import uk.ac.liv.jmzqml.model.mzqml.AssayList;
import uk.ac.liv.jmzqml.model.mzqml.AuditCollection;
import uk.ac.liv.jmzqml.model.mzqml.Column;
import uk.ac.liv.jmzqml.model.mzqml.ColumnDefinition;
import uk.ac.liv.jmzqml.model.mzqml.Cv;
import uk.ac.liv.jmzqml.model.mzqml.CvList;
import uk.ac.liv.jmzqml.model.mzqml.CvParam;
import uk.ac.liv.jmzqml.model.mzqml.CvParamRef;
import uk.ac.liv.jmzqml.model.mzqml.DataMatrix;
import uk.ac.liv.jmzqml.model.mzqml.DataProcessing;
import uk.ac.liv.jmzqml.model.mzqml.DataProcessingList;
import uk.ac.liv.jmzqml.model.mzqml.EvidenceRef;
import uk.ac.liv.jmzqml.model.mzqml.Feature;
import uk.ac.liv.jmzqml.model.mzqml.FeatureList;
import uk.ac.liv.jmzqml.model.mzqml.GlobalQuantLayer;
import uk.ac.liv.jmzqml.model.mzqml.InputFiles;
import uk.ac.liv.jmzqml.model.mzqml.Label;
import uk.ac.liv.jmzqml.model.mzqml.ModParam;
import uk.ac.liv.jmzqml.model.mzqml.MzQuantML;
import uk.ac.liv.jmzqml.model.mzqml.Organization;
import uk.ac.liv.jmzqml.model.mzqml.Param;
import uk.ac.liv.jmzqml.model.mzqml.PeptideConsensus;
import uk.ac.liv.jmzqml.model.mzqml.PeptideConsensusList;
import uk.ac.liv.jmzqml.model.mzqml.Person;
import uk.ac.liv.jmzqml.model.mzqml.ProcessingMethod;
import uk.ac.liv.jmzqml.model.mzqml.Protein;
import uk.ac.liv.jmzqml.model.mzqml.ProteinList;
import uk.ac.liv.jmzqml.model.mzqml.Ratio;
import uk.ac.liv.jmzqml.model.mzqml.RatioList;
import uk.ac.liv.jmzqml.model.mzqml.RatioQuantLayer;
import uk.ac.liv.jmzqml.model.mzqml.RawFile;
import uk.ac.liv.jmzqml.model.mzqml.RawFilesGroup;
import uk.ac.liv.jmzqml.model.mzqml.Row;
import uk.ac.liv.jmzqml.model.mzqml.SearchDatabase;
import uk.ac.liv.jmzqml.model.mzqml.Software;
import uk.ac.liv.jmzqml.model.mzqml.SoftwareList;
import uk.ac.liv.jmzqml.model.mzqml.UserParam;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLMarshaller;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 13-Jun-2014 14:55:53
 */
public class MzqCreateTask extends Task<Void> {

    private final String in;
    private final File out;

    private static SrmReader sRd;
    private static MzQuantMLMarshaller marshaller;
    private static MzQuantML mzq;
    private static Cv cv;
    private static Cv cv_mod;
    private static Cv cv_unimod;
    private static Cv cv_uo;
    private static Label label;
    private static Label label_heavy;
    private static SearchDatabase db;
    private static HashMap<String, ArrayList<String>> rgIdrawIdMap;
    private static HashMap<String, String> rawIdrgIdMap;
    private static HashMap<String, String> rawFileNameIdMap;
    private static HashMap<String, String> rawFileIdNameMap;
    private static HashMap<String, String> assayNameIdMap;
    private static HashMap<String, String> assayIdNameMap;

    public MzqCreateTask(String input, File output) {
        this.in = input;
        this.out = output;
    }

    @Override
    protected Void call()
            throws Exception {
        updateMessage("Loading file ...");
        updateProgress(-1.0, 1.0);

        try {
            sRd = new SrmReader(new FileReader(in));

            updateMessage("Start converting ...");

            for (int i = 0; i < 100000; i++) {
                for (int j = 0; j < 10000; j++) {
                    if (((i + j) % 333) == 0) {
                        updateMessage("i = " + i + ", j = " + j + " ......");
                    }
                }
            }

            marshaller = new MzQuantMLMarshaller(out.getAbsolutePath());

            //create MzQuantML instance
            mzq = new MzQuantML();

            String version = "1.0.0";
            mzq.setVersion(version);

            Calendar rightnow = Calendar.getInstance();
            mzq.setCreationDate(rightnow);

            int day = rightnow.get(Calendar.DATE);
            int month = rightnow.get(Calendar.MONTH) + 1;
            int year = rightnow.get(Calendar.YEAR);

            /*
             * set mzQuantML id
             */
            mzq.setId("SRM-" + String.valueOf(day) + String.valueOf(month) + String.valueOf(year));

            createCvList();

            createAnalysisSummary();

            createAuditCollection();

            createInputFiles();

            createAssayList();

            createSoftwareList();

            createDataProcessingList();

            createProteinList();

            createFeatureList();

            if (sRd.isLabelled() && (sRd.hasPeptideRatio() || sRd.hasTotalAreaRatio())) {
                createRatioList();
            }

            createPeptideconsensusList();

            /**
             * *
             * create a Marshaller and marshal to File
             */
            marshaller.marshall(mzq);

            updateProgress(1.0, 1.0);
            updateMessage("Converting complete.");
        }
        catch (IOException ex) {
            Logger.getLogger(MzqCreateTask.class.getName()).log(Level.SEVERE, null, ex);
            updateMessage("File loading failed, please check the input file.");
            updateProgress(0, 1.0);
        }
        catch (NullPointerException nex) {
            Logger.getLogger(MzqCreateTask.class.getName()).log(Level.SEVERE, null, nex);
            updateMessage("No input file is selected.");
            updateProgress(0, 1.0);
        }
        catch (Exception e) {
            Logger.getLogger(MzqCreateTask.class.getName()).log(Level.SEVERE, null, e);
            updateMessage("File loading failed, please check the input file!");
            updateProgress(0, 1.0);
        }

        return null;
    }

// private methods
    private static void createCvList() {
        /**
         * create CvListType
         */
        CvList cvs = new CvList();
        List<Cv> cvList = cvs.getCv();
        // psi-ms

        cv = marshaller.createCv("PSI-MS",
                                 "Proteomics Standards Initiative Mass Spectrometry Vocabularies",
                                 "http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo",
                                 "2.25.0");
        cvList.add(cv);

        //unimod
        cv_unimod = marshaller.createCv("UNIMOD", "Unimod", "http://www.unimod.org/obo/unimod.obo", null);
        cvList.add(cv_unimod);

        //psi-mod
        cv_mod = marshaller.createCv("PSI-MOD",
                                     "Proteomics Standards Initiative Protein Modifications Vocabularies",
                                     "http://psidev.cvs.sourceforge.net/psidev/psi/mod/data/PSI-MOD.obo",
                                     null);
        cvList.add(cv_mod);

        //unit ontology
        cv_uo = marshaller.createCv("UO", "Unit Ontology", "http://obo.cvs.sourceforge.net/viewvc/obo/obo/ontology/phenotype/unit.obo", null);
        cvList.add(cv_uo);

        mzq.setCvList(cvs);

        //light label
        label = new uk.ac.liv.jmzqml.model.mzqml.Label();
        CvParam labelCvParam = marshaller.createCvParam("unlabeled sample", cv, "MS:1002038");
        ModParam modParam = new ModParam();
        modParam.setCvParam(labelCvParam);
        label.getModification().add(modParam);

        //heavy label
        //TODO: to detect the heavy label from the file or get it from Skyline setting
        label_heavy = new uk.ac.liv.jmzqml.model.mzqml.Label();
        CvParam label_silac = marshaller.createCvParam("13C(6) Silac label", cv_mod, "MOD:00188", "6.020129", "", "", "");
        ModParam modParam_silac = new ModParam();
        modParam_silac.setCvParam(label_silac);
        label_heavy.getModification().add(modParam_silac);
    }

    private static void createAnalysisSummary() {
        /*
         * create AnalysisSummary
         */

        AnalysisSummary analysisSummary = new AnalysisSummary();
        analysisSummary.getParamGroup().add(marshaller.createCvParam("SRM quantitation analysis", cv, "MS:1001838"));

        if (sRd.isLabelled()) {
            CvParam labelBasedCv = marshaller.createCvParam("MS1 Label-based analysis", cv, "MS:1002018");
            analysisSummary.getParamGroup().add(labelBasedCv);
        }
        else {
            CvParam labelFreeCv = marshaller.createCvParam("LC-MS label-free quantitation analysis", cv, "MS:1001834");
            analysisSummary.getParamGroup().add(labelFreeCv);
        }
        //TODO: need cv terms
        CvParam analysisSummaryCv = marshaller.createCvParam("SRM feature level quantitation", cv, "MS:1002281", "true", "", "", "");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        analysisSummaryCv = marshaller.createCvParam("SRM peptide level quantitation", cv, "MS:1002282", "true", "", "", "");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        analysisSummaryCv = marshaller.createCvParam("SRM protein level quantitation", cv, "MS:1002283", "true", "", "", "");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        analysisSummaryCv = marshaller.createCvParam("SRM proteingroup level quantitation", cv, "MS:1002284", "false", "", "", "");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        mzq.setAnalysisSummary(analysisSummary);
    }

    private static void createAuditCollection() {
        /**
         * create AuditCollection
         */
        AuditCollection auditCollection = new AuditCollection();

        Organization uol = new Organization();
        uol.setId("ORG_UOL");
        uol.setName("University of Liverpool");

        Person andy = new Person();
        andy.setFirstName("Andy");
        andy.setLastName("Jones");

        Affiliation aff = new Affiliation();
        aff.setOrganization(uol);
        andy.getAffiliation().add(aff);
        andy.setId("PERS_ARJ");
        auditCollection.getPerson().add(andy);

        Person ddq = new Person();
        ddq.setFirstName("Da");
        ddq.setLastName("Qi");
        ddq.setId("PERS_DQ");
        ddq.getAffiliation().add(aff);
        auditCollection.getPerson().add(ddq);

        // the schema require person before organization
        auditCollection.getOrganization().add(uol);

        mzq.setAuditCollection(auditCollection);
    }

    private static void createInputFiles() {
        /**
         * *
         * create InputFiles
         */
        InputFiles inputFiles = new InputFiles();
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
//        HashMap<String, String> assayNrgIdMap = new HashMap<String, String>(); //jmzquantml 1.0.0-1.0.0

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

            // jmzquantml 1.0.0-1.0.0
//            if (assayNrgIdMap.get(assayN) == null) {
//                assayNrgIdMap.put(assayN, rgId);
//            }
//            for (String assayN : sRd.getRawFileNameToAssayMap().get(rawFn)) {
//                assayNrgIdMap.put(assayN, rgId);
//            }
            /*
             * build rgIdrawIdMap raw files group id as key raw file ids
             * (ArrayList) as value
             */
            ArrayList<String> rawIds = rgIdrawIdMap.get(rgId);
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

        mzq.setInputFiles(inputFiles);
    }

    private static void createAssayList() {
        /**
         * *
         * create AssayList
         */
        AssayList assays = new AssayList();
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

            //rawFilesGroup.setId(assayNrgIdMap.get(assayN)); //jmzquantml 1.0.0-1.0.0
            assay.setRawFilesGroup(rawFilesGroup);
        }
        mzq.setAssayList(assays);
    }

    private static void createSoftwareList() {
        /**
         * *
         * create SoftwareList
         */
        SoftwareList softwareList = new SoftwareList();
        Software software = new Software();
        softwareList.getSoftware().add(software);
        software.setId("Skyline");
        software.setVersion("1.4");
        software.getCvParam().add(marshaller.createCvParam("Skyline", "PSI-MS", "MS:1000922"));
        mzq.setSoftwareList(softwareList);
    }

    private static void createDataProcessingList() {
        /**
         * *
         * create DataProcessingList
         */
        DataProcessingList dataProcessingList = new DataProcessingList();
        DataProcessing dataProcessing = new DataProcessing();
        dataProcessing.setId("feature_quantification");
        //dataProcessing.setSoftware(software); //jmzquantml 1.0.0-1.0.0
        dataProcessing.setSoftware(mzq.getSoftwareList().getSoftware().get(0));
        dataProcessing.setOrder(BigInteger.ONE);
        ProcessingMethod processingMethod = new ProcessingMethod();
        processingMethod.setOrder(BigInteger.ONE);
        processingMethod.getParamGroup().add(marshaller.createCvParam("quantification data processing", "PSI-MS", "MS:1001861"));
        dataProcessing.getProcessingMethod().add(processingMethod);

        dataProcessingList.getDataProcessing().add(dataProcessing);
        mzq.setDataProcessingList(dataProcessingList);
    }

    private static void createProteinList() {
        /**
         * *
         * create ProteinList
         */
        // get protein to peptide map from SRMReader
        HashMap<String, ArrayList<String>> protToPepMap = sRd.getProteinToPeptideMap();

        ProteinList proteins = new ProteinList();
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

            ArrayList<String> pepSeqs = protToPepMap.get(protAcc);

            if (pepSeqs != null) {

                //List<Object> peptideConsensusRefList = protein.getPeptideConsensusRefs();  //jmzquantml 1.0.0-1.0.0
                //List<PeptideConsensus> peptides = new ArrayList();
                List<String> peptideIds = protein.getPeptideConsensusRefs();
                ArrayList<String> pepIds = new ArrayList();

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
        mzq.setProteinList(proteins);
    }

    private static void createFeatureList() {
        /**
         * *
         * create FeatureList
         */
        List<FeatureList> featureLists = mzq.getFeatureList();
        HashMap<String, FeatureList> rgIdFeatureListMap = new HashMap<>();

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
            CvParam cpMz = marshaller.createCvParam("isolation window target m/z", "PSI-MS", "MS:1000827");
            cpMz.setValue(proMz);
            feature.getCvParam().add(cpMz);

            // set cv term for Q3 charge
            CvParam cpCharge = marshaller.createCvParam("charge state", "PSI-MS", "MS:1000041");
            cpCharge.setValue(proCharge);
            feature.getCvParam().add(cpCharge);

            // set cv term for Q3 rt
            CvParam cpRt = marshaller.createCvParam("local retention time", "PSI-MS", "MS:1000895");
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
                cpRefArea.setCvParam(marshaller.createCvParam("XIC area", "PSI-MS", "MS:1001858"));
                columnArea.setDataType(cpRefArea);

                featureColumnIndex.getColumn().add(columnArea);

                /*
                 * The second column for Q3 background
                 */
                Column columnBg = new Column();
                columnBg.setIndex(BigInteger.ONE);
                CvParamRef cpRefBg = new CvParamRef();

                //cv term for Q3 background
                cpRefBg.setCvParam(marshaller.createCvParam("product background", "PSI-MS", "MS:1002414"));
                columnBg.setDataType(cpRefBg);

                featureColumnIndex.getColumn().add(columnBg);

                /*
                 * The third column for Q3 peakrank
                 */
                Column columnPr = new Column();
                columnPr.setIndex(BigInteger.valueOf(2));
                CvParamRef cpRefPr = new CvParamRef();

                //cv term for Q3 background
                cpRefPr.setCvParam(marshaller.createCvParam("product interpretation rank", "PSI-MS", "MS:1000926"));
                columnPr.setDataType(cpRefPr);

                featureColumnIndex.getColumn().add(columnPr);

                /*
                 * The fourth column for Q3 height
                 */
                Column columnHt = new Column();
                columnHt.setIndex(BigInteger.valueOf(3));
                CvParamRef cpRefHt = new CvParamRef();

                //cv term for Q3 height
                cpRefHt.setCvParam(marshaller.createCvParam("peak intensity", "PSI-MS", "MS:1000042"));
                columnHt.setDataType(cpRefHt);

                featureColumnIndex.getColumn().add(columnHt);

                /*
                 * The fifth column for Q3 AreaNormalized
                 */
                Column columnAn = new Column();
                columnAn.setIndex(BigInteger.valueOf(4));
                CvParamRef cpRefAn = new CvParamRef();

                //cv term for Q3 AreaNormalized
                cpRefAn.setCvParam(marshaller.createCvParam("normalized XIC area", "PSI-MS", "MS:1001859"));
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

    private static void createRatioList() {
        /**
         * *
         * create RatioList
         */
        RatioList ratioList = new RatioList();
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
        denominator_cpRef.setCvParam(marshaller.createCvParam("XIC area", cv, "MS:1001858"));
        pepRatio.setDenominatorDataType(denominator_cpRef);

        CvParamRef numerator_cpRef = new CvParamRef();
        numerator_cpRef.setCvParam(marshaller.createCvParam("XIC area", cv, "MS:1001858"));
        pepRatio.setNumeratorDataType(numerator_cpRef);

        //add to RatioList
        ratioList.getRatio().add(pepRatio);

        mzq.setRatioList(ratioList);
    }

    private static void createPeptideconsensusList() {
        /**
         * *
         * create PeptideConsensusList
         */
        List<PeptideConsensusList> peptideConsensusListList = mzq.getPeptideConsensusList();

        PeptideConsensusList peptideConsensuses = new PeptideConsensusList();
        peptideConsensuses.setId("PepList1");

        /*
         * set RatioQuantLayer
         */
        DataMatrix pepRatioDM = new DataMatrix();
        if (sRd.isLabelled() && (sRd.hasPeptideRatio() || sRd.hasTotalAreaRatio())) {

            RatioQuantLayer pepRQL = new RatioQuantLayer();
            pepRQL.setId("PepRQL_1");
            //pepRQL.getColumnIndex().add(ratioList.getRatio().get(0));   //jmzquantml 1.0.0-1.0.0
            for (Ratio rat : mzq.getRatioList().getRatio()) {
                pepRQL.getColumnIndex().add(rat.getId());
            }
            //pepRQL.getColumns().add(mzq.getRatioList().getRatio().get(0));

            pepRQL.setDataMatrix(pepRatioDM);
            peptideConsensuses.setRatioQuantLayer(pepRQL);
        }
        List<PeptideConsensus> peptideList = peptideConsensuses.getPeptideConsensus();
        for (String pepSeq : sRd.getPeptideList()) {
            PeptideConsensus pepCon = new PeptideConsensus();
            pepCon.setId("pep_" + pepSeq);
            pepCon.setPeptideSequence(pepSeq);
            pepCon.setSearchDatabase(db);

            //add peptide charge list
            ArrayList<String> ids = sRd.getPeptideIdMap().get(pepSeq);
            ArrayList<String> charges = new ArrayList<>();
            for (String id : ids) {
                String charge = sRd.getPrecursorChargeMap().get(id);
                if (!charges.contains(charge)) {
                    charges.add(charge);
                }
            }
            pepCon.getCharge().addAll(charges);

            for (String id : sRd.getPeptideIdMap().get(pepSeq)) {
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
//                if (!evidenceRef.getAssayRefs().contains(assay)) {
//                    evidenceRef.getAssayRefs().add(assay);
//                }    //jmzquantml 1.0.0-1.0.0

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
            if (sRd.hasPeptideRatio()) {
                Row row = new Row();
                row.setObject(pepCon);
                String ratio = sRd.getPeptideToRatioMap().get(pepSeq);
                row.getValue().add(ratio);
                pepRatioDM.getRow().add(row);
            }
            else if (sRd.hasTotalAreaRatio()) {
                Row row = new Row();
                row.setObject(pepCon);
                String ratio = sRd.getPeptideToTotalAreaRatioMap().get(pepSeq);
                row.getValue().add(ratio);
                pepRatioDM.getRow().add(row);
            }
        }

        peptideConsensuses.setFinalResult(true);
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
    private static String getRawFilesGroupIdFromAssayName(String assN) {

        String rawFn = sRd.getAssayToRawFileNameMap().get(assN).get(0);
        String rawId = rawFileNameIdMap.get(rawFn);
        String rgId = rawIdrgIdMap.get(rawId);

        return rgId;
    }

}
