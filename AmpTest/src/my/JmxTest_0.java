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
		//���MBeanServerʵ��
//      MBeanServer mbs = MBeanServerFactory.createMBeanServer();//������jconsole��ʹ��
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();//����jconsole��ʹ��
        //����MBean
        MyControllerMBean controller = new MyController();
        //��MBeanע�ᵽMBeanServer��
        mbs.registerMBean(controller, new ObjectName("MyappMBean:name=controller"));
       
        /*//�����������������ܹ�ͨ�����������MBean
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        adapter.setPort(2098);
        mbs.registerMBean(adapter, new ObjectName(
                    "MyappMBean:name=htmladapter,port=2098"));
        adapter.start();*/
        //while (true) {}
	}
}
