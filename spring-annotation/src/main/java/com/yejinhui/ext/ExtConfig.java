package com.yejinhui.ext;

import java.util.EventListener;
import java.util.concurrent.Executor;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

import com.yejinhui.bean.Blue;

/**
 * 扩展原理：
 * BeanPostProcessor:bean后置处理器，bean创建对象初始化前后进行拦截工作的
 * 
 * 1、BeanFactoryPostProcessor:beanFactory的后置处理器
 * 		在BeanFactory标准初始化之后调用，来定制和修改BeanFactory的内容；
 * 		所有的bean定义已经加载到beanFactory，但是bean的实例还未创建
 * 
 * BeanFactoryPostProcessor 原理：
 * 1）、ioc容器创建对象
 * 2）、invokeBeanFactoryPostProcessors（beanFactory）；执行BeanFactoryPostProcessor；
 * 		如何找到所有的BeanFactoryPostProcessor并执行他们的方法
 * 			a）、直接在BeanFactory中找到所有类型是BeanFactoryPostProcessor的组件，并执行他们的方法
 * 			b）、在初始化创建其他组件前面执行
 * 
 * 2、BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor 
 * 		postProcessBeanDefinitionRegistry();
 * 		在所有bean定义信息将要被加载，bean实例还未创建的
 * 
 * 		优先于BeanFactoryPostProcessor执行；
 * 		利用BeanDefinitionRegistryPostProcessor给容器中再额外添加一些组件
 * 
 * 
 * 原理：
 * 	1）、ioc创建对象
 * 	2）、refresh();->invokeBeanFactoryPostProcessors(beanFactory);
 * 	3）、从容器中获取所有的 BeanDefinitionRegistryPostProcessor 组件
 * 		a）、依次触发所有的postProcessBeanDefinitionRegistry()方法
 * 		b）、再来触发 BeanFactoryPostProcessor 中的方法postProcessBeanFactory()
 *  4）、再来从容器中找到 BeanFactoryPostProcessor 组件，然后依次触发 postProcessBeanFactory()方法
 * 
 *  3、ApplicationListener：监听容器中发生的事件。事件驱动模型开发
 *  	public interface ApplicationListener<E extends ApplicationEvent>
 *  	监听ApplicationEvent及其下面的子事件
 *  
 *  	步骤：
 *  		a）、写一个监听器（ApplicationListener实现类）来监听某个事件（ApplicationEvent及其子类）
 *  			@EventListener 
 *  			原理：使用 EventListenerMethodProcessor 处理器来解析方法上的 @EventListener
 *  			
 *  		b）、把监听器加入到容器
 *  		c）、只要容器中有相关事件的发布，我们就能监听到这个事件
 * 					ContextRefreshedEvent：容器刷新完成（所有bean都完全创建）会发布这个事件
 * 					ContextClosedEvent：关闭容器会发布这个事件
 * 			d）、发布一个事件
 * 	
 * 原理：
 * 	ContextRefreshedEvent、IOCTest_Ext$1[source=我发布的事件]、ContextClosedEvent
 * 	1）、ContextRefreshedEvent事件
 * 		a）、容器创建对象：refresh();
 * 		b）、finishRefresh();容器刷新完成会发布ContextRefreshedEvent事件
 * 	2）、自己发布事件
 * 	3）、容器关闭会发布ContextClosedEvent
 * 
 * 
 * 【事件的发布流程】：
 * 		c）、publishEvent(new ContextRefreshedEvent(this));
 * 				1）、获取事件的多播器(派发器)：getApplicationEventMulticaster()
 * 				2）、multicastEvent派发事件
 * 				3）、获取到所有的ApplicationListener
 * 					for (final ApplicationListener<?> listener : getApplicationListeners(event, type))
 * 					1）、如果有Executor，可以支持使用 Executor 异步派发
 * 						Executor executor = getTaskExecutor();
 * 					2）、否则，同步的方式直接执行listener方法；invokeListener(listener, event);
 * 						拿到Listener回调onApplicationEvent方法
 *  
 * 【事件多播器（派发器）】
 * 	  1）、容器创建对象：refresh();
 * 	  2）、initApplicationEventMulticaster();	初始化ApplicationEventMulticaster；
 * 		a）、先去容器中找有没有id=“applicationEventMulticaster”的组件
 * 		b）、如果没有this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
 * 			并且加入到容器中，我们就可以在其他组件要派发的事件，自动注入这个applicationEventMulticaster
 * 	  
 * 【容器中有那些监听器】
 * 	  1）、容器创建对象：refresh();
 * 	  2）、registerListeners();
 * 			//从容器中拿到所有的监听器，把他们注册到applicationEventMulticaster中
 * 			String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
 * 			//将listener注册到	ApplicationEventMulticaster
 * 			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
 * 
 * SmartInitializingSingleton原理：
 * 	1）、ioc容器创建对象并refresh()
 * 	2）、finishBeanFactoryInitialization(beanFactory);初始化剩下的单实例bean
 * 		a）、先创建所有的单实例bean，getBean()
 * 		b）、获取所有创建好的单实例bean，判断是否是SmartInitializingSingleton类型的
 * 			如果是就调用afterSingletonsInstantiated();
 * 
 * @author ye.jinhui
 * @date 2018年8月24日d
 */
@ComponentScan(value = "com.yejinhui.ext")
@Configuration
public class ExtConfig {

	@Bean
	public Blue blue() {
		return new Blue();
	}

}
