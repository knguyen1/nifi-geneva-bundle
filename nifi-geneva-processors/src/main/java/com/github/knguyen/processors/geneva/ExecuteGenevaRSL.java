package com.github.knguyen.processors.geneva;

import java.util.Arrays;
import java.util.List;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import com.github.knguyen.processors.utils.StringUtils;

public class ExecuteGenevaRSL extends BaseExecuteGeneva {

    static final PropertyDescriptor RSL_NAME = new PropertyDescriptor.Builder().name("rsl-name").displayName("RSL Name")
            .description(
                    "Specifies the RSL name.  The '.rsl' (dot rsl) extension is not necessary, e.g. `glmap_netassets` for Statement of Net Assets.")
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(true)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR).build();

    @Override
    protected List<PropertyDescriptor> additionalDescriptors() {
        return Arrays.asList(RSL_NAME);
    }

    @Override
    protected String getReportCommand(final ProcessContext context, final FlowFile flowfile) {
        // get and clean the RSL name
        final String rslNameProperty = context.getProperty(RSL_NAME).evaluateAttributeExpressions(flowfile).getValue();
        final String rslName = rslNameProperty.endsWith(".rsl")
                ? rslNameProperty.substring(0, rslNameProperty.length() - 4) : rslNameProperty;

        // format the temporary filename
        final String outputDirectory = context.getProperty(REPORT_OUTPUT_DIRECTORY)
                .evaluateAttributeExpressions(flowfile).getValue();
        final String outputFilename = StringUtils.getGuidFilename(outputDirectory);
        final String reportParameters = getReportParameters(context, flowfile);

        return String.format("read %s.rsl\nrunfile %s -f csv -o \"%s\" %s", rslName, rslName, outputFilename,
                reportParameters);
    }
}
