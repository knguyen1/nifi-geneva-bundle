package com.github.knguyen.processors.geneva;

import java.util.Collections;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.expression.ExpressionLanguageScope;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

public class ExecuteGenevaRSL extends BaseExecuteGeneva {

    static final PropertyDescriptor RSL_NAME = new PropertyDescriptor.Builder().name("rsl-name").displayName("RSL Name")
            .description(
                    "Specifies the RSL name.  The '.rsl' (dot rsl) extension is not necessary, e.g. `glmap_netassets` for Statement of Net Assets.")
            .expressionLanguageSupported(ExpressionLanguageScope.FLOWFILE_ATTRIBUTES).required(true)
            .addValidator(StandardValidators.NON_BLANK_VALIDATOR).build();

    @Override
    protected void init(final ProcessorInitializationContext context) {
        super.init(context);

        var commonDesc = commonDescriptors();
        commonDesc.add(RSL_NAME);
        descriptors = Collections.unmodifiableList(commonDesc);
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {

    }
}
