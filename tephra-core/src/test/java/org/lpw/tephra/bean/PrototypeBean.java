package org.lpw.tephra.bean;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.bean.prototype")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PrototypeBean {
}
