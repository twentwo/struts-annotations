package org.easy.struts.trace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.chain.commands.servlet.ExecuteAction;
import org.apache.struts.chain.contexts.ActionContext;

public class TracingExecuteAction extends ExecuteAction {
	public static final Log LOG = LogFactory.getLog(TracingExecuteAction.class);

	@Override
	public boolean execute(ActionContext actionContext) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Executing action: " + actionContext.getActionConfig());
		}
		return super.execute(actionContext);
	}

}
