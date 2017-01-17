package org.lpw.tephra.script;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author lpw
 */
public class EngineTest extends CoreTestSupport {
    @Inject
    private Engine engine;

    @Test
    public void execute() throws IOException {
        Assert.assertEquals("message from javascript", engine.execute("engine_execute"));
    }
}
