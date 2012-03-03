package org.easy.struts.spring;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.struts.action.Action;
import org.easy.struts.annotation.Config;
import org.easy.struts.annotation.StrutsAnnotationsPlugin;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringStrutsAnnotationsPlugin extends StrutsAnnotationsPlugin {

	@SuppressWarnings("unchecked")
	protected Collection<Class<? extends Action>> defineActionClasses(ServletContext context) {
		Collection<Class<? extends Action>> actionClasses = new ArrayList<Class<? extends Action>>();
		ConfigurableWebApplicationContext springContext = (ConfigurableWebApplicationContext)
				WebApplicationContextUtils.getWebApplicationContext(context);
		String[] names = springContext.getBeanNamesForType(Action.class);
		for (String name : names) {
			BeanDefinition def = springContext.getBeanFactory().getBeanDefinition(name);
			if (def instanceof AnnotatedBeanDefinition) {
				AnnotationMetadata meta = ((AnnotatedBeanDefinition) def).getMetadata();
				if (meta.hasAnnotation(Config.class.getName())) {
					Class<? extends Action> actionClass = springContext.getType(name);
					actionClasses.add(actionClass);
				}
			}
		}
		return actionClasses;
	}
}