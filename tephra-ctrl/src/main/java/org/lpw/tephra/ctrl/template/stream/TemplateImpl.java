package org.lpw.tephra.ctrl.template.stream;

import org.lpw.tephra.ctrl.Failure;
import org.lpw.tephra.ctrl.template.Template;
import org.lpw.tephra.ctrl.template.TemplateSupport;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Io;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.template.stream")
public class TemplateImpl extends TemplateSupport implements Template {
    @Inject
    private Io io;
    @Inject
    private Context context;
    @Value("${tephra.ctrl.template.stream.failure:failure.jpg}")
    private String failure;

    @Override
    public String getType() {
        return Templates.STREAM;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void process(String name, Object data, OutputStream outputStream) throws IOException {
        if (data instanceof Failure) {
            InputStream input = new FileInputStream(context.getAbsolutePath(failure));
            io.copy(input, outputStream);
            input.close();

            return;
        }

        outputStream.write((byte[]) data);
    }
}
