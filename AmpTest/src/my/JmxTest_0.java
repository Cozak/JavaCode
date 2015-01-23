package my;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
 
import com.sun.jdmk.comm.HtmlAdaptorServer;

public class JmxTest_0 {
	public static void main(String[] args) 
			throws InstanceAlreadyExistsException, 
			MBeanRegistrationException, NotCompliantMBeanException,
			MalformedObjectNameException {
		//获得MBeanServer实例
//      MBeanServer mbs = MBeanServerFactory.createMBeanServer();//不能在jconsole中使用
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();//可在jconsole中使用
        //创建MBean
        MyControllerMBean controller = new MyController();
        //将MBean注册到MBeanServer中
        mbs.registerMBean(controller, new ObjectName("MyappMBean:name=controller"));
       
        /*//创建适配器，用于能够通过浏览器访问MBean
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        adapter.setPort(2098);
        mbs.registerMBean(adapter, new ObjectName(
                    "MyappMBean:name=htmladapter,port=2098"));
        adapter.start();*/
        //while (true) {}
	}
}
