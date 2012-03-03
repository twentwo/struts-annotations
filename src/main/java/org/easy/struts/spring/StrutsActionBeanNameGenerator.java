package org.easy.struts.spring;

import java.text.MessageFormat;
import java.util.Map;

import org.easy.struts.annotation.Config;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Gives beans annotated with {@code StrutsAction} annotation name equal to
 * {@link Config#path()}, e.g. "/path/to/the/action"
 * 
 * @author npetkov
 */
public class StrutsActionBeanNameGenerator implements BeanNameGenerator {

	@Override
	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		if (!(definition instanceof AnnotatedBeanDefinition)) {
			throw new IllegalArgumentException(MessageFormat.format(
					"The bean of type [{0}] is not annotated with [{1}] annotation",
					definition.getBeanClassName(), Config.class.getName()));
		}

		AnnotationMetadata meta = ((AnnotatedBeanDefinition) definition).getMetadata();
		Map<String, Object> attributes = meta.getAnnotationAttributes(Config.class.getName());
		if (attributes == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					"The bean of type [{0}] is not annotated with [{1}] annotation",
					definition.getBeanClassName(), Config.class.getName()));
		}

		return (String) attributes.get("path");
	}
}