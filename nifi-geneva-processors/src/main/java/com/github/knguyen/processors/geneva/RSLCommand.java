package com.github.knguyen.processors.geneva;

import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.StringUtils;

public class RSLCommand extends RunrepCommand {

    public RSLCommand(final ProcessContext context, final FlowFile flowfile) {
        super(context, flowfile);
    }

    @Override
    protected String getReportCommand() {
        // get and clean the RSL name
        final String rslNameProperty = context.getProperty(ExecuteGenevaRSL.RSL_NAME)
                .evaluateAttributeExpressions(flowfile).getValue();
        final String rslName = rslNameProperty.endsWith(".rsl")
                ? rslNameProperty.substring(0, rslNameProperty.length() - 4) : rslNameProperty;

        // format the temporary filename
        final String outputFilename = getOuputFilename();
        final String reportParameters = getReportParameters();

        if (StringUtils.isNotBlank(reportParameters)) {
            return String.format("read \"%s.rsl\"%nrunfile \"%s\" -f csv -o \"%s\" %s", rslName, rslName,
                    outputFilename, reportParameters);
        } else {
            return String.format("read \"%s.rsl\"%nrunfile \"%s\" -f csv -o \"%s\"", rslName, rslName, outputFilename);
        }
    }
}
