
package uk.ac.liv.srmconvertor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import uk.ac.liv.jmzqml.model.mzqml.*;
import uk.ac.liv.jmzqml.xml.io.MzQuantMLMarshaller;
import uk.ac.liv.reader.SrmReader;

/**
 * Hello world!
 *
 */
public class SrmConvertor {

    public static void main(String[] args)
            throws IOException {

        File directory = new File(".");
        String path = directory.getCanonicalPath();
//        String inputFn = "src//main//resources//examples//test_skyline_output.csv";
//        String mzqFn = "src//main//resources//examples//test_skyline_output.mzq";

        String inputFn = path + "//src//main//resources//examples//Labelled_SRM_skyline_files//Light_heavy_pairs_test.csv";
        String mzqFn = path + "//src//main//resources//examples//Labelled_SRM_skyline_files//Light_heavy_pairs_test.mzq";

        SrmReader sRd = new SrmReader(new FileReader(inputFn));

        //create MzQuantML instance
        MzQuantML mzq = new MzQuantML();

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

        /**
         * *
         * create CvListType
         */
        CvList cvs = new CvList();
        List<Cv> cvList = cvs.getCv();
        // psi-ms
        Cv cv = new Cv();
        cv.setId("PSI-MS");
        cv.setUri("http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo");
        cv.setFullName("Proteomics Standards Initiative Mass Spectrometry Vocabularies");
        cv.setVersion("2.25.0");
        cvList.add(cv);

        //unimod
        Cv cv_unimod = new Cv();
        cv_unimod.setId("UNIMOD");
        cv_unimod.setUri("http://www.unimod.org/obo/unimod.obo");
        cv_unimod.setFullName("Unimod");
        cvList.add(cv_unimod);

        //psi-mod
        Cv cv_mod = new Cv();
        cv_mod.setId("PSI-MOD");
        cv_mod.setUri("http://psidev.cvs.sourceforge.net/psidev/psi/mod/data/PSI-MOD.obo");
        cv_mod.setFullName("Proteomics Standards Initiative Protein Modifications Vocabularies");
        cvList.add(cv_mod);

        mzq.setCvList(cvs);

        Label label = new Label();
        CvParam labelCvParam = new CvParam();
        labelCvParam.setAccession("MS:1002038");
        labelCvParam.setName("unlabeled sample");
        labelCvParam.setCvRef(cv);
        List<ModParam> modParams = label.getModification();
        ModParam modParam = new ModParam();
        modParam.setCvParam(labelCvParam);
        modParams.add(modParam);

        /*
         * create AnalysisSummary
         */

        ParamList analysisSummary = new ParamList();
        analysisSummary.getParamGroup().add(createCvParam("SRM quantitation analysis", "PSI-MS", "MS:1001838"));

        //TODO: need cv terms
        CvParam analysisSummaryCv = createCvParam("SRM raw feature quantitation", "PSI-MS", "MS:100XXXX");
        analysisSummaryCv.setValue("true");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        analysisSummaryCv = createCvParam("SRM peptide level quantitation", "PSI-MS", "MS:100XXXX");
        analysisSummaryCv.setValue("true");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        analysisSummaryCv = createCvParam("SRM protein level quantitation", "PSI-MS", "MS:100XXXX");
        analysisSummaryCv.setValue("true");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        analysisSummaryCv = createCvParam("SRM proteingroup level quantitation", "PSI-MS", "MS:100XXXX");
        analysisSummaryCv.setValue("false");
        analysisSummary.getParamGroup().add(analysisSummaryCv);

        mzq.setAnalysisSummary(analysisSummary);

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
        aff.setOrganizationRef(uol);
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

        /**
         * *
         * create InputFiles
         */
        InputFiles inputFiles = new InputFiles();
        List<RawFilesGroup> rawFilesGroupList = inputFiles.getRawFilesGroup();

        LinkedHashMap<String, String> rawFileNameIdMap = new LinkedHashMap<String, String>();
        LinkedHashMap<String, ArrayList<String>> rgIdrawIdMap = new LinkedHashMap<String, ArrayList<String>>();
        HashMap<String, String> assayNrgIdMap = new HashMap<String, String>();

        int rawFileCounter = 0;
        //replicate name represents the raw file name
        for (String repN : sRd.getReplicateList()) {
            // create fictional raw file name
            String rawFn = repN + ".raw";

            // create raw file ID
            String rawId = "raw_" + Integer.toString(rawFileCounter);

            RawFilesGroup rawFilesGroup = new RawFilesGroup();
            List<RawFile> rawFiles = rawFilesGroup.getRawFile();

            RawFile rawFile = new RawFile();
            rawFile.setId(rawId);
            rawFile.setName(rawFn);
            /*
             * make up some raw file locations as we don't know the real
             * location from progenesis files
             */
            rawFile.setLocation("../msmsdata/" + rawFn);
            rawFileNameIdMap.put(rawFn, rawId);
            rawFiles.add(rawFile);

            String rgId = "rg_" + Integer.toString(rawFileCounter);
            rawFilesGroup.setId(rgId);
            rawFilesGroupList.add(rawFilesGroup);


//            if (assayNrgIdMap.get(assayN) == null) {
//                assayNrgIdMap.put(assayN, rgId);
//            }

            for (String assayN : sRd.getReplicateToAssayMap().get(repN)) {
                assayNrgIdMap.put(assayN, rgId);
            }
            /*
             * build rgIdrawIdMap raw files group id as key raw file ids
             * (ArrayList) as value
             */
            ArrayList<String> rawIds = rgIdrawIdMap.get(rgId);
            if (rawIds == null) {
                rawIds = new ArrayList<String>();
                rgIdrawIdMap.put(rgId, rawIds);
            }
            rawIds.add(rawId);

            rawFileCounter++;
        }

        //add search databases
        List<SearchDatabase> searchDBs = inputFiles.getSearchDatabase();
        SearchDatabase db = new SearchDatabase();
        db.setId("SD1");
        db.setLocation("sgd_orfs_plus_ups_prots.fasta");
        searchDBs.add(db);
        Param dbName = new Param();
        db.setDatabaseName(dbName);
        UserParam dbNameParam = new UserParam();
        dbNameParam.setName("sgd_orfs_plus_ups_prots.fasta");
        dbName.setParamGroup(dbNameParam);

        mzq.setInputFiles(inputFiles);

        /**
         * *
         * create AssayList
         */
        AssayList assays = new AssayList();
        assays.setId("AssayList_1");
        List<Assay> assayList = assays.getAssay();
        int assayCounter = 0;
        HashMap<String, String> assayNameIdMap = new HashMap<String, String>();
        for (String assayN : sRd.getAssayList()) {
            Assay assay = new Assay();
            String assayId = "ass_" + Integer.toString(assayCounter);
            assay.setId(assayId);
            assay.setName(assayN);
            assay.setLabel(label);
            assayList.add(assay);

            assayNameIdMap.put(assayN, assayId);

            //TODO: it is a temporary solution
            RawFilesGroup rawFilesGroup = new RawFilesGroup();
            rawFilesGroup.setId(assayNrgIdMap.get(assayN));
            assay.setRawFilesGroupRef(rawFilesGroup);
        }
        mzq.setAssayList(assays);

        /**
         * *
         * create SoftwareList
         */
        SoftwareList softwareList = new SoftwareList();
        Software software = new Software();
        softwareList.getSoftware().add(software);
        software.setId("Skyline");
        software.setVersion("1.4");
        software.getCvParam().add(createCvParam("Skyline", "PSI-MS", "MS:1000922"));
        mzq.setSoftwareList(softwareList);

        /**
         * *
         * create DataProcessingList
         */
        DataProcessingList dataProcessingList = new DataProcessingList();
        DataProcessing dataProcessing = new DataProcessing();
        dataProcessing.setId("feature_quantification");
        dataProcessing.setSoftwareRef(software);
        dataProcessing.setOrder(BigInteger.ONE);
        ProcessingMethod processingMethod = new ProcessingMethod();
        processingMethod.setOrder(BigInteger.ONE);
        processingMethod.getParamGroup().add(createCvParam("quantification data processing", "PSI-MS", "MS:1001861"));
        dataProcessing.getProcessingMethod().add(processingMethod);

        dataProcessingList.getDataProcessing().add(dataProcessing);
        mzq.setDataProcessingList(dataProcessingList);

        /**
         * *
         * create ProteinList
         */
        // get protein to peptide map from SRMReader
        LinkedHashMap<String, ArrayList<String>> protToPepMap = sRd.getProteinToPeptideMap();

        ProteinList proteins = new ProteinList();
        proteins.setId("ProtList1");
        List<Protein> proteinList = proteins.getProtein();
        int protCounter = 0;
        for (String protAcc : sRd.getProteinList()) {

            Protein protein = new Protein();

            // set protein ID
            protein.setId("prot_" + Integer.toString(protCounter));

            // set protein accession
            protein.setAccession(protAcc);
            protein.setSearchDatabaseRef(db);

            ArrayList<String> pepSeqs = protToPepMap.get(protAcc);

            if (pepSeqs != null) {

                List<Object> peptideConsensusRefList = protein.getPeptideConsensusRefs();
                ArrayList<String> pepIds = new ArrayList();

                for (String pepSeq : pepSeqs) {
                    PeptideConsensus pepCon = new PeptideConsensus();
                    pepCon.setId("pep_" + pepSeq);
                    pepCon.setPeptideSequence(pepSeq);

                    if (!pepIds.contains("pep_" + pepSeq)) {
                        peptideConsensusRefList.add(pepCon);
                        pepIds.add("pep_" + pepSeq);
                    }
                }
            }

            proteinList.add(protein);
        }
        mzq.setProteinList(proteins);

        /**
         * *
         * create FeatureList
         */
        List<FeatureList> featureLists = mzq.getFeatureList();
        HashMap<String, FeatureList> rgIdFeatureListMap = new HashMap<String, FeatureList>();

        // for each feature
        for (String id : sRd.getAreaMap().keySet()) {

            // deal with feature
            Feature feature = new Feature();
            feature.setId("ft_" + id);

            // get product charge
            String charge = sRd.getProductChargeMap().get(id);
            feature.setCharge(charge);

            // get product m/z
            String mz = sRd.getProductMzMap().get(id);
            feature.setMz(Double.valueOf(mz));

            // get retention time
            String rt = sRd.getRetentionTimeMap().get(id);
            feature.setRt(rt);

            // set cv term
            //feature.getCvParam().add(createCvParam("Q3 m/z", "PSI-MS", "MS:100XXXX"));


            // find out this feature belongs to which raw files group via assay name
            String assayN = sRd.getAssayMap().get(id);
            String rgId = assayNrgIdMap.get(assayN);

            // get a FeatureList from rgIdFeatureListMap first
            FeatureList featureList = rgIdFeatureListMap.get(rgId);
            if (featureList == null) {
                featureList = new FeatureList();
                RawFilesGroup rawFilesGroup = new RawFilesGroup();
                rawFilesGroup.setId(rgId);
                featureList.setRawFilesGroupRef(rawFilesGroup);
                String fListId = "Flist_" + rgId.substring(3);
                featureList.setId(fListId);
                rgIdFeatureListMap.put(rgId, featureList);
                featureLists.add(featureList);

                List<GlobalQuantLayer> featureQuantLayers = featureList.getFeatureQuantLayer();
                GlobalQuantLayer featureQuantLayer = new GlobalQuantLayer();
                featureQuantLayers.add(featureQuantLayer);
                featureQuantLayer.setId("FQL_" + fListId.substring(6));
                ColumnDefinition featureColumnIndex = new ColumnDefinition();
                featureQuantLayer.setColumnDefinition(featureColumnIndex);

                Column column = new Column();
                column.setIndex(BigInteger.ZERO);
                CvParamRef cvParamRef = new CvParamRef();

                //cv term for Q3 m/z
                cvParamRef.setCvParam(createCvParam("Q3 m/z", "PSI-MS", "MS:100XXXX"));
                column.setDataType(cvParamRef);

                featureColumnIndex.getColumn().add(column);

                // deal with DMArea for GlobalQuantLayer
                DataMatrix DMArea = new DataMatrix();

                Row row = new Row();
                row.setObjectRef(feature);
                String area = sRd.getAreaMap().get(id);
                row.getValue().add(area);

                DMArea.getRow().add(row);
                featureQuantLayer.setDataMatrix(DMArea);
            }
            else {
                // deal with DMArea for GlobalQuantLayer
                Row row = new Row();
                row.setObjectRef(feature);
                String area = sRd.getAreaMap().get(id);
                row.getValue().add(area);

                featureList.getFeatureQuantLayer().get(0).getDataMatrix().getRow().add(row);
            }

            featureList.getFeature().add(feature);

        }

        /**
         * *
         * create PeptideConsensusList
         */
        List<PeptideConsensusList> peptideConsensusListList = mzq.getPeptideConsensusList();

        PeptideConsensusList peptideConsensuses = new PeptideConsensusList();
        peptideConsensuses.setId("PepList1");
        List<PeptideConsensus> peptideList = peptideConsensuses.getPeptideConsensus();
        for (String pepSeq : sRd.getPeptideList()) {
            PeptideConsensus pepCon = new PeptideConsensus();
            pepCon.setId("pep_" + pepSeq);
            pepCon.setPeptideSequence(pepSeq);
            pepCon.setSearchDatabaseRef(db);

            //add peptide charge list
            ArrayList<String> ids = sRd.getPeptideIdMap().get(pepSeq);
            ArrayList<String> charges = new ArrayList<String>();
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
                evidenceRef.setFeatureRef(feature);

                // set AssayRefs to evidenceRef
                String assayN = sRd.getAssayMap().get(id);
                String assayId = assayNameIdMap.get(assayN);
                Assay assay = new Assay();
                assay.setId(assayId);
                if (!evidenceRef.getAssayRefs().contains(assay)) {
                    evidenceRef.getAssayRefs().add(assay);
                }
            }
            peptideList.add(pepCon);
        }
        peptideConsensuses.setFinalResult(true);
        peptideConsensusListList.add(peptideConsensuses);

        /**
         * *
         * create a Marshaller and marshal to File
         */
        MzQuantMLMarshaller marshaller = new MzQuantMLMarshaller(mzqFn);
        marshaller.marshall(mzq);
    }

    // private methods
    private static CvParam createCvParam(String name, String cvRef,
                                         String accession) {
        CvParam cp = new CvParam();
        cp.setName(name);
        Cv cv = new Cv();
        cv.setId(cvRef);
        cp.setCvRef(cv);
        cp.setAccession(accession);
        return cp;
    }

}
