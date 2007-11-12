package org.esa.beam.framework.gpf.main;

import com.bc.ceres.binding.ConversionException;
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.binding.dom.DomConverter;
import com.bc.ceres.binding.dom.DomElement;
import junit.framework.TestCase;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.*;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.graph.Graph;
import org.esa.beam.framework.gpf.graph.GraphException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandLineToolOperatorTest extends TestCase {
    private OpCommandLineContext context;
    private CommandLineTool clTool;
    private static final TestOps.Op3.Spi OP_SPI = new TestOps.Op3.Spi();

    @Override
    protected void setUp() throws Exception {
        context = new OpCommandLineContext();
        clTool = new CommandLineTool(context);
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(OP_SPI);
    }

    @Override
    protected void tearDown() throws Exception {
        GPF.getDefaultInstance().getOperatorSpiRegistry().removeOperatorSpi(OP_SPI);
    }

    public void testPrintUsage() throws Exception {
        assertTrue(context.output.length() == 0);
        clTool.run(new String[]{"-h"});
        assertTrue(context.output.startsWith("Usage:\n  gpt <op>|<graph-file> [options] "));

        System.out.println("\n" + context.output + "\n");
    }

    public void testPrintOperatorUsage() throws Exception {
        assertTrue(context.output.length() == 0);
        clTool.run(new String[]{"Op3", "-h"});
        assertTrue(context.output.startsWith("Usage:\n  gpt Op3 [options] "));

        System.out.println("\n" + context.output + "\n");
    }

    public void testOperatorSingleSource() throws Exception {
        clTool.run(new String[]{"Op3", "-Sinput1=vercingetorix.dim"});
        assertEquals("s0=" + new File("vercingetorix.dim").getCanonicalPath() + ";o=Op3;t0=" + CommandLineTool.DEFAULT_TARGET_FILEPATH + ";", context.logString);
        assertEquals("Op3", context.opName);
    }

    public void testOperatorTwoSources() throws Exception {
        clTool.run(new String[]{"Op3", "-Sinput1=vercingetorix.dim", "-Sinput2=asterix.N1"});
        String expectedLog = "s0=" + new File("vercingetorix.dim").getCanonicalPath() + ";" +
                "s1=" + new File("asterix.N1").getCanonicalPath() + ";" +
                "o=Op3;t0=" + CommandLineTool.DEFAULT_TARGET_FILEPATH + ";";
        assertEquals(expectedLog, context.logString);
        assertEquals("Op3", context.opName);
        assertNotNull(context.parameters);
    }

    public void testOperatorTargetProduct() throws Exception {
        clTool.run(new String[]{"Op3", "-t", "obelix.dim"});
        assertEquals("o=Op3;t0=obelix.dim;", context.logString);
        assertEquals("Op3", context.opName);
        assertNotNull(context.parameters);
    }

    public void testOperatorWithParameters() throws Exception {
        clTool.run(new String[]{"Op3", "-Pexpression=log(1+radiance_13)", "-PignoreSign=true", "-Pfactor=-0.025"});
        assertEquals("o=Op3;t0=" + CommandLineTool.DEFAULT_TARGET_FILEPATH + ";", context.logString);
        assertEquals("Op3", context.opName);

        Map<String, Object> parameters = context.parameters;
        assertNotNull(parameters);
        assertEquals(4, parameters.size());
        assertEquals("log(1+radiance_13)", parameters.get("expression"));
        assertEquals(true, parameters.get("ignoreSign"));
        assertEquals(-0.025, parameters.get("factor"));
    }

    public void testFailureNoReaderFound() {
        CommandLineTool tool = new CommandLineTool(new OpCommandLineContext() {
            @Override
            public Product readProduct(String productFilepath) throws IOException {
                return null;  // returning null to simulate an error
            }

        });
        try {
            tool.run(new String[]{"Op3", "-Sinput1=vercingetorix.dim", "-Sinput2=asterix.N1"});
            fail("Exception expected for reason: " + "No reader found");
        } catch (Exception e) {
            // expected
        }

    }

    @OperatorMetadata(alias = "domop")
    static class DomTestOp extends Operator{

        @Parameter (domConverter = DomTestOpConverter.class)
        DomTestOpConfig config;

        public void initialize() throws OperatorException {
            setTargetProduct(new Product("a", "b", 10, 10));
        }
    }
     static class DomTestOpSpi extends OperatorSpi{
         DomTestOpSpi() {
             super(DomTestOp.class);
         }
     }

    static class DomTestOpConfig {
        String name;
        double value;
        int ref;
    }

    static class DomTestOpConverter implements DomConverter {
        public Object convertDomToValue(DomElement parentElement, Object value) throws ConversionException, ValidationException {
            return null;
        }

        public void convertValueToDom(Object value, DomElement parentElement) {
        }

        public Class getValueType() {
            return DomTestOpConfig.class;
        }
    }


    public void testOperatorWithDomConverter() throws Exception {
        final DomTestOpSpi spi = new DomTestOpSpi();
        final OperatorSpiRegistry spiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
        spiRegistry.addOperatorSpi(spi);

        clTool.run(new String[]{"domop", "-h"});

        assertNotNull(context.output);
        assertTrue(context.output.contains(CommandLineUsage.CANNOT_GEN_XML_MESSAGE));
    }

    private static class OpCommandLineContext implements CommandLineContext {
        public String logString;
        private int readProductCounter;
        private int writeProductCounter;
        private String opName;
        private Map<String, Object> parameters;
        private Map<String, Product> sourceProducts;
        private String output = "";

        public OpCommandLineContext() {
            logString = "";
        }

        public Product readProduct(String productFilepath) throws IOException {
            logString += "s" + readProductCounter + "=" + productFilepath + ";";
            readProductCounter++;
            return new Product("S", "ST", 10, 10);
        }

        public void writeProduct(Product targetProduct, String filePath, String formatName) throws IOException {
            logString += "t" + writeProductCounter + "=" + filePath + ";";
            writeProductCounter++;
        }

        public Graph readGraph(String filepath, Map<String, String> parameterMap) throws IOException {
            fail("did not expect to come here");
            return null;
        }

        public void executeGraph(Graph graph) throws GraphException {
            fail("did not expect to come here");
        }

        public Map<String, String> readParameterFile(String propertiesFilepath) throws IOException {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("expression", "sqrt(x*x + y*y)");
            hashMap.put("threshold", "-0.5125");
            return hashMap;
        }

        public Product createOpProduct(String opName, Map<String, Object> parameters, Map<String, Product> sourceProducts) throws OperatorException {
            this.opName = opName;
            this.parameters = parameters;
            this.sourceProducts = sourceProducts;
            logString += "o=" + opName + ";";
            return new Product("T", "TT", 10, 10);
        }

        public void print(String m) {
            this.output += m;
        }
    }

}
