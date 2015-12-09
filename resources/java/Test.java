package integrator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private BundleContext m_context = null;

    public void start(BundleContext context) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Activator.start()");
        m_context = context;
    }
}