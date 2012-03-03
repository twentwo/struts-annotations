package org.easy.struts.trace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.chain.commands.servlet.PerformForward;
import org.apache.struts.chain.contexts.ActionContext;

public class TracingPerformForward extends PerformForward {
	public static final Log LOG = LogFactory.getLog(TracingPerformForward.class);

	@Override
	public boolean execute(ActionContext actionContext) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Performing forward to: " + actionContext.getForwardConfig());
		}
		return super.execute(actionContext);
	}
}
