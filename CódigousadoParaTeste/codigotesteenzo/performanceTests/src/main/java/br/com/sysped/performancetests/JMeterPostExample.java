package br.com.sysped.performancetests;

import static com.strobel.decompiler.languages.Languages.java;
import java.io.FileOutputStream;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;

import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jmeter.threads.ThreadGroup;

public class JMeterTestedePerformance {

    public static void main(String[] args) throws Exception {

        // Create the Jmeter engine
        StandardJMeterEngine jm = new StandardJMeterEngine();

        // Set some configuration
		String jmeterHome = "C:\\Users\\pagno\\Downloads\\apache-jmeter-5.6.2\\apache-jmeter-5.6.2\\";
        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(jmeterHome + "bin\\jmeter.properties");
        JMeterUtils.initLocale();
        // Create a new hash tree to hold our test elements
        HashTree testPlanTree = new HashTree();

		// Create a sampler
        HTTPSampler httpSamplerOne = new HTTPSampler();
        httpSamplerOne.setDomain("victti-dev.com.br");
        httpSamplerOne.setPort(443);
        httpSamplerOne.setPath("/lolpros/br");
        httpSamplerOne.setMethod("GET");
        httpSamplerOne.setName("HTTP Request");
        httpSamplerOne.setProtocol("https");
        httpSamplerOne.setFollowRedirects(true);
       // httpSamplerOne.addArgument("username", "1233453453");
       //httpSamplerOne.addArgument("passoword", "123123123");
       
        
//        String qqer = JOptionPane.showInputDialog(null, "Insira a senhado coitado");
        httpSamplerOne.setProperty(TestElement.TEST_CLASS, HTTPSampler.class.getName());
        httpSamplerOne.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        // Create a loop controller
        LoopController loopController = new LoopController();
        loopController.setLoops(50);
        loopController.setFirst(true);
      
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();

        // Create a thread group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Thread Group-");
        threadGroup.setNumThreads(12000);
        threadGroup.setRampUp(7*60);
        threadGroup.setDuration(15*60);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS,  ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
       
        // Create a test plan
        TestPlan testPlan = new TestPlan("Ulife fucker");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());
       

        // Add the test plan to our hash tree, this is the top level of our test
        testPlanTree.add(testPlan);

        // Create another hash tree and add the thread group to our test plan
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);

        // Add the http sampler to the hash tree that contains the thread group
        threadGroupHashTree.add(httpSamplerOne);

        // Configure
        jm.configure(testPlanTree);

        // Build a Jmeter test after execution
        SaveService.saveTree(testPlanTree, new FileOutputStream(jmeterHome + "bin\\FirstTest.jmx"));

        // Configure
        jm.configure(testPlanTree);

        // Summariser
        Summariser summariser = null;
        String summariserName = JMeterUtils.getPropDefault("summarise.names", "summary response");
        if (summariserName.length() > 0) {
            summariser = new Summariser(summariserName);
        }
//
        ResultCollector logger = new ResultCollector(summariser);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Write to a file
        ResultCollector rc = new ResultCollector();
        rc.setEnabled(true);
        rc.setErrorLogging(false);
        rc.isSampleWanted(true);
        SampleSaveConfiguration ssc = new SampleSaveConfiguration();
        ssc.setTime(true);
        ssc.setAssertionResultsFailureMessage(true);
        ssc.setThreadCounts(true);
        ssc.setFieldNames(true);
        ssc.setHostname(true);
        rc.setSaveConfig(ssc);
        rc.setFilename(jmeterHome + "bin\\testestressreal2.jtl");
        testPlanTree.add(testPlanTree.getArray()[0], rc);

        // Run
        jm.run();
    }
}
