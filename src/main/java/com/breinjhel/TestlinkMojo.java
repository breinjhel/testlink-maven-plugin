package com.breinjhel;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.constants.TestLinkResponseParams;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import com.breinjhel.model.TestSuite;
import com.google.inject.internal.util.Maps;
import junit.framework.Test;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by breinjhel on 8/11/16.
 */
@Mojo(name = "report-result")
public class TestlinkMojo extends AbstractMojo{

    @Parameter
    private String url;

    @Parameter
    private String devKey;

    @Parameter
    private String projectName;

    @Parameter
    private String testPlan;

    @Parameter
    private String platform;

    @Parameter(property = "testlink.build")
    private String build;

    @Parameter
    private String outputDir;

    @Parameter
    private String customField;

    @Parameter(defaultValue = "TEST-*.xml")
    private String filename;

    TestLinkAPI api;
    TestProject testProject;
    TestPlan tp;

    public void execute() throws MojoExecutionException{
        try {
            api = new TestLinkAPI(new URL(url), devKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        // Get all testcases in testlink that has custom and automated
        testProject = api.getTestProjectByName(projectName);
        tp = api.getTestPlanByName(testPlan,testProject.getName());
        List<TestCase> tcs;

        getLog().info("TestProject: " + testProject.getName());
        getLog().info("TestPlan: " + tp.getName());


        if(platform != null){
            tcs = getTestcasesByPlatform(tp,platform);
        } else {
            TestCase[] testCases = this.api.getTestCasesForTestPlan(tp.getId(), null, null, null, null, null, null, null,
                    ExecutionType.AUTOMATED, null, TestCaseDetails.FULL);
            tcs = Arrays.asList(testCases);
        }

        Map<String, List<TestCase>> testcasesPerCustomfields = sortTestcasesByCustomFields(tcs);
        // Parse xml junit result
        reportJunitResultsToTestlink(testcasesPerCustomfields);

    }

    private Map<String, List<TestCase>> sortTestcasesByCustomFields(List<TestCase> a){
        Map<String, List<TestCase>> b = new HashMap<>();

        List<String> foundKeys = new ArrayList<>();


        // Get all keys
        a.stream().forEach( testCase -> {
            String key = api.getTestCaseCustomFieldDesignValue(testCase.getId(), null, testCase.getVersion(), testProject.getId(),customField, null).getValue();
            if (key !=null&& !foundKeys.contains(key)) {
                foundKeys.add(key);
                b.put(key, new ArrayList<>());
            }
            b.get(key).add(testCase);
        });

        return b;

    }

    private List<TestCase> getTestcasesByPlatform(TestPlan tp, String platform){
        TestCase[] testCases = this.api.getTestCasesForTestPlan(tp.getId(), null, null, null, null, null, null, null,
                ExecutionType.AUTOMATED, null, TestCaseDetails.FULL);

        List<TestCase> s = Arrays.stream(testCases).filter(a -> a.getPlatform().getName().equals(platform))
                .collect(Collectors.toCollection(ArrayList::new));

        return s;
    }

    private void reportJunitResultsToTestlink(Map<String, List<TestCase>> sortedTestcases){
        File file = new File(outputDir);
        FileFilter fileFilter = new WildcardFileFilter(filename);
        File[] files = file.listFiles(fileFilter);
        if(files != null){
            for(File f: files){
                getLog().info("File - "+ f.getName());
                JAXBContext jaxbContext = null;
                try {
                    jaxbContext = JAXBContext.newInstance(TestSuite.class);

                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    TestSuite ts = (TestSuite) jaxbUnmarshaller.unmarshal(f);
                    com.breinjhel.model.TestCase[] tc = ts.getTestCases();


                    Arrays.stream(tc).forEach(s -> {
                        ExecutionStatus status;
                        String tcName = s.getClassname() + "#" + s.getName();

                        if(s.getError() != null || s.getFailure() != null){
                            status = ExecutionStatus.FAILED;
                        } else {
                            status = ExecutionStatus.PASSED;
                        }
                        Build[] builds = api.getBuildsForTestPlan(tp.getId());
                        if(!Arrays.asList(builds).stream().filter(o -> o.getName().equals(build)).findFirst().isPresent()){
                            api.createBuild(tp.getId(),build,"Created by Testlink Maven Plugin");
                        }

                        if(sortedTestcases.containsKey(tcName)){

                            sortedTestcases.get(tcName).forEach( j -> {
                                if(s.getError() != null || s.getFailure() != null){
                                    String note = s.getError() != null ? s.getError().getMessage() : s.getFailure().getMessage();
                                    getLog().info(tcName + " - Failed");
                                    this.api.reportTCResult(j.getId(), null, tp.getId(), status,null, build, note, null,null,null,platform,null,true);
                                } else {
                                    getLog().info(tcName + " - Passed");
                                    this.api.reportTCResult(j.getId(), null, tp.getId(), status,null, build, "", null,null,null,platform,null,true);

                                }

                            });
                        } else {
                            getLog().info("No test case found for: " + tcName);
                        }
                    });
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }
        } else {
            getLog().warn("No test results found in "+ outputDir + "/" + filename);
        }
    }

}
